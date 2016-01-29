/*
 * ****************************************************************************
 *  Copyright Michael Lienhardt 2015
 *
 *  This file is part of IFDJTS (a type system for the IFDJ language).
 *
 *  IFDJTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  IFDJTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with IFDJTS.  If not, see <http://www.gnu.org/licenses/>.
 * ****************************************************************************
 */

package org.gzoumix.ts.ifdj.refactor;

import org.gzoumix.ts.ifdj.data.factory.DeltaOrderGraphFactory;
import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaActivation;
import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaOrdering;
import org.gzoumix.ts.ifdj.data.syntax.core.Attribute;
import org.gzoumix.ts.ifdj.data.syntax.core.Classs;
import org.gzoumix.ts.ifdj.data.syntax.core.Program;
import org.gzoumix.ts.ifdj.data.syntax.delta.*;
import org.gzoumix.ts.ifdj.data.syntax.formula.FormulaAnd;
import org.gzoumix.ts.ifdj.data.syntax.formula.FormulaNot;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormula;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormulaElement;
import org.gzoumix.ts.ifdj.util.Reference;
import org.gzoumix.util.graph.Edge;
import org.gzoumix.util.graph.Graph;
import org.gzoumix.util.graph.Vertex;
import org.gzoumix.util.graph.visitor.GraphTopologicalOrderFactory;

import java.util.*;

public class Monotonicity {

  public enum Operation { ADDS, REMOVES }

  public static void refactor(Program p, Operation op) {
    Monotonicity factory = new Monotonicity(p);
    if(op == Operation.ADDS) { factory.increasing(); }
    else {System.out.println("Decreasing not implemented yet!!");}
  }

  private Program program;
  private Graph<String, DeltaOrdering> deltaOrderGraph;
  private LinkedList<Vertex<String, DeltaOrdering>> to;
  private int pos;

  private Vertex<String, DeltaOrdering> v1, v2;
  private DeltaModule d1, d2;
  private IDeltaOperation abs1, abs2;
  private ListIterator<Vertex<String, DeltaOrdering>> itd1, itd2;
  private Set<IFormulaElement> s;

  private Monotonicity(Program program) { this.program = program; }


  /////////////////////////////////////////////////////////////////////////////
  // 1. Increasing Monotonic

  private void increasing() {
    // 1. create the data structure
    this.deltaOrderGraph = DeltaOrderGraphFactory.create(this.program);
    try {
      this.to = GraphTopologicalOrderFactory.create(this.deltaOrderGraph);
      Collections.reverse(this.to); // go from up to down
    } catch (GraphTopologicalOrderFactory.LoopException e) {
      e.printStackTrace();
      return;
    }

    this.pos = 0;
    this.itd1 = this.to.listIterator(pos);
    while(this.itd1.hasNext()) {
      this.v1 = this.itd1.next();
      pos++;
      this.d1 = this.program.getDelta(this.v1.getID());
      Iterator<IClassOperation> itcop = this.d1.getOperations().iterator();
      while(itcop.hasNext()) {
        IClassOperation cop = itcop.next();
        if(cop instanceof ClassRemoval) {
          itcop.remove();
          this.abs1 = cop;
          this.manageAddOperation();
        } else if(cop instanceof ClassModification) {
          Iterator<IAttributeOperation> itaop = ((ClassModification) cop).getOperations().iterator();
          while(itaop.hasNext()) {
            IAttributeOperation aop = itaop.next();
            if(aop instanceof AttributeRemoval) {
              itaop.remove();
              this.abs1 = aop;
              this.manageAddOperation();
            }
          }
        }
      }
    }
  }

  private void manageAddOperation() {
    this.s = new HashSet<>();
    this.itd2 = this.to.listIterator(pos);
    while(this.itd2.hasNext()) {
      this.v2 = this.itd2.next();
      this.d2 = this.program.getDelta(this.v2.getID());
      List<IClassOperation> cops = this.d2.getOperations();
      Iterator<IClassOperation> itcop = cops.iterator();
      while(itcop.hasNext()) {
        IClassOperation cop = itcop.next();
        if(this.abs1.getNameElement().leq(cop.getName())) {
          itcop.remove();
          if(cops.isEmpty()) { this.removeDelta(); }
          this.abs2 = cop;
          this.mergeAddOperations();
        } else if(cop instanceof ClassModification) {
          List<IAttributeOperation> aops = ((ClassModification) cop).getOperations();
          Iterator<IAttributeOperation> itaop = aops.iterator();
          while(itaop.hasNext()) {
            IAttributeOperation aop = itaop.next();
            if (this.abs1.getNameElement().leq(aop.getClassName(), aop.getName())) {
              itaop.remove();
              if(aops.isEmpty() && (((ClassModification) cop).getSuper() == null)) { this.removeDelta(); }
              this.abs2 = aop;
              this.mergeAddOperations();
            }
          }
        }
      }
      mergeRemoveToCore();
    }
  }

  private void mergeAddOperations() {
    // first delta
    FormulaAnd f1 = new FormulaAnd();
    f1.addAll(this.s);
    f1.add(this.d2.getID());
    f1.add(new FormulaNot(this.d1.getID()));
    this.addDelta(this.abs2, this.v2, this.itd2, f1);

    if(this.abs2.getOperation().equals(AbstractOperation.Operation.ADDS)
            && this.abs1.getNameElement().equals(this.abs2.getNameElement())) {
      this.s.add(this.d2.getID());
    }
  }

  private void mergeRemoveToCore() {
    AbstractOperation.NameElement el = this.abs1.getNameElement();
    IDeltaOperation abs = null;
    if(el.isClass()) {
      Classs c = this.program.removeClass(el.getNameClass());
      if(c != null) { abs = new ClassAddition(Reference.DUMMY_POSITION, c); }
    } else {
      Classs c = this.program.getClasss(el.getNameClass());
      Attribute att = null;
      if(c != null) { att = c.removeAttribute(el.getNameAttribute()); }
      if(att != null) {
        ClassModification tmp = new ClassModification(Reference.DUMMY_POSITION, c.getName());
        tmp.addOperation(new AttributeAddition(Reference.DUMMY_POSITION, att));
        abs = tmp;
      }
    }

    if(abs != null) {
      // define the formula
      FormulaAnd f = new FormulaAnd();
      f.add(this.d1.getID());
      for(IFormulaElement fel: this.s) { f.add(new FormulaNot(fel)); }
      this.addDelta(abs, this.v1, this.itd1, f);
    }
  }




  private void mergeRemoveOperations() {
    if(this.abs2.getOperation().equals(AbstractOperation.Operation.REMOVES)
            && this.abs1.getOperation().equals(AbstractOperation.Operation.ADDS)) {
      FormulaAnd f2 = new FormulaAnd();
      f2.addAll(this.s);
      f2.add(this.d2.getID());
      f2.add(this.d1.getID());
      IDeltaOperation abs = createModifyFrom(this.abs1);
      this.addDelta(abs, this.v1, this.itd1, f2);

    }
  }





  private IDeltaOperation createModifyFrom(IDeltaOperation abs) {
    if(abs instanceof AttributeAddition) {
      return new AttributeModification(Reference.DUMMY_POSITION, ((AttributeAddition) abs).getAttribute());
    } else if(abs instanceof ClassAddition) {
      return null; // should be "replace" that does not exist
    }
    return null;
  }




  private void addDelta(IDeltaOperation op, Vertex<String, DeltaOrdering> v, ListIterator<Vertex<String, DeltaOrdering>> it, IFormula cond) {
    DeltaModule origin = this.program.getDelta(v.getID());

    // 1. create the delta
    DeltaModule delta = new DeltaModule(Reference.DUMMY_POSITION, origin.getName() + "_" + op.getOperation().getName() + "_" + op.getNameElement().toString());
    if(op instanceof IClassOperation) {
      IClassOperation cop = (IClassOperation)op;
      cop.setDelta(origin.getID());
      delta.addOperation(cop);
    } else {
      IAttributeOperation aop = (IAttributeOperation) op;
      ClassModification abs = new ClassModification(Reference.DUMMY_POSITION, aop.getClassName());
      abs.addOperation(aop);
      abs.setDelta(origin.getID());
      delta.addOperation(abs);
    }

    program.addDeltaModule(delta);
    program.addDeltaActivation(new DeltaActivation(Reference.DUMMY_POSITION, delta.getName(), cond));
    Vertex<String, DeltaOrdering> vd = this.deltaOrderGraph.addVertex(delta.getName());
    it.add(vd);

    for(Edge<String, DeltaOrdering> prev: v.getPrevs()) {
      DeltaOrdering order = new DeltaOrdering(Reference.DUMMY_POSITION, prev.getStartID(), delta.getName());
      program.addDeltaOrdering(order);
      this.deltaOrderGraph.addEdge(order, prev.getStartID(), delta.getName());
    }
    for(Edge<String, DeltaOrdering> next: v.getNexts()) {
      DeltaOrdering order = new DeltaOrdering(Reference.DUMMY_POSITION, delta.getName(), next.getEndID());
      program.addDeltaOrdering(order);
      this.deltaOrderGraph.addEdge(order, delta.getName(), next.getEndID());
    }

  }

  private void removeDelta() { // from the list and from the program
    this.itd2.remove();
    this.program.removeDelta(this.d2.getName());
  }
}

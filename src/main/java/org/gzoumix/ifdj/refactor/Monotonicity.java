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

package org.gzoumix.ifdj.refactor;

import org.gzoumix.ifdj.lang.data.factory.DeltaOrderGraphFactory;
import org.gzoumix.ifdj.lang.syntax.ck.DeltaActivation;
import org.gzoumix.ifdj.lang.syntax.ck.DeltaOrdering;
import org.gzoumix.ifdj.lang.syntax.core.Attribute;
import org.gzoumix.ifdj.lang.syntax.core.Classs;
import org.gzoumix.ifdj.lang.syntax.core.Program;
import org.gzoumix.ifdj.lang.syntax.delta.*;
import org.gzoumix.ifdj.lang.syntax.formula.FormulaAnd;
import org.gzoumix.ifdj.lang.syntax.formula.FormulaNot;
import org.gzoumix.ifdj.lang.syntax.formula.IFormula;
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;
import org.gzoumix.ifdj.util.Reference;
import org.gzoumix.util.graph.Edge;
import org.gzoumix.util.graph.Graph;
import org.gzoumix.util.graph.Vertex;
import org.gzoumix.util.graph.visitor.GraphTopologicalOrderFactory;

import java.util.*;

public class Monotonicity {

  public enum Operation { ADDS, REMOVES }

  public static void refactor(Program p, Operation op) {
    Monotonicity factory = new Monotonicity(p);
    if(op == Operation.REMOVES) { factory.increasing(); }
    else { factory.decreasing(); }
  }

  private Program program;
  private Graph<String, DeltaOrdering> deltaOrderGraph;
  private LinkedList<Vertex<String, DeltaOrdering>> to;

  private Vertex<String, DeltaOrdering> v1, v2;
  private DeltaModule d1, d2;
  private IDeltaOperation abs1, abs2;
  private ListIterator<Vertex<String, DeltaOrdering>> itd2;

  private Monotonicity(Program program) { this.program = program; }


  /////////////////////////////////////////////////////////////////////////////
  // 1. Increasing Monotonic

  private Set<IFormulaElement> s;

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

    System.out.println("Topological Order = " + this.to);

    //this.pos = 0;
    while(!this.to.isEmpty()) {
      this.v1 = this.to.poll();
      //pos++;
      this.d1 = this.program.getDelta(this.v1.getID());
      Iterator<IClassOperation> itcop = this.d1.getOperations().iterator();
      while(itcop.hasNext()) {
        IClassOperation cop = itcop.next();
        if(cop instanceof ClassRemoval) {
          itcop.remove();
          this.abs1 = cop;
          this.manageRemoveOperation();
        } else if(cop instanceof ClassModification) {
          Iterator<IAttributeOperation> itaop = ((ClassModification) cop).getOperations().iterator();
          while(itaop.hasNext()) {
            IAttributeOperation aop = itaop.next();
            if(aop instanceof AttributeRemoval) {
              itaop.remove();
              this.abs1 = aop;
              this.manageRemoveOperation();
            }
          }
        }
      }
    }
  }

  private void manageRemoveOperation() {
    System.out.println("Managing delta \"" + this.d1.getName() + "\": operation = " + this.abs1.getOperation().getName() + " " + this.abs1.getNameElement());
    this.s = new HashSet<>();
    //this.itd2 = this.to.listIterator(pos);
    this.itd2 = this.to.listIterator(0);
    while(this.itd2.hasNext()) {
      this.v2 = this.itd2.next();
      this.d2 = this.program.getDelta(this.v2.getID());
      System.out.println("Checking delta \"" + this.d2.getName() + "\"");
      List<IClassOperation> cops = this.d2.getOperations();
      Iterator<IClassOperation> itcop = cops.iterator();
      while(itcop.hasNext()) {
        IClassOperation cop = itcop.next();
        System.out.println("  considering op=\"" + ABS(cop) + "\"");
        if(this.abs1.getNameElement().leq(cop.getName())) {
          itcop.remove();
          if(cops.isEmpty()) { this.removeDelta2(); }
          this.abs2 = cop;
          this.mergeIncreasingOperations();
        } else if(cop instanceof ClassModification) {
          List<IAttributeOperation> aops = ((ClassModification) cop).getOperations();
          Iterator<IAttributeOperation> itaop = aops.iterator();
          while(itaop.hasNext()) {
            IAttributeOperation aop = itaop.next();
            if (this.abs1.getNameElement().leq(aop.getClassName(), aop.getName())) {
              itaop.remove();
              if(aops.isEmpty() && (((ClassModification) cop).getSuper() == null)) { this.removeDelta2(); }
              this.abs2 = aop;
              this.mergeIncreasingOperations();
            }
          }
        }
      }
    }
    mergeRemoveToCore();
  }

  private void mergeIncreasingOperations() {
    System.out.println("  found opposite delta \"" + this.d2.getName() + "\": operation = " + this.abs2.getOperation().getName() + " " + this.abs2.getNameElement());
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
    System.out.println("Putting in Core delta \"" + this.d1.getName() + "\": operation = " + this.abs1.getOperation().getName() + " " + this.abs1.getNameElement());
    AbstractOperation.NameElement el = this.abs1.getNameElement();
    IDeltaOperation abs = null;
    if(el.isClass()) {
      Classs c = this.program.removeClass(el.getNameClass()); // apply the remove
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
      this.addDelta(abs, this.v1, null, f);
    }
  }



  /////////////////////////////////////////////////////////////////////////////
  // 1. Decreasing Monotonic

  //private int pos;

  private void decreasing() {
    // 1. create the data structure
    this.deltaOrderGraph = DeltaOrderGraphFactory.create(this.program);
    LinkedList<Vertex<String, DeltaOrdering>> to;
    try {
      to = GraphTopologicalOrderFactory.create(this.deltaOrderGraph);
    } catch (GraphTopologicalOrderFactory.LoopException e) {
      e.printStackTrace();
      return;
    }
    this.to = new LinkedList<>();


    //this.pos = this.to.size() + 1; // one before the current element
    while(!to.isEmpty()) {
      this.v1 = to.poll();
      //pos++;
      this.d1 = this.program.getDelta(this.v1.getID());
      Iterator<IClassOperation> itcop = this.d1.getOperations().iterator();
      while(itcop.hasNext()) {
        IClassOperation cop = itcop.next();
        if(cop instanceof ClassAddition) {
          itcop.remove();
          this.abs1 = cop;
          this.manageAddOperation();
        } else if(cop instanceof ClassModification) {
          Iterator<IAttributeOperation> itaop = ((ClassModification) cop).getOperations().iterator();
          while(itaop.hasNext()) {
            IAttributeOperation aop = itaop.next();
            if(aop instanceof AttributeAddition) {
              itaop.remove();
              this.abs1 = aop;
              this.manageAddOperation();
            }
          }
        }
      }
      this.to.addFirst(this.v1);
    }
  }

  private void manageAddOperation() {
    System.out.println("Managing delta \"" + this.d1.getName() + "\": operation = " + this.abs1.getOperation().getName() + " " + this.abs1.getNameElement());
    //this.itd2 = this.to.listIterator(this.pos);
    this.itd2 = this.to.listIterator(0);
    while(this.itd2.hasNext()) {
      this.v2 = this.itd2.next();
      this.d2 = this.program.getDelta(this.v2.getID());
      System.out.println("Checking delta \"" + this.d2.getName() + "\"");
      List<IClassOperation> cops = this.d2.getOperations();
      Iterator<IClassOperation> itcop = cops.iterator();
      while(itcop.hasNext()) {
        IClassOperation cop = itcop.next();
        System.out.println("  considering op=\"" + ABS(cop) + "\"");
        if(this.abs1.getNameElement().leq(cop.getName()) && (cop.getOperation() == AbstractOperation.Operation.REMOVES)) { // adds C vs
          itcop.remove();
          if(cops.isEmpty()) { this.removeDelta2(); }
          this.abs2 = cop;
          this.mergeIncreasingOperations();
        } else if((this.abs1.getNameElement().getNameClass().equals(cop.getName())) && (cop instanceof ClassModification)) {
          List<IAttributeOperation> aops = ((ClassModification) cop).getOperations();
          Iterator<IAttributeOperation> itaop = aops.iterator();
          while(itaop.hasNext()) {
            IAttributeOperation aop = itaop.next();
            if (aop.getName().equals(this.abs1.getNameElement().getNameAttribute()) && (aop.getOperation() == AbstractOperation.Operation.REMOVES)) {
              itaop.remove();
              if(aops.isEmpty() && (((ClassModification) cop).getSuper() == null)) { this.removeDelta2(); }
              this.abs2 = aop;
              this.mergeDecreasingOperations();
            }
          }
        }
      }
    }
    mergeAddToCore();
  }



  private void mergeDecreasingOperations() {
    System.out.println("  found opposite delta \"" + this.d2.getName() + "\": operation = " + this.abs2.getOperation().getName() + " " + this.abs2.getNameElement());
    // first delta
    FormulaAnd f1 = new FormulaAnd();
    f1.add(this.d2.getID());
    f1.add(new FormulaNot(this.d1.getID()));
    this.addDelta(this.abs2, this.v2, this.itd2, f1);
  }

  private void mergeAddToCore() {
    System.out.println("Putting in Core delta \"" + this.d1.getName() + "\": operation = " + this.abs1.getOperation().getName() + " " + this.abs1.getNameElement());
    IDeltaOperation absreplace = null;
    IDeltaOperation absremove = null;

    // 1. construction of the two delta, and add to the core
    if(this.abs1 instanceof AttributeAddition) {
      AttributeAddition tmp = (AttributeAddition) this.abs1;
      Classs c = this.program.getClasss(tmp.getClassName());
      if (c.getAttribute(tmp.getName()) != null) {
        ClassModification cop = new ClassModification(Reference.DUMMY_POSITION, tmp.getClassName());
        AttributeModification aop = new AttributeModification(Reference.DUMMY_POSITION, tmp.getAttribute());
        aop.setReplace(true);
        cop.addOperation(aop);
        absreplace = cop;
      } else {
        c.addAttribute(tmp.getAttribute());
        ClassModification cop = new ClassModification(Reference.DUMMY_POSITION, tmp.getClassName());
        AttributeRemoval aop = new AttributeRemoval(Reference.DUMMY_POSITION, tmp.getClassName(), tmp.getName());
        cop.addOperation(aop);
        absremove = cop;
      }
    } else {
      ClassAddition tmp = (ClassAddition) this.abs1;

      Classs c = this.program.getClasss(tmp.getName());
      if(c != null) {
        Collection<String> attsDelta = tmp.getClasss().getAttributeNames();
        Collection<String> attsProgram = c.getAttributeNames();
        Set<String> attsReplace = new HashSet<>(attsDelta);
        attsReplace.retainAll(attsProgram);
        Set<String> attsRemove = new HashSet<>(attsDelta);
        attsReplace.removeAll(attsProgram);

        if(!attsReplace.isEmpty()) {
          ClassModification cop = new ClassModification(Reference.DUMMY_POSITION, tmp.getName());
          for(String att: attsReplace) {
            AttributeModification aop = new AttributeModification(Reference.DUMMY_POSITION, tmp.getClasss().getAttribute(att));
            aop.setReplace(true);
            cop.addOperation(aop);
          }
          absreplace = cop;
        }

        if(!attsRemove.isEmpty()) {
          ClassModification cop = new ClassModification(Reference.DUMMY_POSITION, tmp.getName());
          for(String att: attsReplace) {
            AttributeRemoval aop = new AttributeRemoval(Reference.DUMMY_POSITION, tmp.getClasss().getName(), att);
            cop.addOperation(aop);
          }
          absremove = cop;
        }

      } else {
        this.program.addClass(tmp.getClasss());
        absremove = new ClassRemoval(Reference.DUMMY_POSITION, tmp.getName());
      }
    }

    // add the delta to the program
    if(absreplace != null ) { this.addDelta(absreplace, this.v1, null, this.d1.getID()); } // no need to add it to this.to, it does not interact with adds.
    if(absremove != null ) { this.addDelta(absremove, this.v1, this.itd2, new FormulaNot(this.d1.getID())); } // adds it to the end of this.to, the order does not matter
  }





  /////////////////////////////////////////////////////////////////////////////
  // 3. Utility Functions

  private void addDelta(IDeltaOperation op, Vertex<String, DeltaOrdering> v, ListIterator<Vertex<String, DeltaOrdering>> it, IFormula cond) {

    // 1. create the delta
    DeltaModule delta = new DeltaModule(Reference.DUMMY_POSITION, v.getID() + "_" + op.getOperation().getName() + "_" + op.getNameElement().toString());
    System.out.println("Adding delta \"" + delta.getName() + "\"");
    if(op instanceof IClassOperation) {
      IClassOperation cop = (IClassOperation)op;
      cop.setDelta(delta.getID());
      delta.addOperation(cop);
    } else {
      IAttributeOperation aop = (IAttributeOperation) op;
      ClassModification abs = new ClassModification(Reference.DUMMY_POSITION, aop.getClassName());
      abs.addOperation(aop);
      abs.setDelta(delta.getID());
      delta.addOperation(abs);
    }

    program.addDeltaModule(delta);
    program.addDeltaActivation(new DeltaActivation(Reference.DUMMY_POSITION, delta.getName(), cond));
    Vertex<String, DeltaOrdering> vd = this.deltaOrderGraph.addVertex(delta.getName());
    if(it != null) { it.add(vd); }

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

  private void removeDelta2() { // from the list and from the program
    System.out.println("Removing delta \"" + this.d2.getName() + "\"");
    this.itd2.remove();
    this.program.removeDelta(this.d2.getName());

    for(Edge<String, DeltaOrdering> prev: this.v2.getPrevs()) { this.program.removeOrdering(prev.getID()); }
    for(Edge<String, DeltaOrdering> next: v2.getNexts()) { this.program.removeOrdering(next.getID()); }

  }

  private static String ABS(IDeltaOperation op) { return op.getOperation() + " " + op.getNameElement(); }
}

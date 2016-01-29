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
import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaOrdering;
import org.gzoumix.ts.ifdj.data.syntax.core.Program;
import org.gzoumix.ts.ifdj.data.syntax.delta.*;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormulaElement;
import org.gzoumix.util.graph.Graph;
import org.gzoumix.util.graph.Vertex;
import org.gzoumix.util.graph.visitor.GraphTopologicalOrderFactory;

import java.util.*;

public class Monotonicity {

  public enum Operation { ADDS, REMOVES }

  public static void refactor(Program p, Operation op) {

  }

  private Program program;
  private Operation op;
  private Graph<String, DeltaOrdering> deltaOrderGraph;
  private LinkedList<Vertex<String, DeltaOrdering>> to;
  private DeltaModule d1;
  private int pos;
  private ListIterator<Vertex<String, DeltaOrdering>> itd1, itd2;

  private Monotonicity(Program program, Operation op) {
    this.program = program;
    this.op = op;
  }

  private void algorithm() {
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
      Vertex<String, DeltaOrdering> v1 = this.itd1.next();
      pos++;
      this.d1 = this.program.getDelta(v1.getID());
      Iterator<IClassOperation> itd1op = d1.getOperations().iterator();
      while(itd1op.hasNext()) {
        IClassOperation cop = itd1op.next();
        if(((this.op == Operation.ADDS) && (cop instanceof ClassAddition))
            || ((this.op == Operation.REMOVES) && (cop instanceof ClassRemoval))) {
          itd1op.remove();
          this.solveOperation(cop.getRepresentation().get(0)); // by construction, the operation on the class is always the first on the list
        } else if(cop instanceof ClassModification) {
          Iterator<IAttributeOperation> itd1cop = ((ClassModification) cop).getOperations().iterator();
          while(itd1cop.hasNext()) {
            IAttributeOperation aop = itd1cop.next();
            if(((this.op == Operation.ADDS) && (aop instanceof AttributeAddition))
                    || ((this.op == Operation.REMOVES) && (aop instanceof AttributeRemoval))) {
              itd1cop.remove();
              this.solveOperation(aop.getRepresentation());
            }
          }
        }
      }
    }
  }

  private void solveOperation(AbstractOperation aop) {
    Set<IFormulaElement> s = new HashSet<>();
    this.itd2 = this.to.listIterator(pos);

  }


}

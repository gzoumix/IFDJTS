package org.gzoumix.ts.ifdj.data.factory;
/******************************************************************************/
/* Copyright Michael Lienhardt 2015                                           */
/*                                                                            */
/* This file is part of IFDJTS (a type system for the IFDJ language).         */
/*                                                                            */
/* IFDJTS is free software: you can redistribute it and/or modify             */
/* it under the terms of the GNU General Public License as published by       */
/* the Free Software Foundation, either version 3 of the License, or          */
/* (at your option) any later version.                                        */
/*                                                                            */
/* IFDJTS is distributed in the hope that it will be useful,                  */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of             */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              */
/* GNU General Public License for more details.                               */
/*                                                                            */
/* You should have received a copy of the GNU General Public License          */
/* along with IFDJTS.  If not, see <http://www.gnu.org/licenses/>.            */
/******************************************************************************/

import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaOrdering;
import org.gzoumix.ts.ifdj.data.syntax.core.Program;
import org.gzoumix.ts.ifdj.data.syntax.visitor.VisitorBasic;
import org.gzoumix.util.graph.Graph;


public class DeltaOrderGraphFactory extends VisitorBasic {
  private Graph<String, DeltaOrdering> graph;

  public DeltaOrderGraphFactory() { }

  public Graph<String, DeltaOrdering> create(Program program) {
    Graph<String, DeltaOrdering> res = this.graph = new Graph<>();
    this.visit(program);
    this.graph = null;
    return res;
  }

  @Override
  public void visit(Program program) {
    for(DeltaOrdering deltaOrdering: program.getOrderings()) {
      deltaOrdering.accept(this);
    }
  }

  @Override
  public void visit(DeltaOrdering ordering) {
    String before = ordering.getBefore();
    String after = ordering.getAfter();
    this.graph.addVertice(before);
    this.graph.addVertice(after);
    this.graph.addEdge(ordering, before, after);
  }
}

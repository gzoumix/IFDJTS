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

import org.gzoumix.ts.ifdj.data.FCS;
import org.gzoumix.ts.ifdj.data.FCST;
import org.gzoumix.ts.ifdj.data.syntax.ISuperClassDeclaration;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormulaElement;
import org.gzoumix.util.Pair;
import org.gzoumix.util.graph.Edge;
import org.gzoumix.util.graph.Graph;
import org.gzoumix.util.graph.visitor.GraphVisitorBasic;

import java.util.Collection;

public class LookupFactory extends GraphVisitorBasic<String, Pair<IFormulaElement, ISuperClassDeclaration>> {

  public static FCST create(FCST base, Graph<String, Pair<IFormulaElement, ISuperClassDeclaration>> inheritanceGraph) {
    LookupFactory factory = new LookupFactory(base);
    inheritanceGraph.depthFirstSearch(factory);
    return factory.res;
  }

  // Implementation
  private FCST res;

  private LookupFactory(FCST base) {
    this.res = new FCST(base);
  }

  @Override
  public void leave(String s, Collection<Edge<String, Pair<IFormulaElement, ISuperClassDeclaration>>> nexts) {
    FCS fcs = this.res.get(s);
    for (Edge<String, Pair<IFormulaElement, ISuperClassDeclaration>> edge : nexts) {
      fcs.add(this.res.get(edge.getEnd()));
    }
  }
}

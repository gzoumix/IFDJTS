package org.gzoumix.ifdj.lang.data.factory;
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

import org.gzoumix.ifdj.lang.data.SPLS;
import org.gzoumix.ifdj.lang.syntax.ISuperClassDeclaration;
import org.gzoumix.ifdj.lang.syntax.core.Classs;
import org.gzoumix.ifdj.lang.syntax.core.Program;
import org.gzoumix.ifdj.lang.syntax.delta.ClassModification;
import org.gzoumix.ifdj.lang.syntax.delta.DeltaModule;
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;
import org.gzoumix.ifdj.lang.syntax.visitor.VisitorBasic;
import org.gzoumix.util.data.Pair;
import org.gzoumix.util.graph.Graph;


public class InheritanceGraphFactory extends VisitorBasic {

  public static Graph<String, Pair<IFormulaElement, ISuperClassDeclaration>> create(Program program) {
    InheritanceGraphFactory factory = new InheritanceGraphFactory();
    factory.visit(program);
    return factory.graph;
  }



  private Graph<String, Pair<IFormulaElement, ISuperClassDeclaration>> graph;
  private IFormulaElement deltaID;
  private InheritanceGraphFactory() {
    this.deltaID = null;
    this.graph = new Graph<>();
  }

  // What we are interested in are the class declarations and modifications

  @Override
  public void visit(DeltaModule delta) {
    this.deltaID = delta.getID();
    super.visit(delta);
    this.deltaID = null;
  }

  @Override
  public void visit(Classs classs) {
    String base = classs.getBaseClass();
    String sup = classs.getSuperClass();
    IFormulaElement d = this.getDeltaID();
    this.graph.addVertex(base);
    this.graph.addVertex(sup);
    this.graph.addEdge(new Pair<IFormulaElement, ISuperClassDeclaration>(d, classs), base, sup);
  }

  @Override
  public void visit(ClassModification op) {
    String base = op.getBaseClass();
    String sup = op.getSuperClass();
    IFormulaElement d = this.getDeltaID();
    this.graph.addVertex(base);
    if(sup != null) {
      this.graph.addVertex(sup);
      this.graph.addEdge(new Pair<IFormulaElement, ISuperClassDeclaration>(d, op), base, sup);
    }
  }

  private IFormulaElement getDeltaID() {
    return this.deltaID == null ? SPLS.DELTA_CORE : this.deltaID;
  }
}

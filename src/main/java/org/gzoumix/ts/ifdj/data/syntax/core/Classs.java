package org.gzoumix.ts.ifdj.data.syntax.core;
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
import org.gzoumix.ts.ifdj.data.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ts.ifdj.data.syntax.IASTNode;
import org.gzoumix.ts.ifdj.data.syntax.ISuperClassDeclaration;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormulaElement;
import org.gzoumix.ts.ifdj.data.syntax.visitor.IVisitor;
import org.gzoumix.util.syntax.Position;

import java.util.List;
import java.util.Vector;


public class Classs  extends ASTNodeCommonFunctionalities<IASTNode> implements ISuperClassDeclaration<IASTNode> {
  private IFormulaElement delta;
  private String name;
  private List<Attribute> atts;
  private String superClass;

  public Classs(Position pos, IFormulaElement delta, String name, String superClass) {
    super(pos);
    this.delta = delta;
    this.name = name;
    this.superClass = superClass;
    this.atts = new Vector<>();
  }

  public String getName() { return this.name; }
  public String getSuper() { return this.superClass; }

  public boolean addAttribute(Attribute att) {
    boolean res = this.atts.add(att);
    if(res) { att.setFather(this); }
    return res;
  }
  public List<Attribute> getAttributes() { return this.atts; }



  public FCS getFCS() {
    FCS res = new FCS(this.getName());
    res.addOrigin(this);
    for(Attribute att: this.atts) { res.addAttribute(att); }
    return res;
  }

  @Override
  public IFormulaElement getDelta() { return this.delta; }

  @Override
  public String getBaseClass() { return this.getName();
  }

  @Override
  public String getSuperClass() { return this.getSuper(); }


  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }
}

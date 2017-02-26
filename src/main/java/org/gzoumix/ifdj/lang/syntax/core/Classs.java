package org.gzoumix.ifdj.lang.syntax.core;
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

import org.gzoumix.ifdj.lang.data.FCS;
import org.gzoumix.ifdj.lang.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ifdj.lang.syntax.IASTNode;
import org.gzoumix.ifdj.lang.syntax.ISuperClassDeclaration;
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.util.syntax.Position;

import java.util.*;


public class Classs  extends ASTNodeCommonFunctionalities<IASTNode> implements ISuperClassDeclaration<IASTNode> {
  private IFormulaElement delta;
  private String name;
  private Map<String,Attribute> atts;
  private String superClass;

  public Classs(Position pos, IFormulaElement delta, String name, String superClass) {
    super(pos);
    this.delta = delta;
    this.name = name;
    this.superClass = superClass;
    this.atts = new HashMap<>();
  }

  public String getName() { return this.name; }
  public String getSuper() { return this.superClass; }

  public void setDelta(IFormulaElement delta) { this.delta = delta; }
  public void addAttribute(Attribute att) {
    this.atts.put(att.getName(), att);
    att.setFather(this);
  }
  public Collection<Attribute> getAttributes() { return this.atts.values(); }
  public Collection<String> getAttributeNames() { return this.atts.keySet(); }
  public Attribute getAttribute(String name) { return this.atts.get(name); }
  public Attribute removeAttribute(String name) { return this.atts.remove(name); }



  public FCS getFCS() {
    FCS res = new FCS(this.getName());
    res.addOrigin(this);
    for(Attribute att: this.getAttributes()) { res.addAttribute(att); }
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

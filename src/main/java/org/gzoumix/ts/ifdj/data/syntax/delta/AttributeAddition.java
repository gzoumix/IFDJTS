package org.gzoumix.ts.ifdj.data.syntax.delta;
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

import org.gzoumix.ts.ifdj.data.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ts.ifdj.data.syntax.visitor.IVisitor;
import org.gzoumix.ts.ifdj.data.syntax.core.Attribute;
import org.gzoumix.util.syntax.Position;


public class AttributeAddition extends ASTNodeCommonFunctionalities<ClassModification> implements IAttributeOperation {
  private Attribute att;
  private AbstractOperation op;

  public AttributeAddition(Position pos, Attribute att) {
    super(pos);
    this.att = att;
    this.op = AbstractOperation.adds(this.att.getClassName(), this.att.getName(), this);
    att.setFather(this);
  }

  public Attribute getAttribute() { return this.att; }

  @Override
  public String getClassName() { return this.att.getClassName(); }

  @Override
  public String getName() { return this.att.getName(); }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }


  @Override
  public AbstractOperation getRepresentation() { return this.op; }

  @Override
  public AbstractOperation.Operation getOperation() { return this.getRepresentation().getOp(); }

  @Override
  public AbstractOperation.NameElement getNameElement() { return this.getRepresentation().getEl(); }

}

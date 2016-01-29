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
import org.gzoumix.util.syntax.Position;


public class AttributeRemoval extends ASTNodeCommonFunctionalities<ClassModification> implements IAttributeOperation {
  private String cl;
  private String name;

  public AttributeRemoval(Position pos, String cl, String name) {
    super(pos);
    this.cl = cl;
    this.name = name;
  }

  public String getName() { return this.name; }
  public String getCl() { return cl; }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

  @Override
  public AbstractOperation getRepresentation() {
    return AbstractOperation.removes(this.cl, this.name, this);
  }
}


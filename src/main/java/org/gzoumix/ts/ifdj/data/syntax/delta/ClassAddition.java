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

import org.gzoumix.ts.ifdj.data.FCS;
import org.gzoumix.ts.ifdj.data.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ts.ifdj.data.syntax.core.Attribute;
import org.gzoumix.ts.ifdj.data.syntax.visitor.IVisitor;
import org.gzoumix.ts.ifdj.data.syntax.core.Classs;
import org.gzoumix.ts.ifdj.util.Reference;
import org.gzoumix.util.syntax.Position;

import java.util.LinkedList;
import java.util.List;

public class ClassAddition extends ASTNodeCommonFunctionalities<DeltaModule> implements IClassOperation {
  private Classs c;

  public ClassAddition(Position pos, Classs c) {
    super(pos);
    this.c = c;
    c.setFather(this);
  }

  public Classs getClasss() { return this.c; }

  // implementation of IClassOperation
  @Override
  public String getName() { return this.c.getName(); }

  @Override
  public FCS getFCS() { return this.c.getFCS(); }

  @Override
  public List<AbstractOperation> getRepresentation() {
    List<AbstractOperation> res =  new LinkedList<>();
    res.add(AbstractOperation.adds(this.c.getName(), this));
    for(Attribute att: c.getAttributes()) {
      res.add(AbstractOperation.adds(this.c.getName(), att.getName(), new AttributeAddition(Reference.DUMMY_POSITION, att)));
    }
    return res;
  }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

}

package org.gzoumix.ifdj.lang.syntax.delta;
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
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.util.syntax.Position;

import java.util.LinkedList;
import java.util.List;

public class ClassRemoval extends ASTNodeCommonFunctionalities<DeltaModule> implements IClassOperation, IASTNode<DeltaModule> {
  private String name;
  private FCS fcs;
  private AbstractOperation op;
  private IFormulaElement delta;

  public ClassRemoval(Position pos, String name) {
    super(pos);
    this.name = name;
    this.fcs = new FCS(this.name);
    this.op = AbstractOperation.removes(this.getName(), this);
  }

  @Override
  public void setDelta(IFormulaElement delta) { this.delta = delta; }

  // implementation of IClassOperation
  @Override
  public String getName() { return this.name; }

  @Override
  public FCS getFCS() { return this.fcs; }

  @Override
  public List<AbstractOperation> getFullRepresentation() {
    List<AbstractOperation> res = new LinkedList<>();
    res.add(this.op);
    return res;
  }

  @Override
  public AbstractOperation getRepresentation() { return this.op; }

  @Override
  public AbstractOperation.Operation getOperation() { return this.getRepresentation().getOp(); }

  @Override
  public AbstractOperation.NameElement getNameElement() { return this.getRepresentation().getEl(); }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }


}

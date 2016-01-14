package org.gzoumix.ts.ifdj.data.syntax;
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

import org.gzoumix.ts.ifdj.data.syntax.formula.IFormula;
import org.gzoumix.util.syntax.Position;


public abstract class ASTNodeCommonFunctionalities<N extends IASTNode> implements IASTNode<N> {
  protected N father;
  Position pos;

  protected String type;
  protected IFormula constraint;

  protected ASTNodeCommonFunctionalities(Position pos) { this.pos = pos; }

  @Override
  public void setType(String type) { this.type = type; }
  @Override
  public String getType() { return this.type; }

  @Override
  public void setDependencyConstraint(IFormula constraint) { this.constraint = constraint; }
  @Override
  public IFormula getDependencyConstraint() { return this.constraint; }

  @Override
  public N getFather() { return this.father; }

  @Override
  public void setFather(N father) { this.father = father;  }

  @Override
  public Position getPosition() { return this.pos; }
}

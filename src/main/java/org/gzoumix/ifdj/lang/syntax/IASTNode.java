package org.gzoumix.ifdj.lang.syntax;/******************************************************************************/
/* Copyright Gzoumix 2015                                                     */
/*                                                                            */
/* This file is part of Gzoumcraft (a minecraft mod).                         */
/*                                                                            */
/* Gzoumcraft is free software: you can redistribute it and/or modify         */
/* it under the terms of the GNU General Public License as published by       */
/* the Free Software Foundation, either version 3 of the License, or          */
/* (at your option) any later version.                                        */
/*                                                                            */
/* Gzoumcraft is distributed in the hope that it will be useful,              */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of             */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              */
/* GNU General Public License for more details.                               */
/*                                                                            */
/* You should have received a copy of the GNU General Public License          */
/* along with Gzoumcraft.  If not, see <http://www.gnu.org/licenses/>.        */
/******************************************************************************/

import org.gzoumix.ifdj.lang.syntax.formula.IFormula;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.util.syntax.Position;



public interface IASTNode<F extends IASTNode> {
  F getFather();
  void setFather(F father);
  Position getPosition();

  void setType(String type);
  String getType();

  void setDependencyConstraint(IFormula constraint);
  IFormula getDependencyConstraint();

  void accept(IVisitor visitor);
}

package org.gzoumix.ts.ifdj.data.syntax.formula;/******************************************************************************/
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

import org.gzoumix.ts.ifdj.data.syntax.IASTNode;

import java.util.Collection;

public interface IFormula extends IASTNode<IASTNode> {
  public boolean eval(Collection<FormulaPredicate> truePredicates);
  public IFormula simplify();
}

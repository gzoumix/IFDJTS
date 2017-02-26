/******************************************************************************/
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
package org.gzoumix.ifdj.lang.syntax.delta;

import org.gzoumix.ifdj.lang.data.FCS;
import org.gzoumix.ifdj.lang.syntax.IASTNode;
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;

import java.util.List;


public interface IClassOperation extends IASTNode<DeltaModule>, IDeltaOperation {
  void setDelta(IFormulaElement delta);
  String getName();
  FCS getFCS();
  List<AbstractOperation> getFullRepresentation();
}

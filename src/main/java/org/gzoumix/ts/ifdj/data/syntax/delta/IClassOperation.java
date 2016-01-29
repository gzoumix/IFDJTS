package org.gzoumix.ts.ifdj.data.syntax.delta;/******************************************************************************/
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

import org.gzoumix.ts.ifdj.data.FCS;
import org.gzoumix.ts.ifdj.data.syntax.IASTNode;

import java.util.List;


public interface IClassOperation extends IASTNode<DeltaModule> {
  String getName();
  FCS getFCS();
  List<AbstractOperation> getRepresentation();
}

package org.gzoumix.util.graph;
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

import org.gzoumix.util.ImmutableCollection;

public class Component<V> extends ImmutableCollection<V> {
  private boolean hasLoop;

  public Component() {
    super();
  }

  public Component(V element) {
    super();
    this.content.add(element);
  }

  void addVertex(V v) {
    this.content.add(v);
    if (this.size() > 1) {
      this.hasLoop = true;
    }
  }

  public boolean hasLoop() {
    return this.hasLoop;
  }

  public void setLoop(boolean b) {
    this.hasLoop |= b;
  }
}

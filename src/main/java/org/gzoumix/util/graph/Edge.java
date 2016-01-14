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

import org.gzoumix.util.Pair;

public class Edge<V, E> {
  private E id;
  private Vertex<V, E> start;
  private Vertex<V, E> end;

  Edge(E id, Vertex<V, E> start, Vertex<V, E> end) {
    this.id = id;
    this.start = start;
    this.end = end;
  }

  public E getID() {
    return id;
  }

  public V getStart() {
    return this.getVertexStart().getID();
  }

  public V getEnd() {
    return this.getVertexEnd().getID();
  }

  Vertex<V, E> getVertexStart() {
    return this.start;
  }

  Vertex<V, E> getVertexEnd() {
    return this.end;
  }


  @Override
  public String toString() {
    return (new Pair<>(this.getID(), this.getVertexEnd())).toString();
  }
}

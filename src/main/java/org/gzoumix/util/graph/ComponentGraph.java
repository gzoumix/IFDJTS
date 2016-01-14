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


public class ComponentGraph<V> extends Graph<Component<V>, Object> {

  public Graph<V, Object> flatten() {
    Graph<V, Object> res = new Graph<>();

    // 1. define the vertices of the graph
    for (Component<V> comp : this.getVertices()) {
      for (V v : comp) {
        res.addVertice(v);
      }
    }

    // 2. generate the edges form within the Components
    for (Component<V> comp : this.getVertices()) {
      if (comp.hasLoop()) {
        for (V v : comp) {
          for (V w : comp) {
            if(!v.equals(w)) { res.addEdge(null, v, w); }
          }
        }
      }
    }

    // 3. generate the edges between the Components
    for (Component<V> comp1 : this.getVertices()) {
      for (Edge<Component<V>, Object> e : this.getNexts(comp1)) {
        for (V v : comp1) {
          for (V w : e.getEnd()) {
            res.addEdge(null, v, w);
          }
        }
      }
    }
    return res;
  }
}

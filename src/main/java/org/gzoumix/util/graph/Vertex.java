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
/******************************************************************************/ /////////////////////////////////////////////

import java.util.List;
import java.util.Vector;

class Vertex<V, E> {
  // 1.1. Basic Vertice Structure
  private V id;
  private List<Edge<V, E>> nexts;

  public Vertex(V info) {
    this.id = info;
    this.nexts = new Vector<>();
    this.resetVisited();
  }

  public void addNext(Edge<V, E> edge) {
    this.nexts.add(edge);
  }

  public V getID() {
    return this.id;
  }

  public List<Edge<V, E>> getNexts() {
    return this.nexts;
  }

  // 1.2. Component Computation Structures

  private Vertex<V, E> root;         // the root of the component
  private Component<V> component; // the component
  private int visitedID;            // when this vertice is first setVisitedID
  private int csaved;               // the state of the cstack when setVisitedID

  public Vertex<V, E> getRoot() {
    return root;
  }

  public void setRoot(Vertex<V, E> root) {
    this.root = root;
  }

  public int getCSaved() {
    return this.csaved;
  }
  public void setCSaved(int csaved) {
    this.csaved = csaved;
  }

  public Component<V> getComponent() {
    return component;
  }

  public void setComponent(Component<V> component) {
    this.component = component;
  }

  public void setVisitedID(int i) {
    this.visitedID = i;
  }

  public int getVisitedID() {
    return this.visitedID;
  }

  public boolean isVisited() {
    return this.visitedID != Graph.VERTICE_NOT_VISITED_ID;
  }

  public void resetVisited() {
    this.visitedID = Graph.VERTICE_NOT_VISITED_ID;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Vertex) {
      return this.id.equals(((Vertex) o).getID());
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return this.id.toString();
  }
}

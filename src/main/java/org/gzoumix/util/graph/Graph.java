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

import org.gzoumix.util.graph.visitor.IGraphVisitor;

import java.util.*;

// Simple implementation of a graph class, to perform transitive closure computation
// V is a class for unique identifiers (w.r.t. hashCode and equals) for vertices
public class Graph<V, E> {
  public Map<V, Vertex<V,E>> vertices;

  public Graph() { vertices = new HashMap<>(); }

  public Vertex<V,E> addVertice(V id) {
    Vertex<V,E> res = this.vertices.get(id);
    if(res != null) { return res; }
    else {
      return this.vertices.put(id, new Vertex<V, E>(id));
    }
  }

  public boolean addEdge(E id, V start, V end) {
    Vertex<V,E> startV = this.vertices.get(start);
    Vertex<V,E> endV = this.vertices.get(end);
    if((startV != null) && (endV != null)) {
      startV.addNext(new Edge<V, E>(id,startV,endV));
      return true;
    } else { return false; }
  }

  public Set<V> getVertices() { return this.vertices.keySet(); }
  public Collection<Edge<V,E>> getNexts(V id) {
    Vertex<V,E> v = this.vertices.get(id);
    if(v == null) { return null; }
    else { return v.getNexts(); }
  }

  @Override
  public String toString() {
    String res = "";
    Iterator<Vertex<V,E>> i = this.vertices.values().iterator();
    while(i.hasNext()) {
      Vertex<V,E> v = i.next();
      res += "  " + v.toString() + " -> " + v.getNexts().toString();
      if(i.hasNext()) { res += "\n"; }
    }
    return res;
  }


  /////////////////////////////////////////////
  // 4. Search algorithms

  static final int VERTICE_NOT_VISITED_ID = -1;        // to remember the order in which vertices have been setVisitedID
  private int verticeVisitID;

  private void resetVisited() {
    for(Vertex<V,E> v: this.vertices.values()) { v.setVisitedID(VERTICE_NOT_VISITED_ID); }
    this.verticeVisitID = VERTICE_NOT_VISITED_ID;
  }

  public void depthFirstSearch(IGraphVisitor<V,E> visitor) {
    this.resetVisited();

    for(Vertex<V,E> v: this.vertices.values()) {
      if(v.getVisitedID() == VERTICE_NOT_VISITED_ID) {
        this.visitDepthFirst(visitor, v);
      }
    }
  }

  private void visitDepthFirst(IGraphVisitor<V,E> visitor, Vertex<V,E> v) {
    visitor.enter(v.getID(), v.getNexts());
    v.setVisitedID(++this.verticeVisitID);

    for(Edge<V,E> e: v.getNexts()) {                                   // for each vertex w such that (v; w) in E do begin
      Vertex<V, E> w = e.getVertexEnd();
      if (w.getVisitedID() == VERTICE_NOT_VISITED_ID) {
        visitDepthFirst(visitor, w);
      }
    }
    visitor.leave(v.getID(), v.getNexts());
  }


  /////////////////////////////////////////////
  // 5. Transitive closure algorithm. TODO: use a factory with the depthFirstSearch visitor
  public ComponentGraph<V> transitiveClosure() {
    // 1. Initialize Data and Construction of the Component Graph
    this.resetVisited();
    this.vstack = new Stack<>();        // vstack := emptyset;
    this.cstack = new Stack<>();        // cstack := emptyset;
    this.cgraph = new ComponentGraph<>();

    for(Vertex<V,E> v: this.vertices.values()) {  // for each vertex v in V do
      if(!v.isVisited()) {               // if v is not already setVisitedID then compTC(v)
        this.transitiveClosure(v);
      }
    }

    // 2. reset Structures
    ComponentGraph<V> res = this.cgraph;
    this.cgraph = null;

    return res;

    // 2. Translation of the Component Graph
  }

  private Stack<Vertex<V,E>> vstack;                          // the stack of parsed vertice
  private Stack<Component<V>> cstack;                        // the stack of discovered components
  private ComponentGraph<V> cgraph;


  private void transitiveClosure(Vertex<V,E> v) {
    boolean notForward = false;
    boolean hasSelfLoop = false;
    v.setVisitedID(++this.verticeVisitID);
    v.setRoot(v);                                       // root(v) := v;
    //v.setComponent(null);                             // C(v) := Nil;
    v.setCSaved(this.cstack.size());                    // csaved(v) := height(cstack);
    this.vstack.push(v);                                // push(v; vstack);

    //System.out.println("Visiting Vertice \"" + v.toString() + "\" : (setVisitedID=" + v.getVisitedID() + ", csaved=" + v.getCSaved() + ")");


    for(Edge<V,E> e: v.getNexts()) {                                   // for each vertex w such that (v; w) in E do begin
      Vertex<V,E> w = e.getVertexEnd();
      if(w.equals(v)) { hasSelfLoop = true; }
      if(!w.isVisited()) { transitiveClosure(w); notForward = true; }                     // depth-first search
      else if(w.getVisitedID() < v.getVisitedID()) { notForward = true; }

      if(w.getComponent() == null) { // found a loop
        // update the root when necessary
        if(w.getRoot().getVisitedID() < v.getRoot().getVisitedID()) {
          v.setRoot(w.getRoot());
        }
      } else if(notForward) {  // else if (v; w) is not a forward edge then push(C(w); cstack);
        // the component w.getComponent() is accessible from v
        //System.out.println("  " + v.toString() + ": pushing Component of " + w.toString());
        cstack.push(w.getComponent()); // Collect all the edge between components
      }
    }

    //if(v.getRoot().equals(v)) { System.out.println(" " + v.toString() + ": the root is still "  + v.toString()); }
    //else { System.out.println(" " + v.toString() + ": the root is now \"" + v.getRoot().toString() + "\""); }

    if(v.getRoot().equals(v)) {

      //System.out.print(" => creating a Component for vertex " + v.toString());

      Component<V> C = new Component<>();

      // Set up the the Component in the graph
      Vertex<V,E> w;
      do {                      // repeat
        w = this.vstack.pop();  // w := pop(vstack)
        w.setComponent(C);      // C(w) := C;
        C.addVertex(w.getID()); // insert w into component C;
      } while(!w.equals(v));
      //System.out.println(" = " + C.toString());

      List<Component<V>> nexts = new Vector<>();

      //System.out.print("  nexts = ");
      // Constructing the set of next Components
      while(v.getCSaved() != this.cstack.size()) {      // while height(cstack) != csaved(v) do begin
        Component<V> X = this.cstack.pop();             // X := pop(cstack);
        if(!nexts.contains(X)) {                        // if X not in Succ(C) then
          nexts.add(X);                                 // Succ(C) := Succ(C) cup {X} cup Succ(X);
          //System.out.println(X.iterator().next() + ", ");
          for(Edge<Component<V>, Object> e: cgraph.getNexts(X)) {
            //System.out.println(e.getEnd().iterator().next() + ", ");
            nexts.add(e.getEnd());
          }
        }
      }

      // set up the graph
      cgraph.addVertice(C);
      for(Component<V> X: nexts) { cgraph.addEdge(null, C, X); }
    }
  }


  /// TODO: refactor everything to get a fully functional implementation of this class
  private static class TransitiveClosureFactory<V,E> implements IGraphVisitor<V,E> {

    public static <V,E> ComponentGraph<V> create(Graph<V,E> graph) {
      TransitiveClosureFactory<V,E> factory = new TransitiveClosureFactory<>();
      graph.depthFirstSearch(factory);
      return factory.cgraph;
    }

    private ComponentGraph<V> cgraph;
    private Stack<Vertex<V,E>> vstack;                          // the stack of parsed vertice
    private Stack<Component<V>> cstack;                        // the stack of discovered components


    @Override
    public void enter(V v, Collection<Edge<V, E>> nexts) {
     }

    @Override
    public void leave(V v, Collection<Edge<V, E>> nexts) {

    }
  }


}

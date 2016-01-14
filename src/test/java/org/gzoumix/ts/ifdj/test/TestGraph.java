package org.gzoumix.ts.ifdj.test;
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

import org.gzoumix.util.graph.ComponentGraph;
import org.gzoumix.util.graph.Graph;
import org.junit.Test;


public class TestGraph {

  @Test
  public void testGraphBasicOperations() {
    System.out.println("Starting Testing on graph");

    Graph<String, String> graph = new Graph<>();

    System.out.println("Adding v1-8 to graph");
    for(int i = 1; i < 9; i++) { graph.addVertice("v" + i); }

    System.out.println("Adding edges");
    graph.addEdge("e01", "v1", "v2");
    graph.addEdge("e02", "v2", "v3");
    graph.addEdge("e03", "v3", "v4");
    graph.addEdge("e04", "v3", "v5");
    graph.addEdge("e05", "v4", "v2");
    graph.addEdge("e06", "v2", "v6");
    graph.addEdge("e07", "v6", "v7");
    graph.addEdge("e08", "v7", "v2");
    graph.addEdge("e09", "v8", "v7");

    System.out.println("Graph =\n" + graph.toString());

    System.out.println("\nComputing the transitive closure of the graph");
    ComponentGraph<String> tgraph = graph.transitiveClosure();
    System.out.println("TGraph =\n" + tgraph.toString());
    System.out.println("Flattened TGraph =\n" + tgraph.flatten().toString());
  }
}

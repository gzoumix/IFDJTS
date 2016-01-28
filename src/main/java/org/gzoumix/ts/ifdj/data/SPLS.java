package org.gzoumix.ts.ifdj.data;
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

import org.gzoumix.ts.ifdj.data.factory.*;
import org.gzoumix.ts.ifdj.data.syntax.ISuperClassDeclaration;
import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaOrdering;
import org.gzoumix.ts.ifdj.data.syntax.core.Program;
import org.gzoumix.ts.ifdj.data.syntax.delta.DeltaModule;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormulaElement;
import org.gzoumix.ts.ifdj.util.Reference;
import org.gzoumix.util.data.HashMapSet;
import org.gzoumix.util.data.Pair;
import org.gzoumix.util.graph.*;
import org.gzoumix.util.graph.visitor.GraphTransitiveClosureFactory;
import org.gzoumix.util.graph.visitor.GraphVisitorDepthSearch;

import java.util.*;


public class SPLS {

  public static final IFormulaElement DELTA_CORE = Reference.FORMULA_TRUE;

  private Program program;
  private Map<String, DeltaModule> deltaModules;

  // Base Information
  private FCST fcst;
  private FCST lookup;

  private Graph<String, Pair<IFormulaElement, ISuperClassDeclaration>> inheritanceGraph;
  private ComponentGraph<String, Pair<IFormulaElement, ISuperClassDeclaration>> subtype;


  private DMST dmst;
  private Graph<String, DeltaOrdering> deltaOrderGraph;
  private ComponentGraph<String, DeltaOrdering> deltaOrderComponentGraph;
  private Graph<String, List<Edge<String,DeltaOrdering>>> deltaAfterGraph;

  // Additional Informations
  private Set<String> classes;
  private Set<IFormulaElement> deltas;
  private HashMapSet<IFormulaElement, IFormulaElement> deltaAfter;
  private HashMapSet<IFormulaElement, IFormulaElement> deltaBefore;
  private Map<String, HashMapSet<String,Set<ISuperClassDeclaration>>> inh;

  private static final Set<IFormulaElement> EMTPYSET = new HashSet<>();



  public SPLS(Program program) {
    this.program = program;

    // TODO: here check that elements in program are declared only once
    // TODO: check also that all used elements (like features, delta module names) are declared

    this.deltaModules = new HashMap<>();
    for(DeltaModule delta: this.program.getDeltas()) {
      this.deltaModules.put(delta.getName(), delta);
    }

    this.fcst = FCSTFactory.create(program);

    // complete the FCST with the standard library
    for(FCS fcs: Reference.classes.keySet()) {
      this.fcst.addFCS(fcs);
    }

    this.inheritanceGraph = InheritanceGraphFactory.create(program);

    // complete the inheritance graph with standard library (except the null type, which is added later)
    for(Collection<ISuperClassDeclaration> sdecls: Reference.classes.values()) {
      for(ISuperClassDeclaration sdecl: sdecls) {
        String baseClass = sdecl.getBaseClass();
        String superClass = sdecl.getSuperClass();

        this.inheritanceGraph.addVertex(baseClass);
        this.inheritanceGraph.addVertex(superClass);
        this.inheritanceGraph.addEdge(new Pair<>(DELTA_CORE, sdecl), baseClass, superClass);
      }
    }

    this.subtype = GraphTransitiveClosureFactory.create(this.inheritanceGraph);

    // complete the subtyping relation with the null type
    ComponentGraph.Component<String, Pair<IFormulaElement, ISuperClassDeclaration>> componentNull = new ComponentGraph.Component<>(new Vertex<String, Pair<IFormulaElement, ISuperClassDeclaration>>(Reference.NAME_NULL_TYPE));
    this.subtype.addVertex(componentNull);
    for(Vertex<ComponentGraph.Component<String, Pair<IFormulaElement, ISuperClassDeclaration>>, List<Edge<String, Pair<IFormulaElement, ISuperClassDeclaration>>>> comp: this.subtype.getVertices()) {
      if(!comp.getID().equals(componentNull)) { this.subtype.addEdge(null, componentNull, comp.getID());
      }
    }

    this.lookup = LookupFactory.create(this.fcst, this.inheritanceGraph);

    this.dmst = (new DMSTFactory()).create(program);

    // complete the DMST with the standard Library
    for(Map.Entry<FCS, Collection<ISuperClassDeclaration>> entry: Reference.classes.entrySet()) {
      String className = entry.getKey().getName();
      this.dmst.addDef(className, DELTA_CORE);
      for(FCS.FCSAttribute att: entry.getKey().getAttributes().values()) {
        this.dmst.addDef(className, att.getName(), DELTA_CORE);
      }
      for(ISuperClassDeclaration superDeclarator: entry.getValue()) {
        this.dmst.addExt(superDeclarator.getBaseClass(), superDeclarator.getSuperClass(), DELTA_CORE);
      }
    }

    this.deltaOrderGraph = (new DeltaOrderGraphFactory()).create(program);
    this.deltaOrderComponentGraph = GraphTransitiveClosureFactory.create(this.deltaOrderGraph);
    this.deltaAfterGraph = this.deltaOrderComponentGraph.flatten();

    // Additional Informations
    this.classes = this.fcst.dom();
    this.deltas = this.dmst.dom();

    this.deltaAfter = new HashMapSet<>();
    this.deltaBefore = new HashMapSet<>();
    for(Vertex<String, List<Edge<String, DeltaOrdering>>> beforeName: this.deltaAfterGraph.getVertices()) {
      IFormulaElement before = this.getDelta(beforeName.getID()).getID();
      this.deltaAfter.putEl(Reference.FORMULA_TRUE, before);
      this.deltaBefore.putEl(before, Reference.FORMULA_TRUE);
      for(Edge<String, List<Edge<String, DeltaOrdering>>> afterName: this.deltaAfterGraph.getNexts(beforeName.getID())) {
        IFormulaElement after = this.getDelta(afterName.getEndID()).getID();
        this.deltaAfter.putEl(before, after);
        this.deltaBefore.putEl(after, before);
      }
    }

    this.inh = InhFactory.create(this.inheritanceGraph);
  }




  public Program getProgram() { return this.program; }
  public FCST getFCST() { return this.fcst; }
  public Graph<String, Pair<IFormulaElement, ISuperClassDeclaration>> getInheritanceGraph() { return this.inheritanceGraph; }
  public ComponentGraph<String, Pair<IFormulaElement, ISuperClassDeclaration>> getSubtype() { return this.subtype; }
  public FCST getLookup() { return this.lookup; }
  public DMST getDMST() { return this.dmst; }
  public Graph<String, DeltaOrdering> getDeltaOrderGraph() { return this.deltaOrderGraph; }
  public ComponentGraph<String, DeltaOrdering> getDeltaOrderComponentGraph() { return this.deltaOrderComponentGraph; }

  public Set<String> getClasses() { return this.classes; }
  public Set<IFormulaElement> getDeltas() { return this.deltas; }
  public DeltaModule getDelta(String delta) { return this.deltaModules.get(delta); }
  public Set<IFormulaElement> getAfterDelta(IFormulaElement delta) {
    Set<IFormulaElement> res = this.deltaAfter.get(delta);
    if(res == null) { res = EMTPYSET; }
    return res;
  }
  public Set<IFormulaElement> getBeforeDelta(IFormulaElement delta) {
    Set<IFormulaElement> res = this.deltaBefore.get(delta);
    if(res == null) { res = EMTPYSET; }
    return res;
  }
  public Set<Set<ISuperClassDeclaration>> getInh(String baseClass, String superClass) {
    Map<String,Set<Set<ISuperClassDeclaration>>> val = this.inh.get(baseClass);
    if(val == null) { return null; }
    else { return val.get(superClass); }
  }


  private static class InhFactory extends GraphVisitorDepthSearch<String, Pair<IFormulaElement, ISuperClassDeclaration>> {
    public static Map<String, HashMapSet<String,Set<ISuperClassDeclaration>>> create(Graph<String, Pair<IFormulaElement, ISuperClassDeclaration>> graph) {
      InhFactory factory = new InhFactory();
      factory.visit(graph);
      return factory.res;
    }


    private Map<String, HashMapSet<String,Set<ISuperClassDeclaration>>> res;
    private InhFactory() { this.res = new HashMap<>(); }

    @Override
    public void leave(Vertex<String, Pair<IFormulaElement, ISuperClassDeclaration>> v) {
      for(Edge<String, Pair<IFormulaElement, ISuperClassDeclaration>> next: v.getNexts()) {
        HashMapSet<String,Set<ISuperClassDeclaration>> val = new HashMapSet<>();
        this.res.put(v.getID(), val);

        String parent = next.getEndID();
        ISuperClassDeclaration decl = next.getID().getSecond();

        // 1. direct super class
        Set<ISuperClassDeclaration> valParent = new HashSet<>();
        valParent.add(decl);
        val.putEl(parent, valParent);

        // 2. ancestors
        HashMapSet<String,Set<ISuperClassDeclaration>> ancestorsAll = this.res.get(parent);
        if(ancestorsAll != null) {
          for (Map.Entry<String,Set<Set<ISuperClassDeclaration>>> ancestors : ancestorsAll.entrySet()) {
            for(Set<ISuperClassDeclaration> path: ancestors.getValue()){
              Set<ISuperClassDeclaration> valAncestors = new HashSet<>(path);
              valAncestors.add(decl);
              val.putEl(ancestors.getKey(), valAncestors);
            }
          }
        }
      }
    }
  }



  public String details() {
    String fcst        = "=========================================\n== FCST:\n" + this.fcst.toString() + "\n";
    String inheritance = "=========================================\n== SUBTYPING GRAPH:" + GraphDetailsFactory.create(this.getSubtype()) + "\n\n";
    String lookup      = "=========================================\n== LOOKUP:\n" + this.lookup.toString() + "\n";
    String dmst        = "=========================================\n== DMST:\n" + this.dmst.toString() + "\n";
    String inh         = "=========================================\n== INHERITANCE PATHS:\n" + InheritanceMapDetailsFactory.create(this.inh) + "\n";
    String order       = "=========================================\n== DELTA ORDER:\n" + "  after relation:  " + this.deltaAfter + "\n" + "  before relation: " + this.deltaBefore + "\n";
    return fcst + inheritance + lookup + dmst + inh + order;
  }


  private static class InheritanceMapDetailsFactory {
    public static String create(Map<String, HashMapSet<String,Set<ISuperClassDeclaration>>> inh) {
      String res = "";
      for(Map.Entry<String, HashMapSet<String,Set<ISuperClassDeclaration>>> entryBase: inh.entrySet()) {
        for(Map.Entry<String,Set<Set<ISuperClassDeclaration>>> entrySuper: entryBase.getValue().entrySet()) {
          res = res + "(" + entryBase.getKey() + " -> " + entrySuper.getKey() + "): [";
          Iterator<Set<ISuperClassDeclaration>> i = entrySuper.getValue().iterator();
          while(i.hasNext()) {
            res = res + " [";
            Iterator<ISuperClassDeclaration> j = i.next().iterator();
            while(j.hasNext()) {
              ISuperClassDeclaration tmp = j.next();
              res = res + "(" + tmp.getDelta() + ": " + tmp.getBaseClass() + " < " + tmp.getSuperClass() + ")";
              if(j.hasNext()) { res = res + ", "; }
            }
            res = res + "]";
            if(i.hasNext()) { res = res + ", "; }
          }
          res = res + " ]\n";
        }
      }
      return res;
    }
  }


  private static class GraphDetailsFactory extends GraphVisitorDepthSearch<ComponentGraph.Component<String, Pair<IFormulaElement, ISuperClassDeclaration>>, List<Edge<String, Pair<IFormulaElement, ISuperClassDeclaration>>>> {

    public static String create(ComponentGraph<String, Pair<IFormulaElement, ISuperClassDeclaration>> graph) {
      GraphDetailsFactory factory = new GraphDetailsFactory();
      factory.visit(graph);
      return factory.res;
    }


    private String res;
    private GraphDetailsFactory() { this.res = ""; }

    @Override
    public void leave(Vertex<ComponentGraph.Component<String, Pair<IFormulaElement, ISuperClassDeclaration>>, List<Edge<String, Pair<IFormulaElement, ISuperClassDeclaration>>>> tt) {
      for(Vertex<String, Pair<IFormulaElement, ISuperClassDeclaration>> v : tt.getID()) {
        this.res += "\n  " + v + " -> [";
        Iterator<Edge<ComponentGraph.Component<String, Pair<IFormulaElement, ISuperClassDeclaration>>, List<Edge<String, Pair<IFormulaElement, ISuperClassDeclaration>>>>> ie = tt.getNexts().iterator();
        while(ie.hasNext()) {
          Iterator<Vertex<String, Pair<IFormulaElement, ISuperClassDeclaration>>> ic = ie.next().getEndID().iterator();
          while(ic.hasNext()) {
            this.res += ic.next();
            if(ic.hasNext()) { this.res += ", "; }
          }
          if(ie.hasNext()) { this.res += ", "; }
        }
        this.res += "]";
      }
    }
  }
}

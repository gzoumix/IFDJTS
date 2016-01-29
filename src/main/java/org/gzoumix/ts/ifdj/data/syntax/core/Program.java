package org.gzoumix.ts.ifdj.data.syntax.core;
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

import org.gzoumix.ts.ifdj.data.syntax.IASTNode;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormula;
import org.gzoumix.ts.ifdj.data.syntax.visitor.IVisitor;
import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaActivation;
import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaOrdering;
import org.gzoumix.ts.ifdj.data.syntax.delta.DeltaModule;
import org.gzoumix.ts.ifdj.data.syntax.fm.Configuration;
import org.gzoumix.ts.ifdj.data.syntax.fm.Feature;
import org.gzoumix.util.syntax.Position;

import java.util.*;


public class Program implements IASTNode {

  // Feature Model Declaration
  private List<Feature> features;
  private List<Configuration> configurations;

  // Configuration Knowledge
  private List<DeltaOrdering> orderings;
  private List<DeltaActivation> activations;

  // Code Base
  private Map<String, DeltaModule> deltas;
  private Map<String, Classs> classes;


  public Program() {
    this.features = new LinkedList<>();
    this.configurations = new LinkedList<>();

    this.orderings = new LinkedList<>();
    this.activations = new LinkedList<>();

    this.deltas = new HashMap<>();
    this.classes = new HashMap<>();
  }

  //////////////////////////////////////////////////////////////////////////////
  // 1. Program Construction
  public void addFeature(Feature feature) {
    feature.setFather(this); this.features.add(feature);
  }
  public void addConfiguration(Configuration configuration) {
    configuration.setFather(this);  this.configurations.add(configuration);
  }

  public void addDeltaOrdering(DeltaOrdering ordering) {
    ordering.setFather(this); this.orderings.add(ordering);
  }
  public void addDeltaActivation(DeltaActivation activation) {
    activation.setFather(this); this.activations.add(activation);
  }

  public void addDeltaModule(DeltaModule delta) {
    delta.setFather(this); this.deltas.put(delta.getName(), delta);
  }
  public void addClass(Classs classs) {
    classs.setFather(this);  this.classes.put(classs.getName(), classs);
  }

  //////////////////////////////////////////////////////////////////////////////
  // 2. Getters
  public List<Feature> getFeatures() { return this.features; }
  public List<Configuration> getConfigurations() { return this.configurations; }
  public List<DeltaOrdering> getOrderings() { return this.orderings; }
  public List<DeltaActivation> getActivations() { return this.activations; }
  public Collection<DeltaModule> getDeltas() { return this.deltas.values(); }
  public DeltaModule getDelta(String name) { return this.deltas.get(name); }
  public Collection<Classs> getClasses() { return this.classes.values(); }
  public Classs getClasss(String name) { return this.classes.get(name); }

  //////////////////////////////////////////////////////////////////////////////
  // 3. Program Manipulation
  public DeltaModule removeDelta(String name) { return this.deltas.remove(name); }
  public Classs removeClass(String name) { return this.classes.remove(name); }







  // Dummy implementation of the IASTNOde interface
  @Override
  public IASTNode getFather() { return null; }

  @Override
  public void setFather(IASTNode father) { }

  @Override
  public Position getPosition() {
    return null;
  }

  @Override
  public void setType(String type) { }

  @Override
  public String getType() { return null; }

  @Override
  public void setDependencyConstraint(IFormula constraint) { }

  @Override
  public IFormula getDependencyConstraint() { return null; }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }


}

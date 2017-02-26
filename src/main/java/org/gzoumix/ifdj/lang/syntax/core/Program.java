package org.gzoumix.ifdj.lang.syntax.core;
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

import org.gzoumix.ifdj.lang.syntax.IASTNode;
import org.gzoumix.ifdj.lang.syntax.formula.IFormula;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.ifdj.lang.syntax.ck.DeltaActivation;
import org.gzoumix.ifdj.lang.syntax.ck.DeltaOrdering;
import org.gzoumix.ifdj.lang.syntax.delta.DeltaModule;
import org.gzoumix.ifdj.lang.syntax.fm.Configuration;
import org.gzoumix.ifdj.lang.syntax.fm.Feature;
import org.gzoumix.util.syntax.Position;

import java.util.*;


public class Program implements IASTNode {

  // Feature Model Declaration
  private List<Feature> features;
  private List<Configuration> configurations;

  // Configuration Knowledge
  private Set<DeltaOrdering> orderings;
  private List<DeltaActivation> activations;

  // Code Base
  private Map<String, DeltaModule> deltas;
  private Map<String, Classs> classes;


  public Program() {
    this.features = new LinkedList<>();
    this.configurations = new LinkedList<>();

    this.orderings = new HashSet<>();
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
  public Collection<DeltaOrdering> getOrderings() { return this.orderings; }
  public List<DeltaActivation> getActivations() { return this.activations; }
  public Collection<DeltaModule> getDeltas() { return this.deltas.values(); }
  public DeltaModule getDelta(String name) { return this.deltas.get(name); }
  public Collection<Classs> getClasses() { return this.classes.values(); }
  public Classs getClasss(String name) { return this.classes.get(name); }

  //////////////////////////////////////////////////////////////////////////////
  // 3. Program Manipulation
  public Classs removeClass(String name) { return this.classes.remove(name); }
  public void removeOrdering(DeltaOrdering order) { this.orderings.remove(order); }

  public DeltaModule removeDelta(String name) {
    Iterator<DeltaActivation> itact = this.getActivations().iterator();
    while(itact.hasNext()) {
      DeltaActivation act = itact.next();
      if(act.getDelta().equals(name)) { itact.remove(); }
    }
    return this.deltas.remove(name);
  }






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

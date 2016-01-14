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

import org.gzoumix.ts.ifdj.data.syntax.formula.FormulaPredicate;
import org.gzoumix.ts.ifdj.data.syntax.formula.IFormulaElement;
import org.gzoumix.util.HashMapSet;
import org.gzoumix.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class DMST {


  private HashMapSet<Pair<String,String>, IFormulaElement> ext;    // the set of delta that define a subtyping relation

  private HashMapSet<String, IFormulaElement> defClass;            // the set of delta that add that class
  private HashMapSet<String, Pair<IFormulaElement,String>> defAtt; // the set of delta defining that the class  have that attribute

  private HashMapSet<String, IFormulaElement> reuseClass;          // modifies the class without changing the super class
  private HashMapSet<String, IFormulaElement> replaceClass;        // modifies the class and changes the super class

  private HashMapSet<Pair<String,String>, IFormulaElement> reuseMethod;
  private HashMapSet<Pair<String,String>, IFormulaElement> replaceMethod;

  private HashMapSet<String, IFormulaElement> removeClass;
  private HashMapSet<Pair<String,String>, IFormulaElement> removeAtt;

  private Set<IFormulaElement> domain;

  public DMST() {
    this.ext = new HashMapSet<>();
    this.defClass = new HashMapSet<>();
    this.defAtt = new HashMapSet<>();
    this.reuseClass = new HashMapSet<>();
    this.replaceClass = new HashMapSet<>();
    this.reuseMethod = new HashMapSet<>();
    this.replaceMethod = new HashMapSet<>();
    this.removeClass = new HashMapSet<>();
    this.removeAtt = new HashMapSet<>();
    this.domain = new HashSet<>();
  }


  /////////////////////////////////////////
  // 1. DMST basic construnction methods
  /////////////////////////////////////////

  public void addExt(String C, String parent, IFormulaElement delta) {
    this.domain.add(delta);
    this.ext.putEl(new Pair<>(C, parent), delta);
  }

  public void addDef(String C, IFormulaElement delta) {
    this.domain.add(delta);
    this.defClass.putEl(C, delta);
  }

  public void addDef(String C, String a, IFormulaElement delta) {
    this.domain.add(delta);
    this.defAtt.putEl(a, new Pair<>(delta, C));
  }

  public void addReuse(String C, IFormulaElement delta) {
    this.domain.add(delta);
    this.reuseClass.putEl(C, delta);
  }

  public void addReplace(String C, IFormulaElement delta) {
    this.domain.add(delta);
    this.replaceClass.putEl(C, delta);
  }

  public void addReuse(String C, String m, IFormulaElement delta) {
    this.domain.add(delta);
    this.reuseMethod.putEl(new Pair<>(C,m), delta);
  }

  public void addReplace(String C, String m, IFormulaElement delta) {
    this.domain.add(delta);
    this.replaceMethod.putEl(new Pair(C,m), delta);
  }

  public void addRemove(String C, IFormulaElement delta) {
    this.domain.add(delta);
    this.removeClass.putEl(C, delta);
  }

  public void addRemove(String C, String a, IFormulaElement delta) {
    this.domain.add(delta);
    this.removeAtt.putEl(new Pair<>(C,a), delta);
  }



  /////////////////////////////////////////
  // 2. SPLS getters
  /////////////////////////////////////////

  public Set<IFormulaElement> ext(String C, String parent) {
    Pair<String, String> ref = new Pair<>(C, parent);
    Set<IFormulaElement> res = this.ext.get(new Pair<>(C, parent));
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<IFormulaElement> defClass(String C) {
    Set<IFormulaElement> res = this.defClass.get(C);
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<Pair<IFormulaElement,String>> defAtt(String att) {
    Set<Pair<IFormulaElement,String>> res = this.defAtt.get(att);
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<IFormulaElement> reuse(String C) {
    Set<IFormulaElement> res = this.reuseClass.get(C);
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<IFormulaElement> replace(String C) {
    Set<IFormulaElement> res = this.replaceClass.get(C);
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<IFormulaElement> reuse(String C, String m) {
    Set<IFormulaElement> res = this.removeAtt.get(new Pair<>(C, m));
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<IFormulaElement> replace(String C, String m) {
    Set<IFormulaElement> res = this.replaceMethod.get(new Pair<>(C,m));
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<IFormulaElement> remove(String C) {
    Set<IFormulaElement> res = this.removeClass.get(C);
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<IFormulaElement> remove(String C, String a) {
    Set<IFormulaElement> res = this.removeAtt.get(new Pair<>(C,a));
    if(res == null) { return new HashSet<>(); }
    return res;
  }

  public Set<IFormulaElement> dom() { return this.domain; }


  /*
    private HashMapSet<Pair<String,String>, IFormulaElement> ext;    // the set of delta that define a subtyping relation

  private HashMapSet<String, IFormulaElement> defClass;            // the set of delta that add that class
  private HashMapSet<String, Pair<IFormulaElement,String>> defAtt; // the set of delta defining that the class  have that attribute

  private HashMapSet<String, IFormulaElement> reuseClass;          // modifies the class without changing the super class
  private HashMapSet<String, IFormulaElement> replaceClass;        // modifies the class and changes the super class

  private HashMapSet<Pair<String,String>, IFormulaElement> reuseMethod;
  private HashMapSet<Pair<String,String>, IFormulaElement> replaceMethod;

  private HashMapSet<String, IFormulaElement> removeClass;
  private HashMapSet<Pair<String,String>, IFormulaElement> removeAtt;

  private Set<IFormulaElement> domain;

   */
  @Override
  public String toString() {
    String res = "";

    // 1. Ext mapping
    res = res + "  + EXT:\n";
    for(Map.Entry<Pair<String,String>, Set<IFormulaElement>> entry: this.ext.entrySet()) {
      res = res + "  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }

    // 2. Def
    res = res + "  + DEF:\n";
    for(Map.Entry<String,Set<IFormulaElement>> entry: this.defClass.entrySet()) {
      res = res + "  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }
    for(Map.Entry<String,Set<Pair<IFormulaElement,String>>> entry: this.defAtt.entrySet()) {
      res = res + "  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }

    // 3. Mod classes
    res = res + "  + MOD CLASS:\n";
    res = res + "  |  + REUSE:\n";
    for(Map.Entry<String,Set<IFormulaElement>> entry: this.reuseClass.entrySet()) {
      res = res + "  |  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }
    res = res + "  |  + REPLACE:\n";
    for(Map.Entry<String,Set<IFormulaElement>> entry: this.replaceClass.entrySet()) {
      res = res + "  |  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }

    // 4. Mod Methods
    res = res + "  + MOD ATTRIBUTE:\n";
    res = res + "  |  + REUSE:\n";
    for(Map.Entry<Pair<String,String>,Set<IFormulaElement>> entry: this.reuseMethod.entrySet()) {
      res = res + "  |  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }
    res = res + "  |  + REPLACE:\n";
    for(Map.Entry<Pair<String,String>,Set<IFormulaElement>> entry: this.replaceMethod.entrySet()) {
      res = res + "  |  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }

    // 5. Remove Class
    res = res + "  + REMOVE CLASS:\n";
    for(Map.Entry<String, Set<IFormulaElement>> entry: this.removeClass.entrySet()) {
      res = res + "  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }

    // 6. Remove Attribute
    res = res + "  + REMOVE ATTRIBUTE:\n";
    for(Map.Entry<Pair<String,String>, Set<IFormulaElement>> entry: this.removeAtt.entrySet()) {
      res = res + "  |  + " + entry.getKey().toString() + " -> " + entry.getValue().toString() + "\n";
    }


    return res;
  }

}

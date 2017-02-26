package org.gzoumix.ifdj.ts;
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

import org.gzoumix.ifdj.lang.data.FCS;
import org.gzoumix.ifdj.lang.data.FCST;
import org.gzoumix.ifdj.lang.syntax.core.Attribute;
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;
import org.gzoumix.ifdj.util.Reference;
import org.gzoumix.util.data.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypingEnvironment {
  private FCST lookup;
  private IFormulaElement currentDelta;
  private String currentClass;
  private Attribute.SignatureMethod currentOriginal;

  private Map<String, Attribute.SignatureField> variables;

  public TypingEnvironment(FCST lookup) {
    this.lookup = lookup;
    this.variables = new HashMap<>();
  }

  public void enterDelta(IFormulaElement delta) { this.currentDelta = delta; }
  public IFormulaElement getCurrentDelta() { return this.currentDelta; }
  public void exitDelta() { this.currentDelta = null; }

  public void enterClass(String currentClass) {
    this.currentClass = currentClass;
  }
  public String getCurrentClass() { return this.currentClass; }

  public void exitClass() { this.currentClass = null; }

  public void setCurrentOriginal(Attribute.SignatureMethod sig) {
    this.currentOriginal = sig;
  }

  public void enterMethod(List<Pair<String, String>> parameters) {
    this.variables.put(Reference.THIS, new Attribute.SignatureField(this.currentClass));
    for (Pair<String, String> p : parameters) {
      this.variables.put(p.getSecond(), new Attribute.SignatureField(p.getFirst()));
    }
  }

  public void exitMethod() {
    this.currentOriginal = null;
    this.variables.clear();
  }

  // Without uniformity, lookup would return a set of signatures
  public Attribute.ISignature lookup(String name) {
    Attribute.ISignature res = null;

    // 0. look if name is null
    if (name.equals(Reference.NAME_NULL_TYPE)) {
      res = Reference.SIG_NULL_TYPE;
    }

    // 1. look at the operators
    if (res == null) {
      res = Reference.Operator.operators.get(name);
    }

    // 2. look at the local variables
    if (res == null) {
      res = this.variables.get(name);
    }

    // 3. look at the global variables
    if (res == null) {
      res = this.lookup(this.currentClass, name);
    }
    return res;
  }

  public Attribute.ISignature lookup(String className, String name) {
    Attribute.ISignature res = null;

    if (className.equals(this.currentClass)) {
      res = name.equals(Reference.ORIGINAL) ? this.currentOriginal : null;
    }

    if (res == null) {
      FCS fcs = this.lookup.get(className);
      FCS.FCSAttribute att = fcs.get(name);
      if (att != null) {
        res = att.getSignatures().keySet().iterator().next();
      }
    }
    return res;
  }

  public boolean doesClassExists(String name) {
    return this.lookup.get(name) != null;
  }

  public boolean isDirectlyAccessible(String name) {
    boolean res;

    res = this.variables.containsKey(name); // if it is a variable

    if(!res) { // can be an operator
      if(Reference.Operator.operators.get(name) != null) {
        res = true;
      }
    }

    if(!res) { // possibly original
      res = this.isDirectlyAccessible(this.currentClass, name);
    }

    return res;
  }

  public boolean isDirectlyAccessible(String baseClass, String name) {
    return baseClass.equals(this.currentClass) && name.equals(Reference.ORIGINAL) && (this.currentOriginal != null);
  }


}

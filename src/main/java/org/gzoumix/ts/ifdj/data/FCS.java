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

import org.gzoumix.ts.ifdj.data.syntax.core.Attribute;
import org.gzoumix.ts.ifdj.data.syntax.core.Classs;
import org.gzoumix.util.data.HashMapSet;

import java.util.*;


// class storing information about class declaration / modifications
public class FCS {

  public static class FCSAttribute {
    private String name;
    private HashMapSet<Attribute.ISignature, Attribute> sigs;

    public FCSAttribute(Attribute att) {
      this.name = att.getName();
      this.sigs = new HashMapSet<>();
      this.sigs.putEl(att.getSignature(), att);
    }

    public String getName() { return this.name; }
    public Map<Attribute.ISignature, Set<Attribute>> getSignatures() { return this.sigs; }

    public boolean add(Attribute att) {
      if(att.getName().equals(this.getName())) {
        this.sigs.putEl(att.getSignature(), att);
        return true;
      } else { return false; }
    }

    public boolean add(FCSAttribute att) {
      if(att.getName().equals(this.getName())) {
        this.sigs.putAll(att.getSignatures());
        return true;
      } else { return false; }
    }
  }


  private String name;
  private Map<String, FCSAttribute> attributes;
  private Set<Classs> origins;

  public FCS(String name) {
    this.name = name;
    this.attributes = new HashMap<>();
    this.origins = new HashSet<>();
  }

  public FCS(FCS fcs) {
    this(fcs.getName());
    this.add(fcs);
  }

  public void addAttribute(Attribute att) {
    String name = att.getName();
    FCSAttribute tmp = this.attributes.get(name);
    if(tmp == null) { this.attributes.put(name, new FCSAttribute(att)); }
    else { tmp.add(att); }

    System.out.println("class \"" + this.getName() + "\": " + ((tmp == null) ? "Adding" : "Merging") + " Attribute \"" + name + "\"");
  }


  public void add(FCS fcs) {
    this.origins.addAll(fcs.origins);

    for(Map.Entry<String, FCSAttribute> entry: fcs.attributes.entrySet()) {
      FCSAttribute att = this.attributes.get(entry.getKey());
      if (att == null) { this.attributes.put(entry.getKey(), entry.getValue()); }
      else { att.add(entry.getValue()); }
    }
  }

  public String getName() { return this.name; }
  public FCSAttribute get(String att) { return this.attributes.get(att); }
  public Map<String, FCSAttribute> getAttributes() { return this.attributes; }
  public Set<String> dom() { return this.attributes.keySet(); }
  public void addOrigin(Classs c) { this.origins.add(c); }
  public Set<Classs> getDecls() { return this.origins; }


  @Override
  public String toString() {
    // 1. Class Name
    String res = this.getName() + "\n";
    // 3. Attributes
    res += "  + Attributes =\n";
    Iterator<FCSAttribute> iAtt = this.attributes.values().iterator();
    while(iAtt.hasNext()) {
      FCSAttribute att = iAtt.next();
      res += "     + " + att.getName() + ":\n";
      Iterator<Attribute.ISignature> iSig = att.getSignatures().keySet().iterator();
      while(iSig.hasNext()){
        if(iAtt.hasNext()) { res += "     |  + "; }
        else { res += "        + "; }
        res += iSig.next().toString() + "\n";
      }
    }
    return res;
  }

  /*@Override
  public String toString() { return this.getName(); }*/
}

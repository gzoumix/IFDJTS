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

import org.gzoumix.util.HashMapSet;

import java.util.*;


public class FCST {

  private Map<String, FCS> map;

  public FCST() {
    this.map = new HashMap<>();
  }

  public FCST(FCST fcst) {
    this();
    for(FCS fcs: fcst.map.values()) {
      this.addFCS(new FCS(fcs));
    }
  }


  public void addFCS(FCS fcs) {
    String name = fcs.getName();
    FCS tmp = this.map.get(name);
    if(tmp == null) { this.map.put(name, fcs); }
    else { tmp.add(fcs); }
  }

  public FCS get(String name) { return this.map.get(name); }
  public Set<String> dom() { return this.map.keySet(); }


  @Override
  public String toString() {
    String res = "";
    Iterator<FCS> iFCS = this.map.values().iterator();
    while(iFCS.hasNext()) {
      res += "+ Class " + iFCS.next().toString();
    }

    /*
    res+= "Subtpyping:\n";
    for(Map.Entry<String, Set<String>> entry: subt.entrySet()) {
      res += "   " +entry.getKey() + " <: ";
      Iterator<String> i = entry.getValue().iterator();
      while(i.hasNext()) {
        res += i.next();
        if(i.hasNext()) { res += ", "; }
      }
      res += "\n";
    }*/

    return res;
  }

}

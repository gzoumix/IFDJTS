package org.gzoumix.ts.ifdj.sat4j;
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

import org.gzoumix.ts.ifdj.data.SPLS;
import org.gzoumix.ts.ifdj.data.syntax.formula.FormulaPredicate;
import org.gzoumix.util.data.Pair;

import java.util.Collection;
import java.util.HashSet;


public class Solution extends Pair<Collection<FormulaPredicate>, Collection<FormulaPredicate>> {
  private int[] array;

  public Solution(int[] array, Collection<FormulaPredicate> first, Collection<FormulaPredicate> second) {
    super(first, second);
    this.array = array;
  }

  public Collection<FormulaPredicate> getSelectedFeatures(SPLS spls) {
    Collection<FormulaPredicate> res = new HashSet<>(this.getFirst());
    return extractFeatures(res, spls);
  }

  public Collection<FormulaPredicate> getUnSelectedFeatures(SPLS spls) {
    Collection<FormulaPredicate> res = new HashSet<>(this.getSecond());
    return extractFeatures(res, spls);
  }

  public Collection<FormulaPredicate> getActivatedDelta(SPLS spls) {
    Collection<FormulaPredicate> res = new HashSet<>(this.getFirst());
    return extractDeltaModules(res, spls);
  }

  public Collection<FormulaPredicate> getNonActivatedDelta(SPLS spls) {
    Collection<FormulaPredicate> res = new HashSet<>(this.getSecond());
    return extractDeltaModules(res, spls);
  }

  public int[] getArray() { return this.array; }




  private static Collection<FormulaPredicate> extractDeltaModules(Collection<FormulaPredicate> set, SPLS spls) {
    set.retainAll(spls.getDeltas());
    return set;
  }

  private static Collection<FormulaPredicate> extractFeatures(Collection<FormulaPredicate> set, SPLS spls) {
    set.removeAll(spls.getDeltas());
    return set;
  }

}

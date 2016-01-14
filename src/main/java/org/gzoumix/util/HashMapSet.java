package org.gzoumix.util;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class HashMapSet<K,V> extends HashMap<K,Set<V>> {

  public HashMapSet() { super(); }
  public HashMapSet(int initialCapacity) { super(initialCapacity); }
  public HashMapSet(int initialCapacity, float loadFactor) { super(initialCapacity, loadFactor); }
  public HashMapSet(Map<? extends K,? extends Set<V>> m) { super(m); }

  public Set<V> putEl(K key, V value) {
    Set<V> values = this.get(key);
    if(values == null) {
      values = new HashSet<>();
      this.put(key, values);
    }
    values.add(value);
    return values;
  }

  @Override
  public void putAll(Map<? extends K,? extends Set<V>> m) {
    for(Map.Entry<? extends K,? extends Set<V>> entry: m.entrySet()) {
      Set<V> set = this.get(entry.getKey());
      if(set != null) { set.addAll(entry.getValue()); }
      else { this.put(entry.getKey(), new HashSet<V>(entry.getValue())); }
    }
  }
}

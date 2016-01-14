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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


public class ImmutableCollection<E> implements Collection<E> {
  protected Collection<E> content;

  public ImmutableCollection() { content = new Vector<>(); }
  public ImmutableCollection(Collection<? extends E> c) {
    content = new Vector<>(c);
  }

  @Override
  public int size() { return this.content.size(); }

  @Override
  public boolean isEmpty() { return this.content.isEmpty(); }

  @Override
  public boolean contains(Object o) { return this.content.contains(o); }

  @Override
  public Iterator<E> iterator() { return new ImmutableIterator<>(this.content.iterator()); }

  @Override
  public Object[] toArray() { return this.content.toArray(); }

  @Override
  public <T> T[] toArray(T[] a) { return this.content.toArray(a); }

  @Override
  public boolean add(E v) { throw new UnsupportedOperationException(); }

  @Override
  public boolean remove(Object o) { throw new UnsupportedOperationException(); }

  @Override
  public boolean containsAll(Collection<?> c) { return this.content.containsAll(c); }

  @Override
  public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException(); }

  @Override
  public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }

  @Override
  public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }

  @Override
  public void clear() {throw new UnsupportedOperationException(); }

  @Override
  public String toString() { return this.content.toString(); }

  @Override
  public int hashCode() { return this.content.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if(o instanceof ImmutableCollection) {
      return this.content.equals(((ImmutableCollection) o).content);
    } else { return false; }
  }
}

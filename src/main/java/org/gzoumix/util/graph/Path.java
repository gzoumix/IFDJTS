package org.gzoumix.util.graph;
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

import org.gzoumix.util.ImmutableIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Path<V, E> implements Collection<Edge<V, E>> {
  private LinkedList<Edge<V, E>> list;

  public Path() {
    this.list = new LinkedList<>();
  }

  public Path(Path<V, E> p) {
    this.list = new LinkedList<>(p.list);
  }

  public V getStart() {
    return this.getFirst().getStart();
  }

  public V getEnd() {
    return this.getLast().getEnd();
  }

  public Edge<V, E> getFirst() {
    return this.list.getFirst();
  }

  public Edge<V, E> getLast() {
    return this.list.getLast();
  }

  public boolean addFirst(Edge<V, E> e) {
    if (this.isEmpty() || this.getFirst().getVertexStart().equals(e.getVertexEnd())) {
      this.list.addFirst(e);
      return true;
    } else {
      return false;
    }
  }

  public boolean addLast(Edge<V, E> e) {
    if (this.isEmpty() || this.getLast().getVertexEnd().equals(e.getVertexStart())) {
      this.list.addLast(e);
      return true;
    } else {
      return false;
    }
  }

  public Edge<V, E> removeFirst() {
    return this.list.removeFirst();
  }

  public Edge<V, E> removeLast() {
    return this.list.removeLast();
  }


  // Implementation of the Collection interface
  @Override
  public int size() {
    return this.list.size();
  }

  @Override
  public boolean isEmpty() {
    return this.list.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return this.list.contains(o);
  }

  @Override
  public Iterator<Edge<V, E>> iterator() {
    return new ImmutableIterator<>(this.list.iterator());
  }

  @Override
  public Object[] toArray() {
    return this.list.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return this.list.toArray(a);
  }

  @Override
  public boolean add(Edge<V, E> e) {
    return this.addLast(e);
  }

  @Override
  public boolean remove(Object o) {
    if (this.isEmpty()) {
      return false;
    } else if (this.list.getFirst().equals(o)) {
      this.list.removeFirst();
      return true;
    } else if (this.list.getLast().equals(o)) {
      this.list.removeLast();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return this.list.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends Edge<V, E>> c) {
    if (c instanceof Path) {
      Path path = (Path) c;
      if (!path.isEmpty() && this.getEnd().equals(path.getFirst())) {
        this.list.addAll(path.list);
        return true;
      } else {
        return false;
      }
    } else {
      throw new UnsupportedOperationException();
    }
  }


  @Override
  public boolean removeAll(Collection<?> c) {
    boolean res = false;
    for (Object o : c) {
      res = res || this.remove(o);
    }
    return res;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    this.list.clear();
  }
}

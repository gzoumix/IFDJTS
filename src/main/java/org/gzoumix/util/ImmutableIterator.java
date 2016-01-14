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

import java.util.Iterator;


public class ImmutableIterator<E> implements Iterator<E> {
  private Iterator<E> internal;

  public ImmutableIterator(Iterator<E> i) { this.internal = i; }

  @Override
  public boolean hasNext() { return this.internal.hasNext(); }

  @Override
  public E next() { return this.internal.next(); }

  @Override
  public void remove() { throw new UnsupportedOperationException(); }
}

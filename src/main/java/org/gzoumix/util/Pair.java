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

public class Pair<A, B> {
  private A first;
  private B second;

  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public A getFirst() { return this.first; }
  public B getSecond() { return this.second; }

  //public void setFirst(A first) { this.first = first; }
  //public void setSecond(B second) { this.second = second; }


  public int hashCode() {
    int hashFirst = first != null ? first.hashCode() : 0;
    int hashSecond = second != null ? second.hashCode() : 0;

    return (hashFirst + hashSecond) * hashSecond + hashFirst;
  }

  public boolean equals(Object other) {
    if (other instanceof Pair) {
      Pair otherPair = (Pair) other;
      return
              ((  this.first == otherPair.first ||
                      ( this.first != null && otherPair.first != null &&
                              this.first.equals(otherPair.first))) &&
                      (	this.second == otherPair.second ||
                              ( this.second != null && otherPair.second != null &&
                                      this.second.equals(otherPair.second))) );
    }

    return false;
  }

  public String toString() { return "(" + first + ", " + second + ")"; }

}
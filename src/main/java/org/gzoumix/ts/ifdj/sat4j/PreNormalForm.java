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

import org.gzoumix.ts.ifdj.util.Reference;

/******************************************************************************/

public class PreNormalForm {

  public static class PreNormalFormTrue extends PreNormalForm {
    @Override
    public String toString() { return Reference.TRUE; }
  }

  public static class PreNormalFormFalse extends PreNormalForm {
    @Override
    public String toString() { return Reference.FALSE; }
  }

  public static class PreNormalFormPredicate extends PreNormalForm {
    private int id;
    public PreNormalFormPredicate(int id) { this.id = id; }
    public int getID() { return this.id; }

    @Override
    public String toString() { return String.valueOf(this.id); }
  }

  public static class PreNormalFormOr extends PreNormalForm {
    private PreNormalForm left;
    private PreNormalForm right;

    public PreNormalFormOr(PreNormalForm left, PreNormalForm right) {
      this.left = left;
      this.right = right;
    }

    public PreNormalForm getLeft() { return this.left; }
    public PreNormalForm getRight() { return this.right; }

    @Override
    public String toString() { return "(" + this.getLeft() + " || " + this.getRight() + ")"; }
  }

  public static class PreNormalFormAnd extends PreNormalForm {
    private PreNormalForm left;
    private PreNormalForm right;

    public PreNormalFormAnd(PreNormalForm left, PreNormalForm right) {
      this.left = left;
      this.right = right;
    }

    public PreNormalForm getLeft() { return this.left; }
    public PreNormalForm getRight() { return this.right; }

    @Override
    public String toString() { return "(" + this.getLeft() + " && " + this.getRight() + ")"; }
  }
}

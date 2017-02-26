package org.gzoumix.ifdj.lang.syntax.ck;
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

import org.gzoumix.ifdj.lang.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.ifdj.lang.syntax.core.Program;
import org.gzoumix.util.syntax.Position;


public class DeltaOrdering extends ASTNodeCommonFunctionalities<Program> {
  private String before;
  private String after;

  public DeltaOrdering(Position pos, String before, String after) {
    super(pos);
    this.before = before;
    this.after = after;
  }

  public String getBefore() { return this.before; }
  public String getAfter() { return this.after; }


  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DeltaOrdering that = (DeltaOrdering) o;

    if (!before.equals(that.before)) return false;
    return after.equals(that.after);

  }

  @Override
  public int hashCode() {
    int result = before.hashCode();
    result = 71 * result + after.hashCode();
    return result;
  }
}

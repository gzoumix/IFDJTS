/*
 * ****************************************************************************
 *  Copyright Michael Lienhardt 2015
 *
 *  This file is part of IFDJTS (a type system for the IFDJ language).
 *
 *  IFDJTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  IFDJTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with IFDJTS.  If not, see <http://www.gnu.org/licenses/>.
 * ****************************************************************************
 */

package org.gzoumix.ts.ifdj.data.syntax.delta;

public class AbstractOperation {

  public enum Operation {
    ADDS("adds"), REMOVES("removes"), MODIFIES("modifies"), EXTENDS("extends");

    private String name;
    private Operation(String name) { this.name = name;}
    public String getName() { return name; }
  }

  public static final class NameElement {
    private String c;
    private String a;

    public NameElement(String c) { this.c = c; this.a = null; }
    public NameElement(String c, String a) { this.c = c; this.a = a; }

    public boolean leq(NameElement el) { return (this.c.equals(el.c)) && ((this.a == null)? true : ((el.a != null) && (this.a.equals(el.a)))); }
    public boolean leq(String c) { return (this.c.equals(c)) && (this.a == null); }
    public boolean leq(String c, String a) { return (this.c.equals(c)) && ((this.a == null)? true : this.a.equals(a)); }

    public String getNameClass() { return c; }
    public String getNameAttribute() { return a; }
    public boolean isClass() { return this.a == null; }
    public boolean isAttribute() { return this.a != null; }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NameElement that = (NameElement) o;

      if (!c.equals(that.c)) return false;
      return !(a != null ? !a.equals(that.a) : that.a != null);
    }

    @Override
    public int hashCode() {
      int result = c.hashCode();
      result = 31 * result + (a != null ? a.hashCode() : 0);
      return result;
    }

    @Override
    public String toString() {
      return "C" + this.c + ((this.a == null) ? "" : "A" + this.a);
    }
  }


  public static AbstractOperation ext(String c, String ext, ClassModification cl) { return new AbstractOperation(Operation.EXTENDS, new NameElement(c), ext, cl, null); }
  public static AbstractOperation adds(String c, ClassAddition cl) { return new AbstractOperation(Operation.ADDS, new NameElement(c), null, cl, null); }
  public static AbstractOperation adds(String c, String a, AttributeAddition att) { return new AbstractOperation(Operation.ADDS, new NameElement(c,a), null, null, att); }
  public static AbstractOperation removes(String c, ClassRemoval cl) { return new AbstractOperation(Operation.REMOVES, new NameElement(c), null, cl, null); }
  public static AbstractOperation removes(String c, String a, AttributeRemoval att) { return new AbstractOperation(Operation.REMOVES, new NameElement(c,a), null, null, att); }
  public static AbstractOperation modifies(String c, String a, AttributeModification att) { return new AbstractOperation(Operation.MODIFIES, new NameElement(c,a), null, null, att); }


  private Operation op;
  private NameElement el;
  private String ext;
  private IClassOperation cl;
  private IAttributeOperation att;


  private AbstractOperation(Operation op, NameElement el, String ext, IClassOperation cl, IAttributeOperation att) {
    this.op = op;
    this.el = el;
    this.ext = ext;
    this.cl = cl;
    this.att = att;
  }

  public Operation getOp() { return op; }
  public NameElement getEl() { return el; }
  public String getExt() { return ext; }
  public IClassOperation getCl() { return cl; }
  public IAttributeOperation getAtt() { return att; }
}

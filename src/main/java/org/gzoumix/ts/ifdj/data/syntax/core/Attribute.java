package org.gzoumix.ts.ifdj.data.syntax.core;
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

import org.gzoumix.ts.ifdj.data.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ts.ifdj.data.syntax.IASTNode;
import org.gzoumix.ts.ifdj.data.syntax.visitor.IVisitor;
import org.gzoumix.ts.ifdj.data.syntax.expression.IExpression;
import org.gzoumix.util.data.Pair;
import org.gzoumix.util.syntax.Position;

import java.util.*;


public class Attribute  extends ASTNodeCommonFunctionalities<IASTNode> {


  //////////////////////////////////////////////////////////
  // 1. Signatures

  public interface ISignature {}

  public static class SignatureMethod implements ISignature {
    private String rtype;
    private LinkedList<Pair<String, String>> parameters;
    private List<String> ptypes;
    private List<IExpression> expressions;
    private IExpression returnedExpression;

    public SignatureMethod(String rtype) {
      this.rtype = rtype;
      this.parameters = new LinkedList<>();
      this.ptypes = new ArrayList<>();
      this.expressions = new ArrayList<>();
    }

    public boolean addParameter(String type, String name) {
      boolean res = this.parameters.add(new Pair<>(type, name));
      if(res) {
        res = this.ptypes.add(type);
        if(!res) { this.parameters.removeLast(); System.out.println("Should never occur");}
      }
      return res;
    }

    public boolean addExpression(IExpression e) {
      boolean res = this.expressions.add(e);
      if(res) { this.returnedExpression = e; }
      return res;
    }

    public String rtype() { return this.rtype; }
    public List<String> ptypes() { return this.ptypes; }

    public Map<String, String> typeEnvironment() {
      Map<String,String> res = new HashMap<>();
      for(Pair<String,String> parameter: this.parameters) {
        res.put(parameter.getFirst(), parameter.getSecond());
      }
      return res;
    }

    public List<Pair<String,String>> getParameters() { return this.parameters; }
    public List<IExpression> getExpressions() { return this.expressions; }
    public IExpression getReturnedExpression () { return this.returnedExpression; }

    @Override
    public boolean equals(Object o) {
      if(o instanceof SignatureMethod) {
        SignatureMethod s = (SignatureMethod)o;
        return s.rtype.equals(this.rtype) && s.ptypes.equals(this.ptypes);
      } else { return false; }
    }

    @Override
    public int hashCode() {
      int r = rtype.hashCode();
      int p = ptypes.hashCode();
      return (r + p) * p + r;
    }

    @Override
    public String toString() {
      String res = this.rtype + "(";
      Iterator<Pair<String, String>> i = parameters.iterator();
      while(i.hasNext()) {
        Pair<String, String> tmp = i.next();
        res += tmp.getFirst() + " " + tmp.getSecond();
        if(i.hasNext()) { res += ", "; }
      }
      return res += ")";
    }
  }

  public static class SignatureField implements ISignature {
    private String type;

    public SignatureField(String type) { this.type = type; }
    public String type() { return this.type; }

    @Override
    public boolean equals(Object o) {
      if(o instanceof SignatureField) {
        SignatureField s = (SignatureField)o;
        return s.type.equals(this.type);
      } else { return false; }
    }

    @Override
    public int hashCode() { return this.type.hashCode(); }

    @Override
    public String toString() {
      return type;
    }
  }



  //////////////////////////////////////////////////////////
  // 2. Class Definition

  private String cl;
  private String name;
  private ISignature sig;
  //private HashMapSet<ISignature, Position> pos;

  public Attribute(Position pos, String cl, String name, ISignature sig) {
    super(pos);
    this.name = name;
    this.sig = sig;
    this.cl = cl;

    if(sig instanceof SignatureMethod) {
      for(IExpression expression: ((SignatureMethod)sig).getExpressions()) {
        expression.setFather(this);
      }
    }
  }

  @Override
  public void accept(IVisitor visitor) { visitor.visit(this); }


  public String getClassName() { return cl; }
  public String getName() { return this.name; }
  public ISignature getSignature() { return this.sig; }


  // Redefinition of hashing methods
  @Override
  public boolean equals(Object o) {
    if(o instanceof Attribute) { return this.getName().equals(((Attribute) o).getName()); }
    else { return false; }
  }

  @Override
  public int hashCode() { return this.getName().hashCode(); }
}

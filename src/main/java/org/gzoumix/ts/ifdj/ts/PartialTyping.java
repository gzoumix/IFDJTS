package org.gzoumix.ts.ifdj.ts;
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

import org.gzoumix.ts.ifdj.data.FCST;
import org.gzoumix.ts.ifdj.data.SPLS;
import org.gzoumix.ts.ifdj.data.syntax.core.Attribute;
import org.gzoumix.ts.ifdj.data.syntax.core.Classs;
import org.gzoumix.ts.ifdj.data.syntax.delta.AttributeModification;
import org.gzoumix.ts.ifdj.data.syntax.delta.ClassModification;
import org.gzoumix.ts.ifdj.data.syntax.expression.*;
import org.gzoumix.ts.ifdj.data.syntax.visitor.VisitorBasic;
import org.gzoumix.ts.ifdj.util.Reference;
import org.gzoumix.util.Global;
import org.gzoumix.util.graph.ComponentGraph;
import org.gzoumix.util.graph.Edge;
import org.gzoumix.util.graph.Graph;

import java.util.*;


public class PartialTyping extends VisitorBasic {

  public static boolean check(SPLS spls) {
    PartialTyping factory = new PartialTyping(spls.getLookup(), spls.getSubtype());
    factory.visit(spls.getProgram());
    return factory.hasError;
  }


  ///////////////////////////////////////////////////////////////////////////////
  // IMPLEMENTATION
  ///////////////////////////////////////////////////////////////////////////////

  ///////////////////////////////////////
  // 2. Class Definition

  private TypingEnvironment environment;
  private Graph<String, Object> subtype;
  //private Map<IExpression, String> types;
  private boolean hasError;

  private PartialTyping(FCST lookup, ComponentGraph<String> subtype) {
    this.environment = new TypingEnvironment(lookup);
    this.subtype = subtype.flatten();
    //this.types = new HashMap<>();
    this.hasError = false;
  }

  private boolean isSubtype(String before, String after) {
    if(before.equals(after)) { return true; }
    for(Edge<String, Object> edge: this.subtype.getNexts(before)) {
      if(edge.getEnd().equals(after)) { return true; }
    }
    return false;
  }

  private void registerError(Object o) {
    Global.log.logError(o);
    this.hasError = true;
  }
  ///////////////////////////////////////
  // 3. Visiting Algorithm

  // 3.1. Delta Modules
  @Override
  public void visit(ClassModification classModification) {
    this.environment.enterClass(classModification.getName());
    super.visit(classModification);
  }

  @Override
  public void visit(AttributeModification attributeModification) {
    Attribute.ISignature sig = attributeModification.getAttribute().getSignature();
    if(sig instanceof Attribute.SignatureMethod) {
      this.environment.setCurrentOriginal((Attribute.SignatureMethod) sig);
    }
    super.visit(attributeModification);
  }

  // 3.2. Base Code
  @Override
  public void visit(Classs classs) {
    this.environment.enterClass(classs.getName());
    super.visit(classs);
  }


  @Override
  public void visit(Attribute attribute) {
    Attribute.ISignature sig = attribute.getSignature();
    Attribute.SignatureMethod msig = (sig instanceof Attribute.SignatureMethod) ? (Attribute.SignatureMethod) sig : null;

    if (!sig.equals(this.environment.lookup(attribute.getName()))) {
      this.registerError(" Typing: declaration of \""
              + this.environment.getCurrentClass() + "." + attribute.getName()
              + "\" in [" + attribute.getPosition().toString() + "] does not match its declaration: " + sig.toString());
    }

    if (msig != null) {
      //System.out.println("adding method \"" + attribute.getName() + "\" parameters to the typing environment");
      this.environment.enterMethod(msig.getParameters());
    }
    super.visit(attribute);

    if (msig != null) {
      String rtype = msig.getReturnedExpression().getType();
      if (!this.isSubtype(rtype, msig.rtype())) {
        this.registerError(" Typing: returned expression of \""
                + this.environment.getCurrentClass() + "." + attribute.getName()
                + "\" in [" + attribute.getPosition().toString() + "] has type \""
                + rtype + "\" instead of \"" + msig.rtype() + "\"");
      }
      this.environment.exitMethod();
    }
  }

  // 3.3 Expressions
  @Override
  public void visit(ExpressionAccess expressionAccess) {
    super.visit(expressionAccess);
    IExpression exp = expressionAccess.getBase();
    Attribute.ISignature sig;
    if(exp != null) {
      sig = this.environment.lookup(exp.getType(), expressionAccess.getName());
    } else {
      sig = this.environment.lookup(expressionAccess.getName());
    }

    if (sig != null) {
      if(sig instanceof Attribute.SignatureField) {
        expressionAccess.setType(((Attribute.SignatureField) sig).type());
      } else {
        this.registerError(" Typing: reference to \""
                + expressionAccess.getName()
                + "\" in [" + expressionAccess.getPosition().toString() + "] is neither a field or a variable");
        expressionAccess.setType(Reference.NAME_NULL_TYPE); // set its type to null: typing will thus continue
      }
    } else {
      this.registerError(" Typing: the reference \""
              + expressionAccess.getName()
              + "\" in [" + expressionAccess.getPosition().toString() + "] does not exist");
      expressionAccess.setType(Reference.NAME_NULL_TYPE); // set its type to null: typing will thus continue
    }
  }

  @Override
  public void visit(ExpressionAssign expressionAssign) {
    super.visit(expressionAssign);
    String left = expressionAssign.getReference().getType();
    String right = expressionAssign.getValue().getType();
    if(!this.isSubtype(right, left)) {
      this.registerError(" Typing: the assignement in [" + expressionAssign.getPosition().toString()
              + "] has non matching types: \"" + left + "\" vs \"" + right + "\"");
    }
    expressionAssign.setType(left);
  }

  @Override
  public void visit(ExpressionCast expressionCast) {
    super.visit(expressionCast);
    String left = expressionCast.getType();
    String right = expressionCast.getExpression().getType();

    if(this.environment.doesClassExists(left)) {
      if(!this.isSubtype(right, left)) {
        this.registerError(" Typing: the casting expression in [" + expressionCast.getPosition().toString()
                + "] has non matching types: \"" + left + "\" vs \"" + right + "\"");
      }
      expressionCast.setType(left);
    } else {
      this.registerError(" Typing: the class \"" + left + "\" used in ["
              + expressionCast.getPosition().toString() + "] does not exist");
      expressionCast.setType(Reference.NAME_NULL_TYPE);
    }

  }

  @Override
  public void visit(ExpressionMethodCall expressionMethodCall) {
    super.visit(expressionMethodCall);
    Attribute.ISignature sig;
    Attribute.SignatureMethod msig;
    if(expressionMethodCall.getBase() != null) {
      sig = this.environment.lookup(expressionMethodCall.getBase().getType(), expressionMethodCall.getName());
    } else {
      sig = this.environment.lookup(expressionMethodCall.getName());
    }

    if(sig instanceof Attribute.SignatureMethod) {
      msig = (Attribute.SignatureMethod)sig;

      // check parameters
      Iterator<String> itypesFormal = msig.ptypes().iterator();
      int i = 1;
      for(IExpression exp: expressionMethodCall.getParameters()) {
        if(itypesFormal.hasNext()) {
          String typeFormal = itypesFormal.next();
          String typeReal = exp.getType();
          if(!this.isSubtype(typeReal, typeFormal)) {
            this.registerError(" Typing: the " + i + "th parameter of the method call in ["
                    + expressionMethodCall.getPosition().toString()
                    + "] has non matching types: \"" + typeReal + "\" vs \"" + typeFormal + "\"");
          }
        } else {
          this.registerError(" Typing: the method call in ["+ expressionMethodCall.getPosition().toString()
                  + "] does not have the right number of parameters: "
                  + msig.ptypes().size() + " vs " + expressionMethodCall.getParameters().size());
        }
      }
      if(itypesFormal.hasNext()) {
        this.registerError(" Typing: the method call in ["+ expressionMethodCall.getPosition().toString()
                + "] does not have the right number of parameters: "
                + msig.ptypes().size() + " vs " + expressionMethodCall.getParameters().size());
      }
      expressionMethodCall.setType(msig.rtype());
    } else {
      this.registerError(" Typing: the method call in [" + expressionMethodCall.getPosition().toString()
              + "] does not reference a valid method");
      expressionMethodCall.setType(Reference.NAME_NULL_TYPE);
    }
  }

  @Override
  public void visit(ExpressionNew expressionNew) {
    String type = expressionNew.getType();
    if(this.environment.doesClassExists(type)) {
      expressionNew.setType(type);
    } else {
      this.registerError(" Typing: the class \"" + type + "\" used in ["
              + expressionNew.getPosition().toString() + "] does not exist");
      expressionNew.setType(Reference.NAME_NULL_TYPE);
    }
  }

  @Override
  public void visit(LiteralInteger literalInteger) {
    literalInteger.setType(Reference.NAME_INT_CLASS);
  }

  @Override
  public void visit(LiteralString literalString) {
    literalString.setType(Reference.NAME_STRING_CLASS);
  }


}



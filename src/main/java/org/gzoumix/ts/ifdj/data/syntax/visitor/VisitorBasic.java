package org.gzoumix.ts.ifdj.data.syntax.visitor;
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

import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaActivation;
import org.gzoumix.ts.ifdj.data.syntax.ck.DeltaOrdering;
import org.gzoumix.ts.ifdj.data.syntax.core.Attribute;
import org.gzoumix.ts.ifdj.data.syntax.core.Classs;
import org.gzoumix.ts.ifdj.data.syntax.core.Program;
import org.gzoumix.ts.ifdj.data.syntax.delta.*;
import org.gzoumix.ts.ifdj.data.syntax.expression.*;
import org.gzoumix.ts.ifdj.data.syntax.fm.Configuration;
import org.gzoumix.ts.ifdj.data.syntax.fm.Feature;
import org.gzoumix.ts.ifdj.data.syntax.formula.*;


public class VisitorBasic implements IVisitor {
  @Override
  public void visit(Program program) {
    for(Feature feature: program.getFeatures()) {
      feature.accept(this);
    }

    for(Configuration configuration: program.getConfigurations()) {
      configuration.accept(this);
    }

    for(DeltaOrdering deltaOrdering: program.getOrderings()) {
      deltaOrdering.accept(this);
    }

    for(DeltaActivation deltaActivation: program.getActivations()) {
      deltaActivation.accept(this);
    }

    for(DeltaModule delta: program.getDeltas()) {
      delta.accept(this);
    }

    for(Classs classs: program.getClasses()) {
      classs.accept(this);
    }
  }


  //////////////////////////////////////////////////////////////////////////////
  // 1. Feature Model
  @Override
  public void visit(Feature feature) { }

  @Override
  public void visit(Configuration configuration) {
    configuration.getFormula().accept(this);
  }

  //////////////////////////////////////////////////////////////////////////////
  // 2. Configuration Knowledge

  @Override
  public void visit(DeltaActivation activation) {
    activation.getFormula().accept(this);
  }

  @Override
  public void visit(DeltaOrdering deltaOrdering) { }

  //////////////////////////////////////////////////////////////////////////////
  // 3. Delta Modules

  @Override
  public void visit(DeltaModule deltaModule) {
    for(IClassOperation cop: deltaModule.getOperations()) {
      cop.accept(this);
    }
  }

  @Override
  public void visit(ClassAddition classAddition) {
    classAddition.getClasss().accept(this);
  }

  @Override
  public void visit(ClassModification classModification) {
    for(IAttributeOperation op: classModification.getOperations()) {
      op.accept(this);
    }
  }

  @Override
  public void visit(ClassRemoval classRemoval) { }

  @Override
  public void visit(AttributeAddition attributeAddition) {
    attributeAddition.getAttribute().accept(this);
  }

  @Override
  public void visit(AttributeModification attributeModification) {
    attributeModification.getAttribute().accept(this);
  }

  @Override
  public void visit(AttributeRemoval attributeRemoval) { }

  //////////////////////////////////////////////////////////////////////////////
  // 3. Base Code

  @Override
  public void visit(Classs classs) {
    for(Attribute att: classs.getAttributes()) {
      att.accept(this);
    }
  }

  @Override
  public void visit(Attribute attribute) {
    Attribute.ISignature sig = attribute.getSignature();
    if(sig instanceof Attribute.SignatureMethod) {
      for(IExpression exp: ((Attribute.SignatureMethod) sig).getExpressions()) {
        exp.accept(this);
      }
    }
  }

  @Override
  public void visit(ExpressionAccess expressionAccess) {
    IExpression exp = expressionAccess.getBase();
    if(exp != null) { exp.accept(this); }
  }

  @Override
  public void visit(ExpressionAssign expressionAssign) {
    expressionAssign.getReference().accept(this);
    expressionAssign.getValue().accept(this);
  }

  @Override
  public void visit(ExpressionCast expressionCast) {
    expressionCast.getExpression().accept(this);
  }

  @Override
  public void visit(ExpressionMethodCall expressionMethodCall) {
    IExpression exp = expressionMethodCall.getBase();
    if(exp != null) { exp.accept(this); }
    for(IExpression param: expressionMethodCall.getParameters()) {
      param.accept(this);
    }
  }

  @Override
  public void visit(ExpressionNew expressionNew) { }

  @Override
  public void visit(LiteralInteger literalInteger) { }

  @Override
  public void visit(LiteralString literalString) { }


  //////////////////////////////////////////////////////////////////////////////
  // 4. Formula

  @Override
  public void visit(FormulaAnd formulaAnd) {
    for(IFormula formula: formulaAnd) {
      formula.accept(this);
    }
  }

  @Override
  public void visit(FormulaFalse formulaFalse) { }

  @Override
  public void visit(FormulaTrue formulaTrue) { }

  @Override
  public void visit(FormulaImplies formulaImplies) {
    formulaImplies.getLeft().accept(this);
    formulaImplies.getRight().accept(this);
  }

  @Override
  public void visit(FormulaEquivalent formulaEquivalent) {
    formulaEquivalent.getLeft().accept(this);
    formulaEquivalent.getRight().accept(this);
  }

  @Override
  public void visit(FormulaNot formulaNot) {
    formulaNot.getFormula().accept(this);
  }

  @Override
  public void visit(FormulaOr formulaOr) {
    for(IFormula formula: formulaOr) {
      formula.accept(this);
    }
  }

  @Override
  public <I> void visit(FormulaPredicate<I> formulaPredicate) { }

}

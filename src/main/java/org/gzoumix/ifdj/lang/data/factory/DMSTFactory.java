package org.gzoumix.ifdj.lang.data.factory;
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

import org.gzoumix.ifdj.lang.data.DMST;
import org.gzoumix.ifdj.lang.syntax.core.Attribute;
import org.gzoumix.ifdj.lang.syntax.core.Classs;
import org.gzoumix.ifdj.lang.syntax.core.Program;
import org.gzoumix.ifdj.lang.syntax.delta.*;
import org.gzoumix.ifdj.lang.syntax.expression.ExpressionMethodCall;
import org.gzoumix.ifdj.lang.syntax.expression.IExpression;
import org.gzoumix.ifdj.lang.syntax.formula.FormulaPredicate;
import org.gzoumix.ifdj.lang.syntax.formula.FormulaTrue;
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;
import org.gzoumix.ifdj.lang.syntax.visitor.VisitorBasic;
import org.gzoumix.ifdj.util.Reference;


public class DMSTFactory extends VisitorBasic {
  private DMST dmst;
  private IFormulaElement delta;
  private String className;
  private boolean hasOriginal;

  public DMSTFactory() { }

  public DMST create(Program program) {
    DMST res = this.dmst = new DMST();
    this.visit(program);
    this.dmst = null;
    return res;
  }


  @Override
  public void visit(Program program) {
    for(DeltaModule delta: program.getDeltas()) {
      delta.accept(this);
    }

    for(Classs classs: program.getClasses()) {
      this.delta = new FormulaTrue(classs.getPosition());
      classs.accept(this);
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // 3. Delta Modules

  @Override
  public void visit(DeltaModule delta) {
    this.delta = new FormulaPredicate<>(delta.getPosition(), delta.getName());
    super.visit(delta);
  }

  @Override
  public void visit(ClassModification operation) {
    this.className = operation.getName();

    if(operation.getSuperClass() == null) {
      this.dmst.addReuse(this.className, this.delta);
    } else {
      this.dmst.addExt(this.className, operation.getSuperClass(), this.delta);
      this.dmst.addReplace(this.className, this.delta);
    }
    super.visit(operation);
  }

  @Override
  public void visit(ClassRemoval operation) {
    this.dmst.addRemove(operation.getName(), this.delta);
  }

  @Override
  public void visit(AttributeModification operation) {
    Attribute.ISignature sig = operation.getAttribute().getSignature();
    this.hasOriginal = false;
    if(sig instanceof Attribute.SignatureMethod) {
      for(IExpression exp: ((Attribute.SignatureMethod) sig).getExpressions()) {
        exp.accept(this);
      }
    }

    if(this.hasOriginal) {
      this.dmst.addReuse(this.className, operation.getAttribute().getName(), this.delta);
    } else {
      this.dmst.addReplace(this.className, operation.getAttribute().getName(), this.delta);
    }
  }

  @Override
  public void visit(AttributeRemoval operation) {
    this.dmst.addRemove(this.className, operation.getName(), this.delta);
  }

  //////////////////////////////////////////////////////////////////////////////
  // 3. Base Code

  @Override
  public void visit(Classs classs) {
    this.className = classs.getName();
    this.dmst.addExt(this.className, classs.getSuperClass(), this.delta);
    this.dmst.addDef(this.className, this.delta);
    super.visit(classs);
  }

  @Override
  public void visit(Attribute attribute) {
    this.dmst.addDef(this.className, attribute.getName(), this.delta);
  }

  @Override
  public void visit(ExpressionMethodCall expressionMethodCall) {
    super.visit(expressionMethodCall);
    this.hasOriginal = this.hasOriginal || expressionMethodCall.getName().equals(Reference.ORIGINAL);
  }


}

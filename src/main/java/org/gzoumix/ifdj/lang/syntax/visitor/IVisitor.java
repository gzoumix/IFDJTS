package org.gzoumix.ifdj.lang.syntax.visitor;/******************************************************************************/
/* Copyright Gzoumix 2015                                                     */
/*                                                                            */
/* This file is part of Gzoumcraft (a minecraft mod).                         */
/*                                                                            */
/* Gzoumcraft is free software: you can redistribute it and/or modify         */
/* it under the terms of the GNU General Public License as published by       */
/* the Free Software Foundation, either version 3 of the License, or          */
/* (at your option) any later version.                                        */
/*                                                                            */
/* Gzoumcraft is distributed in the hope that it will be useful,              */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of             */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              */
/* GNU General Public License for more details.                               */
/*                                                                            */
/* You should have received a copy of the GNU General Public License          */
/* along with Gzoumcraft.  If not, see <http://www.gnu.org/licenses/>.        */

import org.gzoumix.ifdj.lang.syntax.ck.DeltaActivation;
import org.gzoumix.ifdj.lang.syntax.ck.DeltaOrdering;
import org.gzoumix.ifdj.lang.syntax.core.Attribute;
import org.gzoumix.ifdj.lang.syntax.core.Classs;
import org.gzoumix.ifdj.lang.syntax.core.Program;
import org.gzoumix.ifdj.lang.syntax.delta.*;
import org.gzoumix.ifdj.lang.syntax.expression.*;
import org.gzoumix.ifdj.lang.syntax.fm.Configuration;
import org.gzoumix.ifdj.lang.syntax.fm.Feature;
import org.gzoumix.ifdj.lang.syntax.formula.*;

/******************************************************************************/
public interface IVisitor {
  void visit(Program program);

  void visit(Feature feature);
  void visit(Configuration configuration);

  void visit(DeltaActivation activation);
  void visit(DeltaOrdering deltaOrdering);

  void visit(DeltaModule deltaModule);
  void visit(ClassAddition classAddition);
  void visit(ClassModification classModification);
  void visit(ClassRemoval classRemoval);
  void visit(AttributeAddition attributeAddition);
  void visit(AttributeModification attributeModification);
  void visit(AttributeRemoval attributeRemoval);



  void visit(Classs classs);
  void visit(Attribute attribute);

  void visit(ExpressionAccess expressionAccess);
  void visit(ExpressionAssign expressionAssign);
  void visit(ExpressionCast expressionCast);
  void visit(ExpressionMethodCall expressionMethodCall);
  void visit(ExpressionNew expressionNew);
  void visit(LiteralInteger literalInteger);
  void visit(LiteralString literalString);


  void visit(FormulaAnd formulaAnd);
  void visit(FormulaFalse formulaFalse);
  void visit(FormulaTrue formulaFalse);
  void visit(FormulaImplies formulaImplies);
  void visit(FormulaNot formulaNot);
  void visit(FormulaOr formulaOr);
  <I> void visit(FormulaPredicate<I> formulaPredicate);
  void visit(FormulaEquivalent formulaEquivalent);
}

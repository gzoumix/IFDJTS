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

package org.gzoumix.ts.ifdj.parser;

import org.antlr.v4.codegen.model.LL1PlusBlockSingleAlt;
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
import org.gzoumix.ts.ifdj.data.syntax.visitor.VisitorBasic;
import org.gzoumix.util.data.Pair;

import java.io.PrintStream;
import java.util.Iterator;

public class ProgramPrint extends VisitorBasic {


  public static void print(PrintStream out, Program p) {
    ProgramPrint factory = new ProgramPrint(out);
    factory.visit(p);
  }



  private PrintStream out;
  private ProgramPrint(PrintStream out) { this.out = out; }


  private static String string(int i) {
    String res = IFDJKeywords.VOCABULARY.getLiteralName(i);
    return res.substring(1, res.length() - 1);
  }

  private static String DOT = string(IFDJKeywords.DOT);
  private static String COMMA = string(IFDJKeywords.COMMA);
  private static String COLON = string(IFDJKeywords.COLON);
  private static String SEMICOLON = string(IFDJKeywords.SEMICOLON);
  private static String LCBRACKET =  string(IFDJKeywords.LCBRACKET);
  private static String RCBRACKET =  string(IFDJKeywords.RCBRACKET);
  private static String LPAREN =  string(IFDJKeywords.LPAREN);
  private static String RPAREN =  string(IFDJKeywords.RPAREN);

  private static String WHEN = string(IFDJKeywords.KWWHEN);
  private static String LNEQ = string(IFDJKeywords.LNEQ);
  private static String DELTA = string(IFDJKeywords.KWDELTA);
  private static String ADDS = string(IFDJKeywords.KWADDS);
  private static String REMOVES = string(IFDJKeywords.KWREMOVES);
  private static String MODIFIES = string(IFDJKeywords.KWMODIFIES);
  private static String EXTENDING = string(IFDJKeywords.KWEXTENDING);
  private static String CLASS = string(IFDJKeywords.KWCLASS);
  private static String EXTENDS = string(IFDJKeywords.KWEXTENDS);
  private static String ASSIGN = string(IFDJKeywords.KWASSIGN);
  private static String NEW = string(IFDJKeywords.KWNEW);
  private static String RETURN = string(IFDJKeywords.KWRETURN);



  private static String FEATURES = string(IFDJKeywords.KWFEATURES) + " " + COLON;
  private static String CONSTRAINT = string(IFDJKeywords.KWCONSTRAINTS) + " " + COLON;

  private static String DELTA_ACTS = DELTA + " " + string(IFDJKeywords.KWACTIVATE) + " " + COLON;
  private static String DELTA_ORDERS =  DELTA + " " + string(IFDJKeywords.KWORDER) + " " + COLON;





  @Override
  public void visit(Program program) {
    ///////////////////////////////////////////////////////////////////////////
    // 1. FEATURE MODEL

    // 1.1. Features
    out.print(FEATURES + " ");
    Iterator<Feature> itfeature = program.getFeatures().iterator();
    while(itfeature.hasNext()) {
      itfeature.next().accept(this);
      if(itfeature.hasNext()) { out.print(COMMA + " "); }
    }
    out.print(SEMICOLON + "\n\n");

    // 1.2. Configurations
    for(Configuration configuration: program.getConfigurations()) {
      out.print(CONSTRAINT + " ");
      configuration.accept(this);
      out.print(SEMICOLON + "\n");
    }
    out.print("\n");


    ///////////////////////////////////////////////////////////////////////////
    // 2. CONFIGURATION KNOWLEDGE

    // 2.1. Delta Activation
    out.print(DELTA_ACTS + " ");
    Iterator<DeltaActivation> itdeltaact = program.getActivations().iterator();
    while(itdeltaact.hasNext()) {
      itdeltaact.next().accept(this);
      if(itdeltaact.hasNext()) { out.print(COMMA + " "); }
    }
    out.print(SEMICOLON + "\n\n");

    // 2.2. delta order
    out.print(DELTA_ACTS + " ");
    Iterator<DeltaOrdering> itdeltaorder = program.getOrderings().iterator();
    while(itdeltaorder.hasNext()) {
      itdeltaorder.next().accept(this);
      if(itdeltaorder.hasNext()) { out.print(COMMA + " "); }
    }
    out.print(SEMICOLON + "\n\n");


    ///////////////////////////////////////////////////////////////////////////
    // 3. ARTIFACT BASE

    // 3.1. Delta Modules
    for(DeltaModule delta: program.getDeltas()) {
      delta.accept(this);
    }

    // 3.2. Core Program
    for(Classs classs: program.getClasses()) {
      classs.accept(this);
    }
  }


  //////////////////////////////////////////////////////////////////////////////
  // 1. Feature Model
  @Override
  public void visit(Feature feature) { out.print(feature.getName()); }

  @Override
  public void visit(Configuration configuration) {
    configuration.getFormula().accept(this);
  }

  //////////////////////////////////////////////////////////////////////////////
  // 2. Configuration Knowledge

  @Override
  public void visit(DeltaActivation activation) {
    out.print(activation.getDelta() + " " + WHEN + " ");
    activation.getFormula().accept(this);
  }

  @Override
  public void visit(DeltaOrdering deltaOrdering) {
    out.print(deltaOrdering.getBefore() + " " + LNEQ + " " + deltaOrdering.getAfter());
  }


  //////////////////////////////////////////////////////////////////////////////
  // 3. Delta Modules

  @Override
  public void visit(DeltaModule deltaModule) {
    out.print(DELTA + " " + deltaModule.getName() + " " + LCBRACKET + "\n");
    for(IClassOperation cop: deltaModule.getOperations()) {
      cop.accept(this);
      out.print("\n");
    }
    out.print(RCBRACKET + "\n");
  }

  @Override
  public void visit(ClassAddition classAddition) {
    out.print(ADDS + " ");
    classAddition.getClasss().accept(this);
  }

  @Override
  public void visit(ClassModification classModification) {
    out.print(MODIFIES + " " + classModification.getBaseClass() + " ");
    if(classModification.getSuperClass() == null) { out.print(LCBRACKET + "\n"); }
    else { out.print(EXTENDING + " " + classModification.getSuperClass() + " " + LCBRACKET + "\n"); }
    for(IAttributeOperation op: classModification.getOperations()) {
      op.accept(this);
      out.print("\n");
    }
    out.print(RCBRACKET + "\n");
  }

  @Override
  public void visit(ClassRemoval classRemoval) { out.print(REMOVES + " " + classRemoval.getName()); }

  @Override
  public void visit(AttributeAddition attributeAddition) {
    out.print(ADDS + " ");
    attributeAddition.getAttribute().accept(this);
  }

  @Override
  public void visit(AttributeModification attributeModification) {
    out.print(MODIFIES + " ");
    attributeModification.getAttribute().accept(this);
  }

  @Override
  public void visit(AttributeRemoval attributeRemoval) { out.print(REMOVES + " " + attributeRemoval.getName()); }

  //////////////////////////////////////////////////////////////////////////////
  // 3. Base Code

  @Override
  public void visit(Classs classs) {
    out.print(CLASS + classs.getBaseClass() + " " + EXTENDS + " " + classs.getSuperClass() + " " + LCBRACKET + "\n");
    for(Attribute att: classs.getAttributes()) {
      att.accept(this);
    }
    out.print(RCBRACKET + "\n");
  }

  @Override
  public void visit(Attribute attribute) {
    Attribute.ISignature sig = attribute.getSignature();
    if(sig instanceof Attribute.SignatureMethod) {
      Attribute.SignatureMethod msig = (Attribute.SignatureMethod) sig;
      out.print(msig.rtype() + " " + attribute.getName() + LPAREN);
      Iterator<Pair<String,String>> itparam = msig.getParameters().iterator();
      while(itparam.hasNext()) {
        Pair<String,String> pair = itparam.next();
        out.print(pair.getFirst() + " " + pair.getSecond());
        if(itparam.hasNext()) { out.print(COMMA); }
      }
      out.print(RPAREN +  " " + LCBRACKET + "\n");

      Iterator<IExpression> itexp = msig.getExpressions().iterator();
      while(itexp.hasNext()) {
        IExpression exp = itexp.next();
        if(!itexp.hasNext()) { out.print(RETURN + " "); }
        exp.accept(this);
        out.print(SEMICOLON + " ");
      }
      out.print(RCBRACKET + "\n");
    } else {
      Attribute.SignatureField fsig = (Attribute.SignatureField)sig;
      out.print(fsig.type() + " " + attribute.getName() + SEMICOLON);
    }
  }

  @Override
  public void visit(ExpressionAccess expressionAccess) {
    IExpression exp = expressionAccess.getBase();
    if(exp != null) { exp.accept(this); out.print(DOT); }
    out.print(expressionAccess.getName());
  }

  @Override
  public void visit(ExpressionAssign expressionAssign) {
    expressionAssign.getReference().accept(this);
    out.print(" " + ASSIGN + " ");
    expressionAssign.getValue().accept(this);
  }

  @Override
  public void visit(ExpressionCast expressionCast) {
    out.print(LPAREN + expressionCast.getType() + RPAREN + LPAREN);
    expressionCast.getExpression().accept(this);
    out.print(RPAREN);
  }

  @Override
  public void visit(ExpressionMethodCall expressionMethodCall) {
    IExpression exp = expressionMethodCall.getBase();
    if(exp != null) { exp.accept(this); out.print(DOT); }
    out.print(expressionMethodCall.getName() + LPAREN);
    Iterator<IExpression> itparam = expressionMethodCall.getParameters().iterator();
    while(itparam.hasNext()) {
      itparam.next().accept(this);
      if(itparam.hasNext()) { out.print(COMMA + " "); }
    }
    out.print(RPAREN);
  }

  @Override
  public void visit(ExpressionNew expressionNew) { out.print(NEW + " " + expressionNew.getType()); }

  @Override
  public void visit(LiteralInteger literalInteger) { out.print(literalInteger.getValue()); }

  @Override
  public void visit(LiteralString literalString) { out.print(literalString.getValue()); }


  //////////////////////////////////////////////////////////////////////////////
  // 4. Formula

  @Override
  public void visit(FormulaAnd formulaAnd) { out.print(formulaAnd.toString()); }

  @Override
  public void visit(FormulaFalse formulaFalse) { out.print(formulaFalse.toString()); }

  @Override
  public void visit(FormulaTrue formulaTrue) { out.print(formulaTrue.toString()); }

  @Override
  public void visit(FormulaImplies formulaImplies) { out.print(formulaImplies.toString()); }

  @Override
  public void visit(FormulaEquivalent formulaEquivalent) { out.print(formulaEquivalent.toString()); }

  @Override
  public void visit(FormulaNot formulaNot) { out.print(formulaNot.toString()); }

  @Override
  public void visit(FormulaOr formulaOr) { out.print(formulaOr.toString()); }

  @Override
  public <I> void visit(FormulaPredicate<I> formulaPredicate) { out.print(formulaPredicate.toString()); }

}

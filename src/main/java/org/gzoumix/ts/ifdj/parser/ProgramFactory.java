package org.gzoumix.ts.ifdj.parser;
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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.gzoumix.ts.ifdj.data.SPLS;
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
import org.gzoumix.ts.ifdj.util.Reference;
import org.gzoumix.util.Global;
import org.gzoumix.util.data.Pair;
import org.gzoumix.util.syntax.ANTLRHelper;
import org.gzoumix.util.syntax.Position;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ProgramFactory {

  public static Program create(Collection<String> fileNames) {
    List<Pair<String, IFDJParser.CompilationUnitContext>> list = new LinkedList<>();
    ANTLRHelper.ParserConstructor<IFDJParser> constructor = new ANTLRHelper.ParserConstructor<>(IFDJLexer.class, IFDJParser.class);
    for(String fileName: fileNames) {
      IFDJParser parser = constructor.file(fileName);
      list.add(new Pair<>(fileName, parser.compilationUnit()));
    }

    if(Global.log.hasError()) {
      return null;
    } else {
      ProgramFactory factory = new ProgramFactory();
      for(Pair<String, IFDJParser.CompilationUnitContext> ctx: list) {
        factory.compilationUnit(ctx.getFirst(), ctx.getSecond());
      }
      return factory.res;
    }
  }



  private String fileName;
  private Program res;
  private IFormulaElement delta;
  private String nameClass;
  private ProgramFactory() { this.res = new Program(); }

  private Position extractPositionFrom(ParserRuleContext ctx) {
    return ANTLRHelper.extractPositionFrom(this.fileName, ctx);
  }

  private Position extractPositionFrom(TerminalNode ctx) {
    return ANTLRHelper.extractPositionFrom(this.fileName, ctx);
  }

    //////////////////////////////////////////////////
  // 0. DECLARATIONS
  //////////////////////////////////////////////////


  private void compilationUnit(String fileName, IFDJParser.CompilationUnitContext ctx) {
    this.fileName = fileName;
    for(IFDJParser.DeclarationContext decl: ctx.declaration()) {
      this.declaration(decl);
    }
  }

  private void declaration(IFDJParser.DeclarationContext ctx) {
    IFDJParser.FeatureSetDeclarationContext fmFeatureDecl = ctx.featureSetDeclaration();
    if(fmFeatureDecl != null) { this.featureSetDeclaration(fmFeatureDecl); }

    IFDJParser.FeatureConfigurationDeclarationContext fmConfigurationDecl = ctx.featureConfigurationDeclaration();
    if(fmConfigurationDecl != null) { this.featureConfigurationDeclaration(fmConfigurationDecl); }

    IFDJParser.DeltaOrderingDeclarationContext ckOrderingDecl = ctx.deltaOrderingDeclaration();
    if(ckOrderingDecl != null) { this.deltaOrderingDeclaration(ckOrderingDecl); }

    IFDJParser.DeltaActivationDeclarationContext ckActivationDecl = ctx.deltaActivationDeclaration();
    if(ckActivationDecl != null) { this.deltaActivationDeclaration(ckActivationDecl); }

    IFDJParser.DeltaDeclarationContext deltaDecl = ctx.deltaDeclaration();
    if(deltaDecl != null) { this.res.addDeltaModule(this.deltaDeclaration(deltaDecl)); }

    this.delta = SPLS.DELTA_CORE;
    IFDJParser.ClassDeclarationContext classDecl = ctx.classDeclaration();
    if(classDecl != null) { this.res.addClass(this.classDeclaration(classDecl)); }
  }

  private void featureSetDeclaration(IFDJParser.FeatureSetDeclarationContext ctx) {
    for(TerminalNode feature: ctx.ID()) {
      this.res.addFeature(new Feature(this.extractPositionFrom(feature), feature.getText()));
    }
  }

  private void featureConfigurationDeclaration(IFDJParser.FeatureConfigurationDeclarationContext ctx) {
    this.res.addConfiguration(new Configuration(this.extractPositionFrom(ctx), this.formula(ctx.formula())));
  }

  private void deltaOrderingDeclaration(IFDJParser.DeltaOrderingDeclarationContext ctx) {
    for(IFDJParser.DeltaOrderingStatementContext stmt: ctx.deltaOrderingStatement()) {
      Position pos = this.extractPositionFrom(stmt);
      Iterator<IFDJParser.DeltaListStatementContext> i = stmt.deltaListStatement().iterator();
      IFDJParser.DeltaListStatementContext prev = i.next();
      while(i.hasNext()) {
        IFDJParser.DeltaListStatementContext next = i.next();
        for(TerminalNode left: prev.ID()) {
          for(TerminalNode right: next.ID()) {
            System.out.println("Adding delta order: " + left.getText() + " < "+ right.getText());
            this.res.addDeltaOrdering(new DeltaOrdering(pos, left.getText(), right.getText()));
          }
        }
        prev = next;
      }
    }
  }

  private void deltaActivationDeclaration(IFDJParser.DeltaActivationDeclarationContext ctx) {
    for(IFDJParser.DeltaActivationStatementContext stmt: ctx.deltaActivationStatement()) {
      IFormula formula = this.formula(stmt.formula());
      Position pos = this.extractPositionFrom(stmt);
      for(TerminalNode delta: stmt.deltaListStatement().ID()) {
        this.res.addDeltaActivation(new DeltaActivation(pos, delta.getText(), formula));
      }
    }
  }


  //////////////////////////////////////////////////
  // 1. DELTAS
  //////////////////////////////////////////////////

  private DeltaModule deltaDeclaration(IFDJParser.DeltaDeclarationContext ctx) {
    String name = ctx.name.getText();
    DeltaModule res = new DeltaModule(this.extractPositionFrom(ctx), name);
    this.delta = new FormulaPredicate<String>(name);
    for(IFDJParser.ClassOperationContext op: ctx.classOperation()) {
      res.addOperation(this.classOperation(op));
    }
    return res;
  }

  private IClassOperation classOperation(IFDJParser.ClassOperationContext ctx) {
    if(ctx instanceof IFDJParser.ClassOperationAddsContext) {
      return this.classOperationAdds((IFDJParser.ClassOperationAddsContext) ctx);
    } else if(ctx instanceof IFDJParser.ClassOperationRemovesContext) {
      return this.classOperationRemoves((IFDJParser.ClassOperationRemovesContext) ctx);
    } else if(ctx instanceof IFDJParser.ClassOperationModifiesContext) {
      return this.classOperationModifies((IFDJParser.ClassOperationModifiesContext) ctx);
    } else { // there is a problem
      Global.log.logError("at " + this.extractPositionFrom(ctx).toString() +": Unknown class operation \"" + ctx.getClass().getCanonicalName() + "\"");
      return null;
    }
  }

  private ClassAddition classOperationAdds(IFDJParser.ClassOperationAddsContext ctx) {
    return new ClassAddition(this.extractPositionFrom(ctx), this.classDeclaration(ctx.classDeclaration()));
  }

  private ClassRemoval classOperationRemoves(IFDJParser.ClassOperationRemovesContext ctx) {
    return new ClassRemoval(this.extractPositionFrom(ctx), ctx.name.getText());
  }

  private ClassModification classOperationModifies(IFDJParser.ClassOperationModifiesContext ctx) {
    String name = ctx.name.getText();
    ClassModification res = new ClassModification(this.extractPositionFrom(ctx), this.delta, name);

    if (ctx.superclass != null) { res.setSuper(ctx.superclass.getText()); }

    this.nameClass = name;
    for (IFDJParser.AttributeOperationContext app : ctx.attributeOperation()) {
      res.addOperation(this.attributeOperation(app));
    }
    return res;
  }


  private IAttributeOperation attributeOperation(IFDJParser.AttributeOperationContext ctx) {
    if(ctx instanceof IFDJParser.AttributeOperationAddsContext) {
      return this.attributeOperationAdds((IFDJParser.AttributeOperationAddsContext) ctx);
    } else if(ctx instanceof IFDJParser.AttributeOperationModifiesContext) {
      return this.attributeOperationModifies((IFDJParser.AttributeOperationModifiesContext) ctx);
    } else if(ctx instanceof IFDJParser.AttributeOperationRemovesContext) {
      return this.AttributeOperationRemoves((IFDJParser.AttributeOperationRemovesContext) ctx);
    } else { // there is a problem
      Global.log.logError("at " + this.extractPositionFrom(ctx).toString() +": Unknown attribute operation \"" + ctx.getClass().getCanonicalName() + "\"");
      return null;
    }
  }

  private AttributeAddition attributeOperationAdds(IFDJParser.AttributeOperationAddsContext ctx) {
    return new AttributeAddition(this.extractPositionFrom(ctx), this.attributeDeclaration(ctx.attributeDeclaration()));
  }

  private AttributeModification attributeOperationModifies(IFDJParser.AttributeOperationModifiesContext ctx) {
    return new AttributeModification(this.extractPositionFrom(ctx), this.attributeDeclaration(ctx.attributeDeclaration()));
  }

  private AttributeRemoval AttributeOperationRemoves(IFDJParser.AttributeOperationRemovesContext ctx) {
    return new AttributeRemoval(this.extractPositionFrom(ctx), this.nameClass, ctx.name.getText());
  }


  //////////////////////////////////////////////////
  // 2. CLASSES
  //////////////////////////////////////////////////


  private Classs classDeclaration(IFDJParser.ClassDeclarationContext ctx) {
    String name = ctx.name.getText();
    String superClass = ctx.superclass.getText();
    Classs res = new Classs(this.extractPositionFrom(ctx), this.delta, name, superClass);

    this.nameClass = name;
    for(IFDJParser.AttributeDeclarationContext attribute: ctx.attributeDeclaration()) {
      res.addAttribute(this.attributeDeclaration(attribute));
    }
    return res;
  }

  private Attribute attributeDeclaration(IFDJParser.AttributeDeclarationContext ctx) {
    if(ctx instanceof IFDJParser.FieldDeclarationContext) {
      return this.fieldDeclaration((IFDJParser.FieldDeclarationContext)ctx);
    } else if (ctx instanceof IFDJParser.MethodDeclarationContext) {
      return this.methodDeclaration((IFDJParser.MethodDeclarationContext) ctx);
    } else { // there is a problem
      Global.log.logError("at " + this.extractPositionFrom(ctx).toString() +": Unknown attribute declaration \"" + ctx.getClass().getCanonicalName() + "\"");
      return null;
    }
  }

  private Attribute fieldDeclaration(IFDJParser.FieldDeclarationContext ctx) {
    Attribute.SignatureField sig = new Attribute.SignatureField(ctx.type.getText());
    return new Attribute(this.extractPositionFrom(ctx), this.nameClass, ctx.name.getText(), sig);
  }

  private Attribute methodDeclaration(IFDJParser.MethodDeclarationContext ctx) {
    Attribute.SignatureMethod sig = new Attribute.SignatureMethod(ctx.rtype.getText());

    for(IFDJParser.MethodParameterContext param: ctx.methodParameter()) {
      sig.addParameter(param.type.getText(), param.name.getText());
    }

    for(IFDJParser.ExpressionContext expression: ctx.expression()) {
      sig.addExpression(this.expression(expression));
    }

    return new Attribute(this.extractPositionFrom(ctx), this.nameClass, ctx.name.getText(), sig);
  }



  //////////////////////////////////////////////////
  // 3. EXPRESSIONS
  //////////////////////////////////////////////////


  private IExpression expression(IFDJParser.ExpressionContext ctx) {
    if(ctx instanceof IFDJParser.ExpressionVariableContext) {
      return this.expressionVariable((IFDJParser.ExpressionVariableContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionAccessContext) {
      return this.expressionAccess((IFDJParser.ExpressionAccessContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionMethodCallContext) {
      return this.expressionMethodCall((IFDJParser.ExpressionMethodCallContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionNewContext) {
      return this.expressionNew((IFDJParser.ExpressionNewContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionCastContext) {
      return this.expressionCast((IFDJParser.ExpressionCastContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionAssignContext) {
      return this.expressionAssign((IFDJParser.ExpressionAssignContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionInnerContext) {
      return this.expressionInner((IFDJParser.ExpressionInnerContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionNullContext) {
      return this.expressionNull((IFDJParser.ExpressionNullContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionOpUnaryContext) {
      return this.expressionOpUnary((IFDJParser.ExpressionOpUnaryContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionOpBinaryContext) {
      return this.expressionOpBinary((IFDJParser.ExpressionOpBinaryContext)ctx);
    } else if(ctx instanceof IFDJParser.ExpressionLiteralIntegerContext) {
      return this.expressionLiteralInteger((IFDJParser.ExpressionLiteralIntegerContext) ctx);
    } else if(ctx instanceof IFDJParser.ExpressionLiteralStringContext) {
      return this.expressionLiteralString((IFDJParser.ExpressionLiteralStringContext) ctx);
    } else { // there is a problem
      Global.log.logError("at " + this.extractPositionFrom(ctx).toString() +": Unknown expression \"" + ctx.getClass().getCanonicalName() + "\"");
      return null;
    }
  }

  private ExpressionAccess expressionVariable(IFDJParser.ExpressionVariableContext ctx) {
    return  new ExpressionAccess(this.extractPositionFrom(ctx), ctx.name.getText());
  }

  private ExpressionAccess expressionAccess(IFDJParser.ExpressionAccessContext ctx) {
    return new ExpressionAccess(this.extractPositionFrom(ctx), this.expression(ctx.base), ctx.name.getText());
  }

  private ExpressionMethodCall expressionMethodCall(IFDJParser.ExpressionMethodCallContext ctx) {
    IExpression tmp = this.expression(ctx.base);
    if(tmp instanceof ExpressionAccess) {
      ExpressionAccess access = (ExpressionAccess)tmp;
      System.out.print("!! found method call at [" + this.extractPositionFrom(ctx).toString() + "]: " + access.getName() + "/" + ctx.params.size());
      ExpressionMethodCall res = new ExpressionMethodCall(this.extractPositionFrom(ctx), access.getBase(), access.getName());
      for(IFDJParser.ExpressionContext exp: ctx.params) {
        System.out.print("  -> " + exp.getText());
        res.addParameter(this.expression(exp));
      }
      System.out.println();
      return res;
    } else {  // there is a problem
      Global.log.logError("at " + this.extractPositionFrom(ctx).toString() +": Cannot call a method a non-reference expression");
      return null;
    }
  }

  private ExpressionNew expressionNew(IFDJParser.ExpressionNewContext ctx) {
    return new ExpressionNew(this.extractPositionFrom(ctx), ctx.name.getText());
  }

  private ExpressionCast expressionCast(IFDJParser.ExpressionCastContext ctx) {
    return new ExpressionCast(this.extractPositionFrom(ctx), ctx.type.getText(), this.expression(ctx.expression()));
  }

  private ExpressionAssign expressionAssign(IFDJParser.ExpressionAssignContext ctx) {
    IExpression exp = this.expression(ctx.left);
    if(exp instanceof ExpressionAccess) {
      return new ExpressionAssign(this.extractPositionFrom(ctx), (ExpressionAccess) exp, this.expression(ctx.right));
    } else {  // there is a problem
      Global.log.logError("at " + this.extractPositionFrom(ctx).toString() +": Cannot assign to a non-reference expression");
      return null;
    }
  }

  private IExpression expressionInner(IFDJParser.ExpressionInnerContext ctx) {
    return this.expression(ctx.expression());
  }

  private ExpressionAccess expressionNull(IFDJParser.ExpressionNullContext ctx) {
    return new ExpressionAccess(this.extractPositionFrom(ctx), Reference.NULL);
  }

  private ExpressionMethodCall expressionOpUnary(IFDJParser.ExpressionOpUnaryContext ctx) {
    ExpressionMethodCall res = new ExpressionMethodCall(this.extractPositionFrom(ctx), ctx.OP_UNARY().getText());
    res.addParameter(this.expression(ctx.expression()));
    return res;
  }

  private ExpressionMethodCall expressionOpBinary(IFDJParser.ExpressionOpBinaryContext ctx) {
    ExpressionMethodCall res = new ExpressionMethodCall(this.extractPositionFrom(ctx), ctx.OP_BINARY().getText());
    res.addParameter(this.expression(ctx.left));
    res.addParameter(this.expression(ctx.right));
    return res;
  }

  private LiteralInteger expressionLiteralInteger(IFDJParser.ExpressionLiteralIntegerContext ctx) {
    return new LiteralInteger(this.extractPositionFrom(ctx), Integer.parseInt(ctx.getText()));
  }

  private LiteralString expressionLiteralString(IFDJParser.ExpressionLiteralStringContext ctx) {
    return new LiteralString(this.extractPositionFrom(ctx), ctx.getText());
  }


  //////////////////////////////////////////////////
  // 4. FORMULA
  //////////////////////////////////////////////////

  private IFormula formula(IFDJParser.FormulaContext ctx) {
    if(ctx instanceof IFDJParser.FormulaInnerContext) {
      return this.formulaInner((IFDJParser.FormulaInnerContext)ctx);
    } else if(ctx instanceof IFDJParser.FormulaTrueContext) {
      return this.formulaTrue((IFDJParser.FormulaTrueContext)ctx);
    } else if(ctx instanceof IFDJParser.FormulaFalseContext) {
      return this.formulaFalse((IFDJParser.FormulaFalseContext)ctx);
    } else if(ctx instanceof IFDJParser.FormulaPredicateContext) {
      return this.formulaPredicate((IFDJParser.FormulaPredicateContext)ctx);
    } else if(ctx instanceof IFDJParser.FormulaNegContext) {
      return this.formulaNeg((IFDJParser.FormulaNegContext)ctx);
    } else if(ctx instanceof IFDJParser.FormulaAndContext) {
      return this.formulaAnd((IFDJParser.FormulaAndContext)ctx);
    } else if(ctx instanceof IFDJParser.FormulaOrContext) {
      return this.formulaOr((IFDJParser.FormulaOrContext)ctx);
    } else if(ctx instanceof IFDJParser.FormulaImpliesContext) {
      return this.formulaImplies((IFDJParser.FormulaImpliesContext)ctx);
    } else {// there is a problem
      Global.log.logError("at " + this.extractPositionFrom(ctx).toString() +": Unknown formula \"" + ctx.getClass().getCanonicalName() + "\"");
      return null;
    }
  }

  private IFormula formulaInner(IFDJParser.FormulaInnerContext ctx) {
    return this.formula(ctx.inner);
  }

  private FormulaTrue formulaTrue(IFDJParser.FormulaTrueContext ctx) {
    return new FormulaTrue(this.extractPositionFrom(ctx));
  }

  private FormulaFalse formulaFalse(IFDJParser.FormulaFalseContext ctx) {
    return new FormulaFalse(this.extractPositionFrom(ctx));
  }

  private FormulaPredicate<String> formulaPredicate(IFDJParser.FormulaPredicateContext ctx) {
    return new FormulaPredicate<>(this.extractPositionFrom(ctx), ctx.predicate.getText());
  }

  private FormulaNot formulaNeg(IFDJParser.FormulaNegContext ctx) {
    return new FormulaNot(this.extractPositionFrom(ctx), this.formula(ctx.formula()));
  }

  private FormulaAnd formulaAnd(IFDJParser.FormulaAndContext ctx) {
    FormulaAnd res = new FormulaAnd(this.extractPositionFrom(ctx));
    IFormula tmp;
    tmp = this.formula(ctx.left);
    if(tmp instanceof FormulaAnd) { res.addDirect((FormulaAnd)tmp); }
    else {res.add(tmp); }
    tmp = this.formula(ctx.right);
    if(tmp instanceof FormulaAnd) { res.addDirect((FormulaAnd)tmp); }
    else {res.add(tmp); }

    return res;
  }

  private FormulaOr formulaOr(IFDJParser.FormulaOrContext ctx) {
    FormulaOr res = new FormulaOr(this.extractPositionFrom(ctx));
    IFormula tmp;
    tmp = this.formula(ctx.left);
    if(tmp instanceof FormulaOr) { res.addDirect((FormulaOr)tmp); }
    else {res.add(tmp); }
    tmp = this.formula(ctx.right);
    if(tmp instanceof FormulaOr) { res.addDirect((FormulaOr)tmp); }
    else {res.add(tmp); }

    return res;
  }

  private FormulaImplies formulaImplies(IFDJParser.FormulaImpliesContext ctx) {
    return new FormulaImplies(this.extractPositionFrom(ctx), this.formula(ctx.left), this.formula(ctx.right));
  }
}

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

import org.gzoumix.ts.ifdj.data.DMST;
import org.gzoumix.ts.ifdj.data.FCS;
import org.gzoumix.ts.ifdj.data.SPLS;
import org.gzoumix.ts.ifdj.data.syntax.IASTNode;
import org.gzoumix.ts.ifdj.data.syntax.ISuperClassDeclaration;
import org.gzoumix.ts.ifdj.data.syntax.core.Attribute;
import org.gzoumix.ts.ifdj.data.syntax.core.Classs;
import org.gzoumix.ts.ifdj.data.syntax.core.Program;
import org.gzoumix.ts.ifdj.data.syntax.delta.*;
import org.gzoumix.ts.ifdj.data.syntax.expression.*;
import org.gzoumix.ts.ifdj.data.syntax.formula.*;
import org.gzoumix.ts.ifdj.data.syntax.visitor.VisitorBasic;
import org.gzoumix.ts.ifdj.util.Reference;
import org.gzoumix.util.Pair;
import org.gzoumix.util.Triplet;

import java.util.*;


public class Consistency extends VisitorBasic {

  public static Pair<IFormula,IFormula> create(SPLS spls) {
    Consistency factory = new Consistency(spls);

    IFormula dependency = DependencyConstraintFactory.create(factory);
    IFormula applicability = ApplicabilityConstraintFactory.create(factory);
    return new Pair<>(dependency, applicability);
  }


  private SPLS spls;
  private Consistency(SPLS spls) {
    this.spls = spls;

    this.inheritanceOverriddenMap = new HashMap<>();
    this.lookupSubtypeMap = new HashMap<>();

    this.lookupAttributeMap = new HashMap<>();
    this.lookupClassMap = new HashMap<>();
  }


  ///////////////////////////////////////////////////////////////////////////////
  // 0. UTILITY FUNCTIONS
  ///////////////////////////////////////////////////////////////////////////////

  private Map<Triplet<IFormulaElement, String, String>, IFormula> inheritanceOverriddenMap;
  private IFormula inheritanceNotOverridden(IFormulaElement delta, String baseClass, String superClass) {
    IFormula res;
    Triplet<IFormulaElement, String, String> key = new Triplet<>(delta, baseClass, superClass);

    res = this.inheritanceOverriddenMap.get(key);
    if(res != null) { return res; }
    else {
      Set<IFormulaElement> deltas = new HashSet<>();
      for (String classs : this.spls.getClasses()) {
        if (!classs.equals(superClass)) {
          deltas.addAll(spls.getDMST().ext(baseClass, classs));
        }
      }
      deltas.retainAll(spls.getAfterDelta(delta));

      FormulaAnd tmpres = new FormulaAnd();
      for (IFormulaElement deltaNext : deltas) {
        tmpres.add(new FormulaNot(deltaNext));
      }
      res = tmpres;
    }

    this.inheritanceOverriddenMap.put(key, res);
    return res;
  }


  private Map<Pair<String,String>,IFormula> lookupSubtypeMap;
  private IFormula lookupSubtype(String C1, String C2) {

    if(C1.equals(C2)) { return Reference.FORMULA_TRUE; }

    IFormula res;
    Pair<String,String>ref = new Pair<>(C1, C2);

    res = this.lookupSubtypeMap.get(ref);
    if (res == null) {
      if (C2.equals(Reference.NAME_OBJECT_CLASS)) {
        return Reference.FORMULA_TRUE;
      } else if (C1.equals(Reference.NAME_NULL_TYPE)) {
        return Reference.FORMULA_TRUE;
      } else {
        Set<Set<ISuperClassDeclaration>> inh = this.spls.getInh(C1, C2);
        if (inh == null) {
          System.out.println("No inheritance path found for: " + C1 + " < " + C2);
          res = Reference.FORMULA_FALSE;
        } else {
          FormulaOr tmpres = new FormulaOr();
          for (Set<ISuperClassDeclaration> decls : inh) {
            FormulaAnd land = new FormulaAnd();
            for (ISuperClassDeclaration decl : decls) {
              /*FormulaOr lor = new FormulaOr(); // this is the implementation in the paper, which uses less precise data
              for (IFormulaElement delta : this.spls.getDMST().ext(decl.getBaseClass(), decl.getSuperClass())) {
                FormulaAnd base = new FormulaAnd();
                base.add(delta);
                base.add(this.inheritanceNotOverridden(delta, decl.getBaseClass(), decl.getSuperClass()));
                lor.add(base);
              }
              land.add(lor);*/
              land.add(decl.getDelta());
              land.add(this.inheritanceNotOverridden(decl.getDelta(), decl.getBaseClass(), decl.getSuperClass()));
            }
            tmpres.add(land);
          }
          res = tmpres;
          this.lookupSubtypeMap.put(ref, res);
        }
      }
    }
    return res;
  }

  private Map<Pair<String,String>, IFormula> lookupAttributeMap;
  private IFormula lookup(String baseClass, String att) {
    IFormula res;
    Pair<String,String> ref = new Pair<>(baseClass, att);

    res = lookupAttributeMap.get(ref);
    if(res == null) {
      FormulaOr tmpres = new FormulaOr();
      for(Pair<IFormulaElement,String> dc: this.spls.getDMST().defAtt(att)) {
        IFormulaElement delta = dc.getFirst();
        String superClass = dc.getSecond();
        FormulaAnd land = new FormulaAnd();
        land.add(dc.getFirst());
        land.add(this.lookupSubtype(baseClass, superClass));
        Set<IFormulaElement> deltasAfter = new HashSet<>(this.spls.getDMST().remove(baseClass, att)); // those that remove this attribute
        deltasAfter.addAll(this.spls.getDMST().remove(baseClass));  // or those that remove the whole class
        deltasAfter.retainAll(this.spls.getAfterDelta(delta));
        for(IFormulaElement deltaAfter: deltasAfter) {
          land.add(new FormulaNot(deltaAfter));
        }
        tmpres.add(land);
      }
      if(tmpres.size() != 0) {
        System.out.println("found");
      } else { System.out.println("Not found"); }

      res = tmpres;
      this.lookupAttributeMap.put(ref, res);
    } else { System.out.println("already in mapping"); }
    return res;
  }

  private Map<String, IFormula> lookupClassMap;
  private IFormula lookup(String baseClass) {
    IFormula res;

    System.out.print("looking for class " + baseClass + "... ");

    res = this.lookupClassMap.get(baseClass);
    if(res == null) {
      for(FCS fcs: Reference.classes.keySet()) {
        if(fcs.getName().equals(baseClass)) {
          System.out.println("found in STDLibrary");
          res = Reference.FORMULA_TRUE;
          break;
        }
      }
      if(res == null) {
        FormulaOr tmpres = new FormulaOr();
        Set<IFormulaElement> deltasRemove = new HashSet<>(this.spls.getDMST().remove(baseClass));
        for (IFormulaElement delta : this.spls.getDMST().defClass(baseClass)) {
          Set<IFormulaElement> deltasAfter = new HashSet<>(deltasRemove);
          deltasAfter.retainAll(this.spls.getAfterDelta(delta));
          FormulaAnd land = new FormulaAnd();
          land.add(delta);
          for (IFormulaElement deltaAfter : deltasAfter) {
            land.add(new FormulaNot(deltaAfter));
          }
          tmpres.add(land);
        }
        res = tmpres;
        if(tmpres.size() > 0) { System.out.println("found"); }
        else { System.out.println("not found"); }
      }
      this.lookupClassMap.put(baseClass, res);
    } else { System.out.println("already in mapping"); }
    return res;
  }


  ///////////////////////////////////////////////////////////////////////////////
  // 1. DEPENDENCY GENERATION
  ///////////////////////////////////////////////////////////////////////////////

  private static class DependencyConstraintFactory extends VisitorBasic {
    public static IFormula create(Consistency main) {
      DependencyConstraintFactory factory = new DependencyConstraintFactory(main);
      factory.visit(main.spls.getProgram());
      return factory.res;
    }

    private FormulaAnd res;
    private Consistency main;
    private DMST dmst;
    private Map<IASTNode, IFormula>deps;
    private TypingEnvironment environment;
    private DependencyConstraintFactory(Consistency main) {
      this.main = main;
      this.dmst= this.main.spls.getDMST();
      this.res = new FormulaAnd();
      this.deps = new HashMap<>();
      this.environment = new TypingEnvironment(this.main.spls.getLookup());
    }


    @Override
    public void visit(Program program) {
      super.visit(program);
      for(DeltaModule delta: program.getDeltas()) {
        res.add(this.deps.get(delta));
      }

      for(Classs classs: program.getClasses()) {
        res.add(this.deps.get(classs));
      }
    }

    @Override
    public void visit(DeltaModule deltaModule) {
      this.environment.enterDelta(deltaModule.getID());
      super.visit(deltaModule);

      FormulaAnd land = new FormulaAnd();
      for(IClassOperation op: deltaModule.getOperations()) {
        land.add(this.deps.get(op));
      }
      this.deps.put(deltaModule, new FormulaImplies(deltaModule.getID(), land));
      this.environment.exitDelta();
    }

    @Override
    public void visit(ClassAddition classAddition) {
      super.visit(classAddition);
      this.deps.put(classAddition, this.deps.get(classAddition.getClasss()));
    }

    @Override
    public void visit(ClassModification classModification) {
      this.environment.enterClass(classModification.getBaseClass());
      super.visit(classModification);

      FormulaAnd land = new FormulaAnd();
      for(IAttributeOperation op: classModification.getOperations()) {
        land.add(this.deps.get(op));
      }

      if(classModification.getSuperClass() != null) {
        land.add(this.ensureSuper(classModification.getBaseClass(), classModification.getSuperClass()));
      }

      this.deps.put(classModification, land);
      this.environment.exitClass();
    }

    @Override
    public void visit(ClassRemoval classRemoval) {
      super.visit(classRemoval);
      this.deps.put(classRemoval, Reference.FORMULA_TRUE);
    }

    @Override
    public void visit(AttributeAddition attributeAddition) {
      super.visit(attributeAddition);
      this.deps.put(attributeAddition, this.deps.get(attributeAddition.getAttribute()));
    }

    @Override
    public void visit(AttributeModification attributeModification) {
      Attribute.ISignature sig = attributeModification.getAttribute().getSignature();
      if(sig instanceof Attribute.SignatureMethod) {
        this.environment.setCurrentOriginal((Attribute.SignatureMethod) sig);
      }
      super.visit(attributeModification);
      this.deps.put(attributeModification, this.deps.get(attributeModification.getAttribute()));
    }

    @Override
    public void visit(AttributeRemoval attributeRemoval) {
      super.visit(attributeRemoval);
      this.deps.put(attributeRemoval, Reference.FORMULA_TRUE);
    }

    @Override
    public void visit(Classs classs) {
      this.environment.enterClass(classs.getBaseClass());
      super.visit(classs);

      FormulaAnd land = new FormulaAnd();
      for(Attribute att: classs.getAttributes()) {
        land.add(this.deps.get(att));
      }
      land.add(this.ensureSuper(classs.getBaseClass(), classs.getSuperClass()));
      this.deps.put(classs, land);
      this.environment.exitClass();
    }

    @Override
    public void visit(Attribute attribute) {
      Attribute.ISignature sig = attribute.getSignature();

      Set<IFormulaElement> rems = new HashSet<>(this.dmst.remove(this.environment.getCurrentClass()));
      rems.addAll(this.dmst.remove(this.environment.getCurrentClass(), attribute.getName()));
      rems.addAll(this.dmst.replace(this.environment.getCurrentClass(), attribute.getName()));
      rems.retainAll(this.main.spls.getAfterDelta(this.environment.getCurrentDelta()));
      FormulaAnd left = new FormulaAnd();
      for(IFormulaElement rem: rems) {
        left.add(new FormulaNot(rem));
      }

      if(sig instanceof Attribute.SignatureField) {
        this.deps.put(attribute, new FormulaImplies(left, this.main.lookup(((Attribute.SignatureField) sig).type())));
      } else {
        Attribute.SignatureMethod msig = (Attribute.SignatureMethod)sig;
        this.environment.enterMethod(msig.getParameters());

        super.visit(attribute);

        this.environment.exitMethod();
        FormulaAnd landConclusion = new FormulaAnd();
        landConclusion.add(this.main.lookup(msig.rtype()));
        for(String tparam: msig.ptypes()) { landConclusion.add(this.main.lookup(tparam)); }
        for(IExpression exp: msig.getExpressions()) { landConclusion.add(this.deps.get(exp)); }

        /*FormulaAnd landNotDelta = new FormulaAnd();
        Set<IFormulaElement> notDeltas = new HashSet<>(this.dmst.replace(this.environment.getCurrentClass(), attribute.getName()));
        notDeltas.addAll(this.dmst.remove(this.environment.getCurrentClass(), attribute.getName()));
        if(this.environment.getCurrentDelta() != null) { notDeltas.retainAll(this.main.spls.getAfterDelta(this.environment.getCurrentDelta())); }
        for(IFormulaElement notDelta: notDeltas) { landNotDelta.add(new FormulaNot(notDelta)); }*/

        this.deps.put(attribute, new FormulaImplies(left, landConclusion));
      }
    }

    @Override
    public void visit(ExpressionAccess expressionAccess) {
      super.visit(expressionAccess);

      IExpression base = expressionAccess.getBase();
      if(base != null) {
        FormulaAnd land = new FormulaAnd();
        land.add(this.deps.get(base));
        land.add(lookup(base.getType(), expressionAccess.getName()));
        this.deps.put(expressionAccess, land);
      } else {
        this.deps.put(expressionAccess, this.lookup(expressionAccess.getName()));
      }
    }

    @Override
    public void visit(ExpressionAssign expressionAssign) {
      super.visit(expressionAssign);
      FormulaAnd land = new FormulaAnd();
      land.add(this.deps.get(expressionAssign.getReference()));
      land.add(this.deps.get(expressionAssign.getValue()));
      System.out.println("at [" + expressionAssign.getPosition() + "] looking for " + expressionAssign.getValue().getType() + " < " + expressionAssign.getReference().getType());
      land.add(this.main.lookupSubtype(expressionAssign.getValue().getType(), expressionAssign.getReference().getType()));
      this.deps.put(expressionAssign, land);
    }

    @Override
    public void visit(ExpressionCast expressionCast) {
      super.visit(expressionCast);
      FormulaAnd land = new FormulaAnd();
      land.add(this.deps.get(expressionCast.getExpression()));

      FormulaOr lor = new FormulaOr();
      System.out.println("at [" + expressionCast.getPosition() + "] looking for " +expressionCast.getExpression().getType() + " <> " +  expressionCast.getType());
      lor.add(this.main.lookupSubtype(expressionCast.getExpression().getType(), expressionCast.getType()));
      lor.add(this.main.lookupSubtype(expressionCast.getType(), expressionCast.getExpression().getType()));

      land.add(lor);
      this.deps.put(expressionCast, land);
    }

    @Override
    public void visit(ExpressionMethodCall expressionMethodCall) {
      super.visit(expressionMethodCall);

      FormulaAnd land = new FormulaAnd();
      Attribute.SignatureMethod msig;

      IExpression base = expressionMethodCall.getBase();
      if(base != null) {
        String baseClass = base.getType();
        //System.out.println("Consistency: visiting method call \"" + baseClass + "." + expressionMethodCall.getName() + "\"");
        msig = (Attribute.SignatureMethod)this.environment.lookup(baseClass, expressionMethodCall.getName());
        land.add(this.deps.get(base));
        land.add(lookup(baseClass, expressionMethodCall.getName()));
      } else {
        //baseClass = this.currentClass;
        //System.out.println("Consistency: visiting method call \"" + expressionMethodCall.getName() + "\"");
        msig = (Attribute.SignatureMethod)this.environment.lookup(expressionMethodCall.getName());
        land.add(lookup(expressionMethodCall.getName()));
      }

      Iterator<String> i = msig.ptypes().iterator();
      for(IExpression exp: expressionMethodCall.getParameters()) {
        land.add(this.deps.get(exp));
        String superClass = i.next();
        System.out.println("at [" + exp.getPosition() + "] looking for " + exp.getType() + " < " + superClass);
        land.add(this.main.lookupSubtype(exp.getType(), superClass));
      }


      this.deps.put(expressionMethodCall, land);
    }

    @Override
    public void visit(ExpressionNew expressionNew) {
      super.visit(expressionNew);
      this.deps.put(expressionNew, this.main.lookup(expressionNew.getType()));
    }

    @Override
    public void visit(LiteralInteger literalInteger) {
      super.visit(literalInteger);
      this.deps.put(literalInteger, Reference.FORMULA_TRUE);
    }

    @Override
    public void visit(LiteralString literalString) {
      super.visit(literalString);
      this.deps.put(literalString, Reference.FORMULA_TRUE);
    }

    private IFormula ensureSuper(String baseClass, String superClass) {
      DMST dmst = this.main.spls.getDMST();
      Set<IFormulaElement> S = new HashSet<>(dmst.replace(baseClass));
      S.addAll(dmst.remove(baseClass));
      if(this.environment.getCurrentDelta() != null) { S.retainAll(this.main.spls.getAfterDelta(this.environment.getCurrentDelta())); }
      FormulaAnd notDelta = new FormulaAnd();
      for(IFormulaElement deltaAfter: S) { notDelta.add(new FormulaNot(deltaAfter)); }
      return new FormulaImplies(notDelta, this.main.lookup(superClass));
    }

    /*
    private Attribute.SignatureMethod lookupSignatureMethod(String method) {
      Attribute.SignatureMethod res;

      // 1. look operators
      res = Reference.Operator.operators.get(method);

      // 2. look local class
      if(res == null) {
        res = this.lookupSignatureMethod(this.currentClass, method);
      }
      return res;
    }

    private Attribute.SignatureMethod lookupSignatureMethod(String baseClass, String method) {
      Attribute.SignatureMethod res = null;

      if(baseClass.equals(this.currentClass)) {
        res = method.equals(Reference.ORIGINAL) ? this.currentOriginal : null;
      }

      if(res ==null) {
        res = (Attribute.SignatureMethod)(this.main.spls.getLookup().get(baseClass).get(method).getSignatures().entrySet().iterator().next().getKey());
      }

      return res;
    }*/


    private IFormula lookup(String att) {
      System.out.print("Looking for attribute " + att + "... ");

      if(this.environment.isDirectlyAccessible(att)) {
        System.out.println("it is directly accessible!");
        return Reference.FORMULA_TRUE;
      }

      return this.main.lookup(this.environment.getCurrentClass(), att);


    }

    private IFormula lookup(String baseClass, String att) {
      System.out.print("Looking for attribute " + baseClass + "." + att + "... ");

      if(this.environment.isDirectlyAccessible(baseClass, att)) {
        System.out.println("it is directly accessible!");
        return Reference.FORMULA_TRUE;
      }

      return this.main.lookup(baseClass, att);
    }
  }




  ///////////////////////////////////////////////////////////////////////////////
  // 2. APPLICABILITY CONSTRAINT GENERATION
  ///////////////////////////////////////////////////////////////////////////////

  private static class ApplicabilityConstraintFactory {
    public static IFormula create(Consistency main) {
      ApplicabilityConstraintFactory factory = new ApplicabilityConstraintFactory(main);
      factory.generate();
      return factory.res;
    }

    private Consistency main;
    private DMST dmst;
    private FormulaAnd res;
    private ApplicabilityConstraintFactory(Consistency main) {
      this.main = main;
      this.dmst = this.main.spls.getDMST();
      this.res = new FormulaAnd();
    }

    private void generate() {
      for(String className: this.main.spls.getFCST().dom()) {
        this.res.add(this.applicability(className));
        for(String att: this.main.spls.getFCST().get(className).getAttributes().keySet()) {
          this.res.add(this.applicability(className, att));
        }
      }
    }

    private IFormula applicability(String className) {
      FormulaAnd res = new FormulaAnd();
      res.add(this.applicabilityAdd(className));
      res.add(this.applicabilityModify(className));
      res.add(this.applicabilityRemove(className));

      return res;
    }

    private IFormula applicability(String className, String att) {
      FormulaAnd res = new FormulaAnd();
      res.add(this.applicabilityAdd(className, att));
      res.add(this.applicabilityModify(className, att));
      res.add(this.applicabilityRemove(className, att));

      return res;
    }


    // 1. Classes
    private IFormula applicabilityAdd(String className) {
      Set<IFormulaElement> adds = this.dmst.defClass(className);
      Set<IFormulaElement> rems = this.dmst.remove(className);

      return this.applicabilityAdd(adds, rems);
    }

    private IFormula applicabilityModify(String className) {
      Set<IFormulaElement> mods = new HashSet<>(this.dmst.replace(className));
      mods.addAll(this.dmst.reuse(className));
      return this.applicabilityModifies(mods, this.dmst.defClass(className), this.dmst.remove(className));
    }

    private IFormula applicabilityRemove(String className) {
      Set<IFormulaElement> adds = this.dmst.defClass(className);
      Set<IFormulaElement> rems = this.dmst.remove(className);

      return this.applicabilityRemove(adds, rems);
    }


    // 2. Attributes
    private IFormula applicabilityAdd(String className, String att) {
      Set<IFormulaElement> adds = new HashSet<>();
      for(Pair<IFormulaElement,String> def: this.dmst.defAtt(att)) {
        if(def.getSecond().equals(className)) { adds.add(def.getFirst()); }
      }

      Set<IFormulaElement> rems = new HashSet<>(this.dmst.remove(className));
      rems.addAll(this.dmst.remove(className, att));
      return this.applicabilityAdd(adds, rems);
    }

    private IFormula applicabilityModify(String className, String att) {
      Set<IFormulaElement> mods = new HashSet<>(this.dmst.replace(className, att));
      mods.addAll(this.dmst.reuse(className, att));

      Set<IFormulaElement> adds = new HashSet<>();
      for(Pair<IFormulaElement,String> def: this.dmst.defAtt(att)) {
        if(def.getSecond().equals(className)) { adds.add(def.getFirst()); }
      }

      Set<IFormulaElement> rems = new HashSet<>(this.dmst.remove(className));
      rems.addAll(this.dmst.remove(className, att));

      return this.applicabilityModifies(mods, adds, rems);
    }

    private IFormula applicabilityRemove(String className, String att) {
      Set<IFormulaElement> adds = new HashSet<>();
      for(Pair<IFormulaElement,String> def: this.dmst.defAtt(att)) {
        if(def.getSecond().equals(className)) { adds.add(def.getFirst()); }
      }

      Set<IFormulaElement> rems = new HashSet<>(this.dmst.remove(className));
      rems.addAll(this.dmst.remove(className, att));
      return this.applicabilityRemove(adds, rems);
    }


    // 3. Delta Sets: Utility
    private IFormula applicabilityAdd(Set<IFormulaElement> adds, Set<IFormulaElement> rems) {
      FormulaAnd res = new FormulaAnd();

      for(IFormulaElement delta: adds) {
        Set<IFormulaElement> nexts = new HashSet<>(adds);
        nexts.retainAll(this.main.spls.getAfterDelta(delta));
        for(IFormulaElement next: nexts) {
          FormulaAnd left = new FormulaAnd();
          left.add(delta);
          left.add(next);

          FormulaOr right = new FormulaOr();
          Set<IFormulaElement> remsBetween = new HashSet<>(rems);
          remsBetween.retainAll(this.main.spls.getAfterDelta(delta));
          remsBetween.retainAll(this.main.spls.getBeforeDelta(next));
          right.addAll(remsBetween);

          res.add(new FormulaImplies(left, right));
        }
      }
      return res;
    }

    private IFormula applicabilityModifies(Set<IFormulaElement> mods, Set<IFormulaElement> adds, Set<IFormulaElement> rems) {
      FormulaAnd res = new FormulaAnd();

      for(IFormulaElement mod: mods) {
        Set<IFormulaElement> addsBefore = new HashSet<>(adds);
        addsBefore.retainAll(this.main.spls.getBeforeDelta(mod));

        FormulaOr lor = new FormulaOr();
        for(IFormulaElement addBefore: addsBefore) {
          Set<IFormulaElement> remsBetween = new HashSet<>(rems);
          rems.retainAll(this.main.spls.getBeforeDelta(mod));
          rems.retainAll(this.main.spls.getAfterDelta(addBefore));

          FormulaAnd land = new FormulaAnd();
          land.add(addBefore);
          for(IFormulaElement rem: rems) { land.add(new FormulaNot(rem)); }

          lor.add(land);
        }

        res.add(new FormulaImplies(mod, lor));
      }
      return res;
    }

    private IFormula applicabilityRemove(Set<IFormulaElement> adds, Set<IFormulaElement> rems) {
      FormulaAnd res = new FormulaAnd();

      for(IFormulaElement delta: rems) {
        Set<IFormulaElement> nexts = new HashSet<>(rems);
        nexts.retainAll(this.main.spls.getAfterDelta(delta));
        for(IFormulaElement next: nexts) {
          FormulaAnd left = new FormulaAnd();
          left.add(delta);
          left.add(next);

          FormulaOr right = new FormulaOr();
          Set<IFormulaElement> addsBetween = new HashSet<>(adds);
          addsBetween.retainAll(this.main.spls.getAfterDelta(delta));
          addsBetween.retainAll(this.main.spls.getBeforeDelta(next));
          right.addAll(addsBetween);

          res.add(new FormulaImplies(left, right));
        }

        Set<IFormulaElement> addsBefore = new HashSet<>(adds);
        addsBefore.retainAll(this.main.spls.getBeforeDelta(delta));
        FormulaOr lor = new FormulaOr();
        lor.addAll(addsBefore);
        res.add(new FormulaImplies(delta, lor));
      }
      return res;

    }

  }



}

package org.gzoumix.ts.ifdj.sat4j;
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

import org.gzoumix.ts.ifdj.data.syntax.formula.*;
import org.gzoumix.ts.ifdj.data.syntax.visitor.VisitorBasic;

import java.util.*;


public class CNFModel {
  public static class Clause extends Vector<Integer> {}


  private Vector<FormulaPredicate> toPredicate;
  private Map<FormulaPredicate, Integer> toID;

  private PreNormalForm preNormalForm;
  private List<Clause> clauses;

  public CNFModel(IFormula formula) {
    FactoryPreNormalForm factory = new FactoryPreNormalForm(formula);
    this.toPredicate = factory.toPredicate;
    this.toID = factory.toID;
    this.preNormalForm = factory.preNormalForm;
    this.clauses = this.convertToCNF(this.preNormalForm);
  }


  public PreNormalForm getPreNormalForm() { return this.preNormalForm; }
  public List<Clause> getClauses() { return this.clauses; }
  public int getNBVariables() {
    int tmp = this.toPredicate.size() -1;
    return (tmp == 0) ? 1 : tmp;
  }
  public Map<FormulaPredicate, Integer> getIDs() { return this.toID; }
  public Vector<FormulaPredicate> getOrderedPredicates() { return this.toPredicate; }



  public Solution translate(int[] solution) {
    Collection<FormulaPredicate> enabled = new HashSet<>();
    Collection<FormulaPredicate> disabled = new HashSet<>();

    for(int i = 0; i < solution.length; i++) {
      int val = solution[i];
      FormulaPredicate p;
      if(val < 0) {
        val = -val;
        p = (val < this.toPredicate.size()) ? this.toPredicate.get(val) : null;
        disabled.add(p);
      } else {
        p = (val < this.toPredicate.size()) ? this.toPredicate.get(val) : null;
        enabled.add(p);
      }
    }
    return new Solution(solution, enabled, disabled);
  }


  @Override
  public String toString() {
    String res = "";

    res = res + "Clauses: ";
    Iterator<Clause> ic = this.getClauses().iterator();
    while(ic.hasNext()) {
      res = res + "(";
      Clause clause = ic.next();
      Iterator<Integer> j = clause.iterator();
      while(j.hasNext()) {
        res = res + j.next();
        if(j.hasNext()) { res = res + " || "; }
      }
      res = res + ")";
      if(ic.hasNext()) { res = res + " && "; }
    }
    res = res + "\n";

    res = res + "ID: [ ";
    Iterator<FormulaPredicate> ip = this.getOrderedPredicates().iterator();
    int id = 1; ip.next(); // the first one is null, always
    while(ip.hasNext()) {
      res = res + id++ + ":" + ip.next();
      if(ip.hasNext()) { res = res + ", "; }
    }
    res = res + " ]\n";
    res = res + "ID: " + this.getIDs();

    return res;
  }



  private List<Clause> convertToCNF(PreNormalForm preNormalForm) {
    List<Clause> res = null;

    if(preNormalForm instanceof PreNormalForm.PreNormalFormAnd) {
      res = this.convertToCNF(((PreNormalForm.PreNormalFormAnd) preNormalForm).getLeft());
      res.addAll(this.convertToCNF(((PreNormalForm.PreNormalFormAnd) preNormalForm).getRight()));

    } else if(preNormalForm instanceof PreNormalForm.PreNormalFormFalse) {
      res = new LinkedList<>();
      Clause tmp = new Clause(); tmp.add(1);
      res.add(tmp);
      tmp = new Clause(); tmp.add(-1);
      res.add(tmp);

    } else if(preNormalForm instanceof PreNormalForm.PreNormalFormOr) {
      List<Clause> clP = this.convertToCNF(((PreNormalForm.PreNormalFormOr) preNormalForm).getLeft());
      List<Clause> clQ = this.convertToCNF(((PreNormalForm.PreNormalFormOr) preNormalForm).getRight());

      res = new LinkedList<>();
      for(Clause Pi: clP) {
        for(Clause Qj: clQ) {
          Clause tmp = new Clause();
          tmp.addAll(Pi); tmp.addAll(Qj);
          res.add(tmp);
        }
      }

    } else if(preNormalForm instanceof PreNormalForm.PreNormalFormPredicate) {
      Clause tmp = new Clause();
      tmp.add(((PreNormalForm.PreNormalFormPredicate) preNormalForm).getID());
      res = new LinkedList<>();
      res.add(tmp);

    } else if(preNormalForm instanceof PreNormalForm.PreNormalFormTrue) {
      res = new LinkedList<>();
    }


    return res;
  }





  private static class FactoryPreNormalForm extends VisitorBasic {

    private Vector<FormulaPredicate> toPredicate;
    private Map<FormulaPredicate, Integer> toID;
    private PreNormalForm preNormalForm;

    private int nextID;

    public FactoryPreNormalForm(IFormula formula) {
      this.toPredicate = new Vector<>(); this.toPredicate.add(null); // noone has 0 as index
      this.toID = new HashMap<>();
      this.nextID = 1;

      formula.accept(this);
    }

    // Implementation of the IVisitor
    @Override
    public void visit(FormulaAnd formulaAnd) {
      if(formulaAnd.isEmpty()) {
        this.preNormalForm = new PreNormalForm.PreNormalFormTrue();
      } else if(formulaAnd.size() == 1) {
        formulaAnd.iterator().next().accept(this);
      } else {
        Iterator<IFormula> it = formulaAnd.iterator();
        it.next().accept(this);
        PreNormalForm first = this.preNormalForm;
        it.next().accept(this);
        PreNormalForm.PreNormalFormAnd res = new PreNormalForm.PreNormalFormAnd(first, this.preNormalForm);
        while(it.hasNext()) {
          it.next().accept(this);
          res = new PreNormalForm.PreNormalFormAnd(res, this.preNormalForm);
        }
        this.preNormalForm = res;
      }
    }

    @Override
    public void visit(FormulaOr formulaOr) {
      if(formulaOr.isEmpty()) {
        this.preNormalForm = new PreNormalForm.PreNormalFormFalse();
      } else if(formulaOr.size() == 1) {
        formulaOr.iterator().next().accept(this);
      } else {
        Iterator<IFormula> it = formulaOr.iterator();
        it.next().accept(this);
        PreNormalForm first = this.preNormalForm;
        it.next().accept(this);
        PreNormalForm.PreNormalFormOr res = new PreNormalForm.PreNormalFormOr(first, this.preNormalForm);
        while(it.hasNext()) {
          it.next().accept(this);
          res = new PreNormalForm.PreNormalFormOr(res, this.preNormalForm);
        }
        this.preNormalForm = res;
      }
    }


    @Override
    public void visit(FormulaFalse formulaFalse) { // encoding of false.
      this.preNormalForm = new PreNormalForm.PreNormalFormFalse();
    }

    @Override
    public void visit(FormulaTrue formulaTrue) { // nothing to do, it seems
      this.preNormalForm = new PreNormalForm.PreNormalFormTrue();
    }

    @Override
    public void visit(FormulaImplies formulaImplies) {
      this.convertImplies(formulaImplies).accept(this);
    }

    @Override
    public void visit(FormulaEquivalent formulaEquivalent) { this.convertEquivalent(formulaEquivalent).accept(this); }

    @Override
    public void visit(FormulaNot formulaNot) {
      IFormula inner = formulaNot.getFormula();

      if(inner instanceof FormulaPredicate) {
        Integer id = this.getID((FormulaPredicate)inner);
        this.preNormalForm = new PreNormalForm.PreNormalFormPredicate(-id);

      } else if(inner instanceof FormulaNot) {
        ((FormulaNot) inner).getFormula().accept(this);

      } else if(inner instanceof FormulaAnd) {
        FormulaOr convert = new FormulaOr();
        for(IFormula formula: (FormulaAnd)inner) {
          convert.add(new FormulaNot(formula));
        }
        convert.accept(this);

      } else if(inner instanceof FormulaOr) {
        FormulaAnd convert = new FormulaAnd();
        for(IFormula formula: (FormulaOr)inner) {
          convert.add(new FormulaNot(formula));
        }
        convert.accept(this);

      } else if(inner instanceof FormulaImplies) {
        this.convertNotImplies((FormulaImplies) inner).accept(this);

      } else if(inner instanceof FormulaEquivalent) {
        this.convertNotEquivalent((FormulaEquivalent) inner).accept(this);

      } else if(inner instanceof FormulaFalse) {
        this.preNormalForm = new PreNormalForm.PreNormalFormTrue();
      } else if(inner instanceof FormulaTrue) {
        this.preNormalForm = new PreNormalForm.PreNormalFormFalse();
      }
    }


    @Override
    public <I> void visit(FormulaPredicate<I> formulaPredicate) {
      Integer id = this.getID(formulaPredicate);
      this.preNormalForm = new PreNormalForm.PreNormalFormPredicate(id);
    }



    ///////////////////////////////////////////////////////////////////////////
    // utility methods

    private FormulaOr convertImplies(FormulaImplies formula) {
      FormulaOr res = new FormulaOr();
      res.add(new FormulaNot(formula.getLeft()));
      res.add(formula.getRight());
      return res;
    }

    private FormulaAnd convertNotImplies(FormulaImplies formula) {
      FormulaAnd res = new FormulaAnd();
      res.add(formula.getLeft());
      res.add(new FormulaNot(formula.getRight()));
      return res;
    }

    private FormulaOr convertEquivalent(FormulaEquivalent formula) {
      FormulaOr res = new FormulaOr();
      FormulaAnd left = new FormulaAnd();
      FormulaAnd right = new FormulaAnd();
      IFormula P = formula.getLeft();
      IFormula Q = formula.getRight();

      left.add(P); left.add(Q);
      right.add(new FormulaNot(P)); right.add(new FormulaNot(Q));
      res.add(left); res.add(right);
      return res;
    }

    private FormulaOr convertNotEquivalent(FormulaEquivalent formula) {
      FormulaOr res = new FormulaOr();
      FormulaAnd left = new FormulaAnd();
      FormulaAnd right = new FormulaAnd();
      IFormula P = formula.getLeft();
      IFormula Q = formula.getRight();

      left.add(P); left.add(new FormulaNot(Q));
      right.add(new FormulaNot(P)); right.add(Q);
      res.add(left); res.add(right);
      return res;
    }


    private Integer getID(FormulaPredicate formula) {
      Integer id = this.toID.get(formula);
      if (id == null) {
        id = new Integer(this.nextID++);
        this.toID.put(formula, id);
        this.toPredicate.add(id, formula);
      }
      return id;
    }
  }

/*

  private static class Factory extends VisitorBasic {

    public static CNFModel create(IFormula formula) {
      Factory factory = new Factory();
      formula.accept(factory);
      return new CNFModel(factory.toPredicate, factory.toID, factory.clauses);
    }

    private Vector<FormulaPredicate> toPredicate;
    private Map<FormulaPredicate, Integer> toID;
    private List<Clause> clauses;

    private int nextID;

    private Factory() {
      this.clauses = new LinkedList<>();
      this.toPredicate = new Vector<>(); this.toPredicate.add(null); // noone has 0 as index
      this.toID = new HashMap<>();
      this.nextID = 1;
    }


    // Implementation of the IVisitor
    @Override
    public void visit(FormulaAnd formulaAnd) {
      super.visit(formulaAnd); // basically, we merge concat all the clauses list
    }

    @Override
    public void visit(FormulaFalse formulaFalse) { // encoding of false.
      this.generateFalse();
    }

    @Override
    public void visit(FormulaTrue formulaTrue) { // nothing to do, it seems
    }


    @Override
    public void visit(FormulaImplies formulaImplies) {
      this.visit(this.convert(formulaImplies));
    }

    @Override
    public void visit(FormulaNot formulaNot) {
      IFormula inner = formulaNot.getFormula();

      if(inner instanceof FormulaPredicate) {
        Integer id = this.getID((FormulaPredicate)inner);
        Clause clause = new Clause();
        clause.add(-id);
        this.clauses.add(clause);

      } else if(inner instanceof FormulaNot) {
        ((FormulaNot) inner).getFormula().accept(this);

      } else if(inner instanceof FormulaAnd) {
        FormulaOr convert = new FormulaOr();
        for(IFormula formula: (FormulaAnd)inner) {
          convert.add(new FormulaNot(formula));
        }
        convert.accept(this);

      } else if(inner instanceof FormulaOr) {
        FormulaAnd convert = new FormulaAnd();
        for(IFormula formula: (FormulaOr)inner) {
          convert.add(new FormulaNot(formula));
        }
        convert.accept(this);

      } else if(inner instanceof FormulaImplies) {
        FormulaNot not = new FormulaNot(((FormulaImplies) inner).getRight());
        FormulaAnd convert = new FormulaAnd();
        convert.add(((FormulaImplies) inner).getLeft());
        convert.add(not);
        convert.accept(this);

      } else if(inner instanceof FormulaFalse) {
        // this is true, nothing to do;
      } else if(inner instanceof FormulaTrue) {
        this.generateFalse();
      }
    }

    @Override
    public void visit(FormulaOr formulaOr) {
      List<Clause> acc = null;
      for (IFormula formula : formulaOr) {
        formula.accept(this);
        if(acc == null) { acc = this.clauses; }
        else {
          List<Clause> tmp = new LinkedList<>();
          for(Clause clP: acc) {
            for(Clause clQ: this.clauses) {
              Clause c = new Clause();
              c.addAll(clP);
              c.addAll(clQ);
              tmp.add(c);
            }
          }
          acc = tmp;
        }
        this.clauses = new LinkedList<>();
      }

      if(acc != null) {
        this.clauses = acc;
      }
    }


    @Override
    public <I> void visit(FormulaPredicate<I> formulaPredicate) {
      Integer id = this.getID(formulaPredicate);

      // construction of the result;
      Clause clause = new Clause();
      clause.add(id);
      this.clauses.add(clause);
    }



    ///////////////////////////////////////////////////////////////////////////
    // utility methods

    private FormulaOr convert(FormulaImplies formula) {
      FormulaNot left = new FormulaNot(formula.getLeft());
      FormulaOr res = new FormulaOr();
      res.add(left);
      res.add(formula.getRight());
      return res;
    }

    private Integer getID(FormulaPredicate formula) {
      Integer id = this.toID.get(formula);
      if (id == null) {
        id = new Integer(this.nextID++);
        this.toID.put(formula, id);
        this.toPredicate.add(id, formula);
      }
      return id;
    }

    private void generateFalse() {
      Clause clause = new Clause();
      clause.add(1);
      this.clauses.add(clause);
      clause = new Clause();
      clause.add(-1);
      this.clauses.add(clause);
    }

  }
*/
}

package org.gzoumix.ts.ifdj.test;
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
import org.gzoumix.ts.ifdj.sat4j.CNFModel;
import org.gzoumix.util.graph.ComponentGraph;
import org.gzoumix.util.graph.Graph;
import org.junit.Test;


public class TestClause {

  static FormulaPredicate<Integer> p1 = new FormulaPredicate<Integer>(1);
  static FormulaPredicate<Integer> p2 = new FormulaPredicate<Integer>(2);
  static FormulaPredicate<Integer> p3 = new FormulaPredicate<Integer>(3);
  static FormulaPredicate<Integer> p4 = new FormulaPredicate<Integer>(4);
  static FormulaPredicate<Integer> p5 = new FormulaPredicate<Integer>(5);


  @Test
  public void testGraphBasicOperations() {
    System.out.println("Starting Testing on clause");


    test1();

    System.out.println();

    test2();

    System.out.println();

    test3();


  }

  private void test1() {
    System.out.println("1. Creating the formula: (1 & 2) | ( !(3 => 4) | 5)");


    FormulaOr formula = new FormulaOr();

    FormulaAnd left = new FormulaAnd(); left.add(p1); left.add(p2);
    formula.add(left);

    FormulaOr right = new FormulaOr();
    right.add(new FormulaNot(new FormulaImplies(p3, p4)));
    right.add(p5);
    formula.add(right);

    System.out.println("  Constructed formula: " + formula);
    System.out.println("  Expected Normal Form: (1 | 3 | 5) & (1 | !4 | 5) & (2 | 3 | 5) & (2 | !4 | 5)");

    CNFModel model = new CNFModel(formula);

    System.out.println("  Pre Normal Form: " + model.getPreNormalForm());
    System.out.println("  Computed Model: " + model.getClauses());
    System.out.println("  Number of Variables used: " + model.getNBVariables());
  }

  private void test2() {
    System.out.println("2. Creating the formula: !(1 & 2 & 3 & 4 & 5)");
    FormulaAnd inner = new FormulaAnd();
    inner.add(p1); inner.add(p2);inner.add(p3);inner.add(p4);inner.add(p5);
    FormulaNot formula = new FormulaNot(inner);

    System.out.println("  Constructed formula: " + formula);
    System.out.println("  Expected Normal Form: !1 | !2 | !3 | !4 | !5");
    CNFModel model = new CNFModel(formula);

    System.out.println("  Pre Normal Form: " + model.getPreNormalForm());
    System.out.println("  Computed Model: " + model.getClauses());
    System.out.println("  Number of Variables used: " + model.getNBVariables());
  }


  private void test3() {
    System.out.println("3. Creating the formula: !(1 | 2 | 3 | 4 | (5 & 4))");
    FormulaOr inner = new FormulaOr();
    inner.add(p1); inner.add(p2); inner.add(p3); inner.add(p4);
    FormulaAnd inin = new FormulaAnd(); inin.add(p5); inin.add(p4);
    inner.add(inin);
    FormulaNot formula = new FormulaNot(inner);

    System.out.println("  Constructed formula: " + formula);
    System.out.println("  Expected Normal Form: !1 & !2 & !3 & !4 & (!5 | !4)");
    CNFModel model = new CNFModel(formula);

    System.out.println("  Pre Normal Form: " + model.getPreNormalForm());
    System.out.println("  Computed Model: " + model.getClauses());
    System.out.println("  Number of Variables used: " + model.getNBVariables());
  }


}

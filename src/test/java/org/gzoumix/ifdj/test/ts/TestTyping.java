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

package org.gzoumix.ifdj.test.ts;
/******************************************************************************/
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

import org.gzoumix.ifdj.lang.data.SPLS;
import org.gzoumix.ifdj.lang.syntax.ck.DeltaActivation;
import org.gzoumix.ifdj.lang.syntax.core.Program;
import org.gzoumix.ifdj.lang.syntax.fm.Configuration;
import org.gzoumix.ifdj.lang.syntax.formula.*;
import org.gzoumix.ifdj.lang.parser.ProgramFactory;
import org.gzoumix.ifdj.lang.ProgramPrint;
import org.gzoumix.ifdj.refactor.Monotonicity;
import org.gzoumix.ifdj.sat4j.Problem;
import org.gzoumix.ifdj.sat4j.Solution;
import org.gzoumix.ifdj.ts.Consistency;
import org.gzoumix.ifdj.ts.PartialTyping;
import org.gzoumix.util.Global;
import org.gzoumix.util.data.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.*;


@RunWith(Parameterized.class)
public class TestTyping {


  @Parameterized.Parameters
  public static Collection<String[]> data() {
    String[][] data = {
            /*{ Reference.EXAMPLE_EMPTY },
            { Reference.EXAMPLE_HELLO_WORLD },
            { Reference.EXAMPLE_EXPRESSION },*/
            { Reference.EXAMPLE_EPL_NUNIFORM }
    };
    return Arrays.asList(data);
  }

  @Parameterized.Parameter
  public String fileName;
  private Vector<String> fileNameVector = new Vector<>(1);

  //@Test
  public void testTyping() throws IOException {

    System.out.println("////////////////////////////////////////////////////////////////////////////////");
    System.out.println("// Typing Test");
    System.out.println("Testing the file \"" + this.fileName + "\"...");

    // 1. Parsing the file

    this.fileNameVector.add(this.fileName);
    Program program = ProgramFactory.create(this.fileNameVector);
    fileNameVector.remove(0);
    if(Global.log.hasError()) {
      Assert.fail("\n" + Global.log.toString());
      Global.log.clear();
      return;
    } else {
      System.out.println("  -> Parsing successful");
      Global.log.toStream(System.out);
      Global.log.clear();
    }


    // 2. Extracting base informations from the program

    SPLS spls = new SPLS(program);
    if(Global.log.hasError()) {
      Assert.fail("\n" + Global.log.toString());
      Global.log.clear();
      return;
    } else {
      System.out.println("  -> SPLS generation successful");
      Global.log.toStream(System.out);
      Global.log.clear();
      System.out.println(spls.details());
    }

    // 3. Partially typing the program

    boolean partiallyTyped = PartialTyping.check(spls);

    if(Global.log.hasError()) {
      Assert.fail("\n" + Global.log.toString());
      Global.log.clear();
      return;
    } else {
      System.out.println("Typing successful");
      Global.log.toStream(System.out);
      Global.log.clear();
    }


    // 4. Computing the consistency constraint

    // 4.1. configurations
    FormulaOr configurations = new FormulaOr();
    for(Configuration configuration: spls.getProgram().getConfigurations()) {
      configurations.add(configuration.getFormula());
    }

    // 4.2. delta activations
    FormulaAnd activations = new FormulaAnd();
    for(DeltaActivation activation: spls.getProgram().getActivations()) {
      activations.add(new FormulaEquivalent(activation.getFormula(), spls.getDelta(activation.getDelta()).getID()));
    }

    // 4.3. consistency part
    Pair<IFormula, IFormula> consistency = Consistency.create(spls);


    // 4.4. putting everything together
    activations.add(configurations);

    FormulaAnd right = new FormulaAnd();
    right.add(consistency.getFirst());
    right.add(consistency.getSecond());

    IFormula global = new FormulaImplies(activations, right).simplify();

    if(Global.log.hasError()) {
      Assert.fail("\n" + Global.log.toString());
      Global.log.clear();
      return;
    } else {
      System.out.println("Constraint generation successful\n");
      Global.log.toStream(System.out);
      Global.log.clear();

      System.out.println("Generated Constraint: " + global);
    }

    // 5. Finding problems
    Problem problem = new Problem(new FormulaNot(global));

    //CNFModel model = new CNFModel(new FormulaNot(global));
    //List<CNFModel.Clause> clauses = model.getClauses();

    System.out.println("Clauses successfully Generated:\n" + problem.getModel());

    boolean hasConsitencyerror = false;
    //while ( problem.isSatisfiable()) {
    if(problem.isSatisfiable()) {
      hasConsitencyerror = true;

      Solution sol = problem.next();
      Collection<FormulaPredicate> enabledFeature = sol.getSelectedFeatures(spls);
      Collection<FormulaPredicate> enabledDelta = sol.getActivatedDelta(spls);

      System.out.println("Typing Error found during consistency check");
      System.out.print("Solver output: [");
      int[] tmp = sol.getArray();
      for(int i = 0; i < tmp.length; i++) {
        System.out.print(tmp[i]);
        if(i == tmp.length - 1) { System.out.println(" ]"); }
        else { System.out.print(", "); }
      }


      System.out.println("Selected features: " + enabledFeature);
      System.out.println("Activated deltas: " + enabledDelta);

      // only keep the features and deltas that have been selected, in two different list (i.e. the ints that are positive)
      // and display the error message: problem with selecting the following features, that activates the following deltas
      // easy
    }

    if(hasConsitencyerror) { System.out.println("The program is not well typed."); }
    else { System.out.println("The program is well typed."); }

  }

  @Test
  public void testRefactorIncreasing() throws IOException {

    System.out.println("////////////////////////////////////////////////////////////////////////////////");
    System.out.println("// Refactor Increasing Test");
    System.out.println("Testing the file \"" + this.fileName + "\"...");

    // 1. Parsing the file

    this.fileNameVector.add(this.fileName);
    Program program = ProgramFactory.create(this.fileNameVector);
    fileNameVector.remove(0);
    if (Global.log.hasError()) {
      Assert.fail("\n" + Global.log.toString());
      Global.log.clear();
      return;
    } else {
      System.out.println("  -> Parsing successful");
      Global.log.toStream(System.out);
      Global.log.clear();
    }

    System.out.println("Printing the Program...");
    ProgramPrint.print(System.out, program);

    System.out.println("\n\nRefactoring the Program");
    Monotonicity.refactor(program, Monotonicity.Operation.REMOVES);

    System.out.println("Printing the Program...");
    ProgramPrint.print(System.out, program);

  }


  @Test
  public void testRefactorDecreasing() throws IOException {

    System.out.println("////////////////////////////////////////////////////////////////////////////////");
    System.out.println("// Refactor Decreasing Test");
    System.out.println("Testing the file \"" + this.fileName + "\"...");

    // 1. Parsing the file

    this.fileNameVector.add(this.fileName);
    Program program = ProgramFactory.create(this.fileNameVector);
    fileNameVector.remove(0);
    if (Global.log.hasError()) {
      Assert.fail("\n" + Global.log.toString());
      Global.log.clear();
      return;
    } else {
      System.out.println("  -> Parsing successful");
      Global.log.toStream(System.out);
      Global.log.clear();
    }

    System.out.println("Printing the Program...");
    ProgramPrint.print(System.out, program);

    System.out.println("\n\nRefactoring the Program");
    Monotonicity.refactor(program, Monotonicity.Operation.ADDS);

    System.out.println("Printing the Program...");
    ProgramPrint.print(System.out, program);

  }


}

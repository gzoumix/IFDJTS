package org.gzoumix.ifdj.sat4j;
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

import org.gzoumix.ifdj.lang.syntax.formula.IFormula;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import java.util.Iterator;
import java.util.List;

public class Problem implements Iterator<Solution> {

  private IFormula formula;
  private CNFModel model;
  private ISolver solver;
  private ModelIterator mi;
  private IProblem problem;

  private boolean satisfiable;


  public Problem(IFormula formula) {
    this.formula = formula;
    this.model = new CNFModel(this.formula);

    this.satisfiable = true;

    List<CNFModel.Clause> clauses = this.model.getClauses();

    this.solver = SolverFactory.newDefault();
    this.mi = new ModelIterator(solver);
    this.mi.newVar(this.model.getNBVariables());
    this.mi.setExpectedNumberOfClauses(clauses.size());

    for(CNFModel.Clause clause: clauses) {
      int[] tmp = new int[clause.size()]; int i = 0;
      for(Integer val: clause) { tmp[i++] = val; }
      try {
        this.mi.addClause(new VecInt(tmp));
      } catch (ContradictionException e) {
        e.printStackTrace(); // not really relevant I think
        this.satisfiable = false;
      }
    }
    this.problem = mi;
  }


  public IFormula getFormula() { return this.formula; }
  public CNFModel getModel() { return this.model; }
  public boolean isSatisfiable() { return this.hasNext(); }

  @Override
  public boolean hasNext() {
    if(this.satisfiable) {
      try {
        this.satisfiable = this.problem.isSatisfiable();
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    }
    return this.satisfiable;
  }

  @Override
  public Solution next() {
    return this.model.translate(problem.model());
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}

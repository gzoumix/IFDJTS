package org.gzoumix.ifdj.util;
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

import org.gzoumix.ifdj.lang.data.FCS;
import org.gzoumix.ifdj.lang.syntax.ASTNodeCommonFunctionalities;
import org.gzoumix.ifdj.lang.syntax.ISuperClassDeclaration;
import org.gzoumix.ifdj.lang.syntax.core.Attribute;
import org.gzoumix.ifdj.lang.syntax.formula.FormulaFalse;
import org.gzoumix.ifdj.lang.syntax.formula.FormulaTrue;
import org.gzoumix.ifdj.lang.syntax.formula.IFormulaElement;
import org.gzoumix.ifdj.lang.syntax.visitor.IVisitor;
import org.gzoumix.util.syntax.Position;

import java.util.*;


public class Reference {

  public static final Position DUMMY_POSITION = new Position("std", 0, 0, 0, 0, 0, 0);

  public static final String TRUE = "true";
  public static final String FALSE = "false";

  public static final FormulaTrue FORMULA_TRUE = new FormulaTrue();
  public static final FormulaFalse FORMULA_FALSE = new FormulaFalse();

  public static final String THIS = "this";
  public static final String NULL = "null";
  public static final String ORIGINAL = "original";


  ///////////////////////////////////////////////////////////////////////////////
  // DEFINITION OF THE STANDARD LIBRARY
  ///////////////////////////////////////////////////////////////////////////////

  public static final Map<FCS, Collection<ISuperClassDeclaration>> classes = new HashMap<>();

  public static final String NAME_OBJECT_CLASS = "Object";
  public static final String NAME_NULL_TYPE = "*NullType*";
  public static final String NAME_INT_CLASS = "int";
  public static final String NAME_STRING_CLASS = "String";

  public static final FCS FCS_OBJECT_CLASS = new FCS(NAME_OBJECT_CLASS);
  public static final FCS FCS_NULL_TYPE = new FCS(NAME_NULL_TYPE);
  public final static FCS FCS_INT_CLASS = new FCS(NAME_INT_CLASS);
  public final static FCS FCS_STRING_CLASS = new FCS(NAME_STRING_CLASS);

  public final static Attribute.SignatureField SIG_NULL_TYPE = new Attribute.SignatureField(NAME_NULL_TYPE);

  private static class STDSuperClassDeclaration extends ASTNodeCommonFunctionalities implements ISuperClassDeclaration {
    private String baseClass;
    private String superClass;

    protected STDSuperClassDeclaration(String baseClass, String superClass) {
      super(DUMMY_POSITION);
      this.baseClass = baseClass;
      this.superClass = superClass;
    }

    @Override
    public String getBaseClass() { return this.baseClass; }

    @Override
    public String getSuperClass() { return this.superClass; }

    @Override
    public IFormulaElement getDelta() {
      return FORMULA_TRUE;
    }

    @Override
    public void accept(IVisitor visitor) { }
  }


  static {
    // everyone can perform a "toString"
    FCS_OBJECT_CLASS.addAttribute(
            new Attribute(DUMMY_POSITION, NAME_OBJECT_CLASS, "toString",
                    new Attribute.SignatureMethod(NAME_STRING_CLASS))
    );

    Collection<ISuperClassDeclaration> tmp;
    tmp = new ArrayList<>(0);
    classes.put(FCS_OBJECT_CLASS, tmp);

    tmp = new ArrayList<>(1);
    tmp.add(new STDSuperClassDeclaration(NAME_INT_CLASS, NAME_OBJECT_CLASS));
    classes.put(FCS_INT_CLASS, tmp);

    tmp = new ArrayList<>(1);
    tmp.add(new STDSuperClassDeclaration(NAME_STRING_CLASS, NAME_OBJECT_CLASS));
    classes.put(FCS_STRING_CLASS, tmp);
  }

  public static class Operator {
    public static final Map<String, Attribute.SignatureMethod> operators = new HashMap<>();

    public static final String NAME_PLUS = "+";
    public static final String NAME_MINUS = "-";
    public static final String NAME_TIMES = "*";

    public static final String NAME_CONCAT = "^";

    public static final Attribute.SignatureMethod SIGNATURE_PLUS = new Attribute.SignatureMethod(NAME_INT_CLASS);
    public static final Attribute.SignatureMethod SIGNATURE_MINUS = new Attribute.SignatureMethod(NAME_INT_CLASS);
    public static final Attribute.SignatureMethod SIGNATURE_TIMES = new Attribute.SignatureMethod(NAME_INT_CLASS);

    public static final Attribute.SignatureMethod SIGNATURE_CONCAT = new Attribute.SignatureMethod(NAME_STRING_CLASS);

    static {
      SIGNATURE_PLUS.addParameter(NAME_INT_CLASS, "P1");
      SIGNATURE_PLUS.addParameter(NAME_INT_CLASS, "P2");

      SIGNATURE_MINUS.addParameter(NAME_INT_CLASS, "P1");

      SIGNATURE_TIMES.addParameter(NAME_INT_CLASS, "P1");
      SIGNATURE_TIMES.addParameter(NAME_INT_CLASS, "P2");

      SIGNATURE_CONCAT.addParameter(NAME_STRING_CLASS, "P1");
      SIGNATURE_CONCAT.addParameter(NAME_STRING_CLASS, "P2");

      operators.put(NAME_PLUS, SIGNATURE_PLUS);
      operators.put(NAME_MINUS, SIGNATURE_MINUS);
      operators.put(NAME_TIMES, SIGNATURE_TIMES);
      operators.put(NAME_CONCAT, SIGNATURE_CONCAT);

    }
  }


}

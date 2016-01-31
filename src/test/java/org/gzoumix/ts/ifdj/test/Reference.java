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

public class Reference {

  private static String PWD = "src/test/ifdj/";
  private static String EXTENSION = ".ifdj";

  private static String build(String name) { return PWD + name + EXTENSION; }

  public static String EXAMPLE_EMPTY         = build("01-exampleEmpty");
  public static String EXAMPLE_HELLO_WORLD   = build("02-exampleHelloWorld");
  public static String EXAMPLE_EXPRESSION    = build("03-exampleExpression");
  public static String EXAMPLE_EPL_NUNIFORM  = build("04-EPL-non-uniform");
}

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


/**
 * This file presents the grammar of the IFDJ language presented in [1]
 */

grammar IFDJ;

import IFDJKeywords;

compilationUnit: declaration* EOF;

declaration
  : featureSetDeclaration
  | featureConfigurationDeclaration
  | deltaActivationDeclaration
  | deltaOrderingDeclaration
  | deltaDeclaration
  | classDeclaration
  ;


// Feature Model Declaration part
featureSetDeclaration: KWFEATURES COLON (ID (COMMA ID)*)? SEMICOLON ;
featureConfigurationDeclaration: KWCONSTRAINTS COLON formula SEMICOLON ;

deltaActivationDeclaration: KWDELTA KWACTIVATE COLON deltaActivationStatement (COMMA deltaActivationStatement)* SEMICOLON ;
deltaActivationStatement: deltaListStatement KWWHEN formula ;

deltaOrderingDeclaration: KWDELTA KWORDER COLON deltaOrderingStatement (COMMA deltaOrderingStatement)* SEMICOLON ;
deltaOrderingStatement : deltaListStatement LNEQ deltaListStatement (LNEQ deltaListStatement)* ;

deltaListStatement: ID | LCBRACKET left+=ID (COMMA left+=ID)* RCBRACKET;

// Formula part
formula
  : LPAREN inner=formula RPAREN            #formulaInner
  | KWTRUE                                 #formulaTrue
  | KWFALSE                                #formulaFalse
  | predicate=ID                           #formulaPredicate
  | KWNEG inner=formula                    #formulaNeg
  | left=formula KWLAND    right=formula   #formulaAnd
  | left=formula KWLOR     right=formula   #formulaOr
  | left=formula KWIMPLIES right=formula   #formulaImplies
  ;


// Delta part
deltaDeclaration
  : KWDELTA name=ID LCBRACKET  classOperation* RCBRACKET
  ;

classOperation
  : KWADDS classDeclaration      #classOperationAdds
  | KWREMOVES name=ID            #classOperationRemoves
  | KWMODIFIES name=ID (KWEXTENDING superclass=ID)? LCBRACKET attributeOperation* RCBRACKET #classOperationModifies
  ;

attributeOperation
  : KWADDS attributeDeclaration       #attributeOperationAdds
  | KWMODIFIES attributeDeclaration   #attributeOperationModifies
  | KWREMOVES name=ID                 #attributeOperationRemoves
  ;

// Class part
classDeclaration
  : KWCLASS name=ID KWEXTENDS superclass=ID LCBRACKET attributeDeclaration* RCBRACKET
  ;

attributeDeclaration
  : type=ID name=ID SEMICOLON                                                                            #fieldDeclaration
  | rtype=ID name=ID LPAREN (parameters+=methodParameter(COMMA parameters+=methodParameter)*)? RPAREN
                     LCBRACKET (expression SEMICOLON)* KWRETURN expression SEMICOLON RCBRACKET           #methodDeclaration
  ;
methodParameter: type=ID name=ID ;
// Expression part

expression
  : name=ID                                                                                      #expressionVariable
  | base=expression DOT name=ID                                                                  #expressionAccess
  | base=expression LPAREN (params+=expression (COMMA params+=expression)*)? RPAREN              #expressionMethodCall
  | KWNEW name=ID LPAREN RPAREN                                                                  #expressionNew
  | LPAREN type=ID RPAREN expression                                                             #expressionCast
  | left=expression KWASSIGN right=expression                                                    #expressionAssign
  | LPAREN inner=expression RPAREN                                                               #expressionInner
  | KWNULL                                                                                       #expressionNull
  | OP_UNARY expression                                                                          #expressionOpUnary
  | left=expression OP_BINARY right=expression                                                   #expressionOpBinary
  | LiteralInteger                                                                               #expressionLiteralInteger
  | LiteralString                                                                                #expressionLiteralString
  ;

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

lexer grammar IFDJKeywords;

LPAREN   : '(' ;
RPAREN   : ')' ;
LBRACKET : '[' ;
RBRACKET : ']' ;
LCBRACKET: '{' ;
RCBRACKET: '}' ;
SEMICOLON: ';' ;
COLON    : ':' ;
COMMA    : ',' ;
DOT      : '.' ;
LNEQ     : '<' ;

KWTRUE : 'true' ;
KWFALSE: 'false' ;
KWNEG  : '!' ;
KWLAND : '&&' ;
KWLOR  : '||' ;
KWIMPLIES: '=>' ;

KWFEATURES: 'features' ;
KWCONSTRAINTS: 'constraints' ;
KWDELTA: 'delta' ;
KWORDER: 'order' ;
KWACTIVATE: 'activation';
KWWHEN: 'when' ;
KWAFTER: 'after' ;
KWADDS: 'adds' ;
KWREMOVES: 'removes' ;
KWMODIFIES: 'modifies' ;
KWEXTENDING: 'extending' ;

KWCLASS: 'class' ;
KWEXTENDS: 'extends' ;

KWRETURN: 'return' ;
KWNEW: 'new' ;
KWASSIGN: '=' ;
KWNULL: 'null' ;

OP_BINARY: '+' | '*' | '/' | '%' | '^' ;
OP_UNARY: '-' ;

/////////////////////////////////////
// Literals
LiteralInteger: '0'? 'x'? [0-9]+;
LiteralString: '"' (~["])* '"';


// ID part (taken from Java grammar https://github.com/antlr/grammars-v4/blob/master/java/Java.g4)
ID:   JavaLetter JavaLetterOrDigit* ;

fragment JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;



// Whitespace and comments part

WS:  [ \t\r\n\u000C]+ -> skip ;
COMMENT:   '/*' .*? '*/' -> skip ;
LINE_COMMENT:   '//' ~[\r\n]* -> skip ;
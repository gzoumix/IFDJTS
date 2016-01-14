package org.gzoumix.util.syntax;
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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.gzoumix.util.Global;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.BitSet;

/******************************************************************************/

public class ANTLRHelper {

  public static class ParserConstructor<P extends Parser> implements ANTLRErrorListener {

    //////////////////////////////////////////////
    // 1. Parsing Errors

    private class ParsingErrorException {
      Exception e;
      ParsingErrorException(IOException e) { this.e = e; }
      @Override
      public String toString() { return e.getMessage(); }
    }

    private class ParsingErrorSyntax {
      String fileName;
      int line, c;
      String msg;

      ParsingErrorSyntax(String fileName, int line, int c, String msg) {
        this.fileName = fileName;
        this.line = line;
        this.c = c;
        this.msg = msg;
      }
      @Override
      public String toString() { return "file \"" + this.fileName + "\": line " + this.line + ":" + this.c + " " + this.msg; }
    }


    //////////////////////////////////////////////
    // 2. Class Definition

    private String fileName;
    private Class lexerClass;
    private Class parserClass;


    public ParserConstructor(Class lexer, Class parser) {
      if(!lexer.getSuperclass().equals(Lexer.class)) {
        Global.log.logError("ANTLR-GPC: the class \"" + lexer.getName() + "\" is not a lexerClass");
      } else { this.lexerClass = lexer; }
      if(!parser.getSuperclass().equals(Parser.class)) {
        Global.log.logError("ANTLR-GPC: the class \"" + parser.getName() + "\" is not a parserClass");
      } else { this.parserClass = parser; }
    }

    public boolean hasError() { return (this.lexerClass == null) || (this.parserClass == null); }

    public P file(String fileName) {
      this.fileName = fileName;
      try {
        return this.parse(new ANTLRFileStream(fileName));
      } catch (IOException e) {
        Global.log.logError(new ParsingErrorException(e));
        return null;
      }
    }

    public P string(String code) {
      this.fileName = "String";
      return this.parse(new ANTLRInputStream(code));
    }

    private P parse(ANTLRInputStream stream) {
      Lexer lexer = null;
      P parser = null;

      try { // create the lexer: NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
        Constructor<Lexer> lexerConstructor = this.lexerClass.getConstructor(CharStream.class);
        lexer = lexerConstructor.newInstance(stream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(this);
      } catch (Exception e) {
        Global.log.logError(e);
      }

      if(lexer != null) {
        try { // create the parser: NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
          CommonTokenStream tokens = new CommonTokenStream(lexer);
          Constructor<Parser> parserConstructor = this.parserClass.getConstructor(TokenStream.class);
          parser = (P) parserConstructor.newInstance(tokens);
          parser.removeErrorListeners();
          parser.addErrorListener(new DiagnosticErrorListener());
          parser.addErrorListener(this);
        } catch (Exception e) {
          Global.log.logError(e);
        }
      }
      return parser;
    }


    //////////////////////////////////////////////
    // 3. Implementation of the ANTLRErrorListener interface

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object o, int line, int c, String msg, RecognitionException e) {
      Global.log.logError(new ParsingErrorSyntax(this.fileName, line, c, msg));
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int line, int c, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {}

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int line, int c, BitSet bitSet, ATNConfigSet atnConfigSet) {}

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int line, int c1, int c2, ATNConfigSet atnConfigSet) {}
  }



  public static Position extractPositionFrom(String fileName, ParserRuleContext ctx) {
    int startLine = ctx.start.getLine();
    int startCharacterFile = ctx.start.getStartIndex();
    int startCharacterLine = ctx.start.getCharPositionInLine();
    int endLine = ctx.stop.getLine();
    int endCharacterFile = ctx.stop.getStopIndex();
    int endCharacterLine = ctx.stop.getCharPositionInLine();

    return new Position(fileName, startLine, startCharacterFile, startCharacterLine, endLine, endCharacterFile, endCharacterLine);
  }

  public static Position extractPositionFrom(String fileName, TerminalNode tn) {
    int startLine = tn.getSymbol().getLine();
    int startCharacterFile = tn.getSymbol().getStartIndex();
    int startCharacterLine = tn.getSymbol().getCharPositionInLine();
    int endLine = tn.getSymbol().getLine();
    int endCharacterFile = startCharacterFile + tn.getSymbol().getText().length();
    int endCharacterLine = startCharacterLine + tn.getSymbol().getText().length();

    return new Position(fileName, startLine, startCharacterFile, startCharacterLine, endLine, endCharacterFile, endCharacterLine);
  }

}

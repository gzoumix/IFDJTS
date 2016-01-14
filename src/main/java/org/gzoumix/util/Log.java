package org.gzoumix.util;
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Log {


  /////////////////////////////////////////////
  // 1. Log Level Class
  public enum LogLevel {
    OFF  ("[OFF]   "),   // The highest possible rank and is intended to turn off logging.
    FATAL("[FATAL] "), // Severe errors that cause premature termination. Expect these to be immediately visible on a status console.
    ERROR("[ERROR] "), // Other runtime errors or unexpected conditions. Expect these to be immediately visible on a status console.
    WARN ("[WARN]  "),  // Use of deprecated APIs, poor use of API, 'almost' errors, other runtime situations that are undesirable or unexpected, but not necessarily "wrong". Expect these to be immediately visible on a status console.
    INFO ("[INFO]  "),  // Interesting runtime events (startup/shutdown). Expect these to be immediately visible on a console, so be conservative and keep to a minimum.
    DEBUG("[DEBUG] "), // Detailed information on the flow through the system. Expect these to be written to logs only.
    TRACE("[TRACE] "); // Most detailed information. Expect these to be written to logs only

    private String s;
    LogLevel(String s) { this.s = s; }

    @Override
    public String toString() { return s; }
  }


  /////////////////////////////////////////////
  // 2. Message classes (store extra informations for formatting)
  private static class LoggedMessage {
    LogLevel level;
    Object message;
    int indent;

    public LoggedMessage(LogLevel level, int indent, Object message) {
      this.level   = level;
      this.indent  = indent;
      this.message = message;
    }

    @Override
    public String toString() {
      String indentString = this.level.toString();
      for (int i = 0; i < this.indent; i++) {
        indentString += "  ";
      }
      return  indentString + this.message.toString().replace("\n", "\n" + indentString);
    }
  }

  /////////////////////////////////////////////
  // 3. Class definition
  private List<LoggedMessage> log;
  private int indent;
  private LogLevel outputLevel;
  private Boolean has[];

  public Log() {
    this.log = new LinkedList<>();
    this.indent = 0;
    this.outputLevel = LogLevel.TRACE;
    this.has = new Boolean[LogLevel.values().length];
    this.resetHas();
  }
  public void setLogLevel(LogLevel level) { this.outputLevel = level; }
  public LogLevel getCurrentLogLevel() { return this.outputLevel; }

  private void resetHas() {
    for(int i = 0; i < this.has.length; i++) { this.has[i] = false; }
  }

  // LOG
  public void log(LogLevel level, Object message) {
    this.log.add(new LoggedMessage(level, this.indent, message));
    this.has[level.ordinal()] = true;
    //for(int i = level.ordinal(); i < this.has.length; i++) { this.has[i] = true; }
  }

  public void logFatal(Object message) { this.log(LogLevel.FATAL, message); }
  public void logError(Object message) { this.log(LogLevel.ERROR, message); }
  public void logWarn (Object message) { this.log(LogLevel.WARN , message); }
  public void logInfo (Object message) { this.log(LogLevel.INFO , message); }
  public void logDebug(Object message) { this.log(LogLevel.DEBUG, message); }
  public void logTrace(Object message) { this.log(LogLevel.TRACE, message); }


  public void indent() { this.indent++; }
  public void dedent() { if(this.indent != 0) { this.indent--; }}

  public boolean isEmpty() { return this.log.isEmpty(); }
  public void clear() { this.log.clear(); this.resetHas(); }

  // Basic Lookup
  public boolean has(LogLevel level) { return this.has[level.ordinal()]; }
  public boolean hasFatal() { return this.has(LogLevel.FATAL); }
  public boolean hasError() { return this.has(LogLevel.ERROR); }
  public boolean hasWarn () { return this.has(LogLevel.WARN); }
  public boolean hasInfo () { return this.has(LogLevel.INFO); }
  public boolean hasDebug() { return this.has(LogLevel.DEBUG); }
  public boolean hasTrace() { return this.has(LogLevel.TRACE); }

  // TOSTRING
  @Override
  public String toString() {
    String res = "";
    for(LoggedMessage mes: this.log) {
      if(mes.level.ordinal() <= this.outputLevel.ordinal()) {
        res += mes.toString() + "\n";
      }
    }
    return res;
  }


  // TOSTREAM
  public void toStream(OutputStream stream) throws IOException {
    for(LoggedMessage mes: this.log) {
      if(mes.level.ordinal() <= this.outputLevel.ordinal()) {
        stream.write((mes.toString() + "\n").getBytes());
      }
    }
  }

}

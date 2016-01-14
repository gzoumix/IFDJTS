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
/******************************************************************************/


public class Position {
  private String fileName;

  private int startLine;
  private int startCharacterFile, startCharacterLine;
  private int endLine;
  private int endCharacterFile, endCharacterLine;

  public Position(String file, int sl, int scf, int scl, int el, int ecf, int ecl) {
    this.fileName = file;
    this.startLine = sl;
    this.startCharacterFile = scf;
    this.startCharacterLine = scl;
    this.endLine = el;
    this.endCharacterFile = ecf;
    this.endCharacterLine = ecl;
  }


  public String getFileName() { return fileName; }
  public int getStartLine() { return startLine; }
  public int getStartCharacterFile() { return startCharacterFile; }
  public int getStartCharacterLine() { return startCharacterLine; }
  public int getEndLine() { return endLine; }
  public int getEndCharacterFile() { return endCharacterFile; }
  public int getEndCharacterLine() { return endCharacterLine; }

  @Override
  public String toString() {
    return "file \"" + this.getFileName() + "\": line " + this.getStartLine() + ":" + this.getStartCharacterLine();
  }
}

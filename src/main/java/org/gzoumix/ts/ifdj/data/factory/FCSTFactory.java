package org.gzoumix.ts.ifdj.data.factory;
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

import org.gzoumix.ts.ifdj.data.FCS;
import org.gzoumix.ts.ifdj.data.FCST;
import org.gzoumix.ts.ifdj.data.syntax.core.Attribute;
import org.gzoumix.ts.ifdj.data.syntax.core.Classs;
import org.gzoumix.ts.ifdj.data.syntax.core.Program;
import org.gzoumix.ts.ifdj.data.syntax.delta.AttributeAddition;
import org.gzoumix.ts.ifdj.data.syntax.delta.AttributeModification;
import org.gzoumix.ts.ifdj.data.syntax.delta.ClassModification;
import org.gzoumix.ts.ifdj.data.syntax.delta.IAttributeOperation;
import org.gzoumix.ts.ifdj.data.syntax.visitor.VisitorBasic;
import org.gzoumix.ts.ifdj.util.Reference;


public class FCSTFactory extends VisitorBasic {

  public static FCST create(Program program) {
    FCSTFactory factory = new FCSTFactory();
    factory.visit(program);
    return factory.fcst;
  }


  private FCST fcst;
  private FCSTFactory() { this.fcst = new FCST(); }

  // What we are interested in are the class declarations and modifications

  @Override
  public void visit(Classs classs) {
    //System.out.println("FCSTFactory: visiting class " + classs.getBaseClass());

    FCS fcs = new FCS(classs.getName());
    for(Attribute att: classs.getAttributes()) {
      //System.out.println("  found attribute " + att.getName());
      fcs.addAttribute(att);
    }
    fcst.addFCS(fcs);
  }

  @Override
  public void visit(ClassModification op) {
    FCS fcs = new FCS(op.getName());
    for(IAttributeOperation attop: op.getOperations()) {
      if(attop instanceof AttributeAddition) {
        fcs.addAttribute(((AttributeAddition) attop).getAttribute());
      } else if(attop instanceof AttributeModification) {
        fcs.addAttribute(((AttributeModification) attop).getAttribute());
      }
    }
    fcst.addFCS(fcs);
  }
}

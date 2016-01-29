# The IFDJ Tool Chain

The purpose of this repository is to implement prototype algorithms related to _Delta Oriented Programming_ [1,2].
We use the **IFDJ** [3] calculus as basis of our work.
Currently, there is no main method: all implemented algorithms are executed with JUnit tests.
This implementation is done in the context of the [HyVAR European Project](http://www.hyvar-project.eu/hyvar/).

Currently, we implemented a modular type system,
  and we intend to extend it into a full toolchain, with variant generation, refactoring algorithms, etc.

## The IFDJ Type System
  This type system is based on the generation of SAT constraints that are solved using SAT4j.
  Examples of input files are available in src/test/ifdj/
  and the test is implemented in src/test/java/org/gzoumix/ts/ifdj/test/TestTyping.java






[1] I. Schaefer, L. Bettini, V. Bono, F. Damiani, and N. Tanzarella. Delta-oriented Programming of Software
    Product Lines. In _Proc. of SPLC 2010, volume 6287 of LNCS_, pages 77–91. Springer, 2010.
[2] I. Schaefer and F. Damiani. Pure Delta-oriented Programming. In _Proc. of FOSD 2010, pages 49–56. ACM_, 2010.

[3] L. Bettini, F. Damiani, and I. Schaefer. Compositional type checking of delta-
    oriented software product lines. Acta Informatica, 50(2):77–122, 2013.

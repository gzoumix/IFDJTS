# The IFDJ Tool Chain

The purpose of this repository is to implement a prototype toolchain for **Delta Oriented Programming** [1,2].
We use the *IFDJ* [3] calculus as basis of our work.
This implementation is done in the context of the [HyVAR European Project](http://www.hyvar-project.eu/hyvar/).


## Delta Oriented Programming

Delta-Oriented Programming (DOP) is a  flexible transformational approach  to implement Software Product Lines.
A Software Product Line (SPL) is a set of similar programs, called *variants*, with a common code base and  well documented variability.
An SPL is described by a *Feature Model* (FM), a  *Configuration Knowledge* (CK), and an *Artifact Base* (AB).
The FM provides an abstract description of variants in terms of *features*:
  each feature represents an abstract description of functionality and each variant is identified by a set of features, called a *product*.
The AB provides language dependent code artifacts that are used to build the variants:
  in the case of DOP, it consists of a *base program* (that might be empty or incomplete) and of a set of *delta modules*, which are containers of modifications to a program
  (e.g., for Java programs, a delta module can add, remove or modify classes and interfaces).
The CK connects the code artifacts in the AB with the features in the FM (thus defining a mapping from products to variants):
 in the case of DOP, it associates to each delta module an *activation condition* over the features
 and specifies an *application ordering* between delta modules.
DOP supports the automatic generation of variants based on a selection of features:
 once a user selects a product, the corresponding variant is derived by applying the delta modules
 with a satisfied activation condition to the base program according to the application ordering.


## Algorithms

**Implementation Details**

All the algorithms are based on the *IFDJ* language, defined in an [ANTLR-v4](http://www.antlr.org/) grammar
 ([lexer](src/main/java/org/gzoumix/ifdj/lang/parser/IFDJKeywords.g4), [parser](src/main/java/org/gzoumix/ifdj/lang/parser/IFDJ.g4)).
The AST of the language is declared [here](src/main/java/org/gzoumix/idfj/lang/syntax).

### Type System
 
Type checking approaches for DOP have already been studied and implemented for the [ABS modeling language](http://abs-models.org/).
Although these approaches do not require to generate any variant, they involve an explicit iteration over the set of products,
 which becomes an issue when the number of products is large (a product line with *n* features can have up to *2<sup>n</sup>* products).
 
Here, we propose a novel type checking approach for DOP by building on [ideas proposed for FOP](ftp://www.cs.utexas.edu/pub/predator/FSE09.pdf).
Our approach represents an achievement over previous type checking approaches for DOP since
 it provides earlier detection of some type errors and  does not  require to iterate over the set of products.
Like the techniques for FOP, our approach requires to check the validity of a propositional formula (which is a co-NP-complete problem) and
 can take advantages of the many heuristics implemented in SAT solvers
 (a SAT solver can be used to check whether a propositional formula is valid by checking whether its negation is unsatisfiable).
The work on the type system for FOP reports that the performance of using SAT solvers  to verify  the propositional formulas
 is encouraging and that, type checking a particular example was even faster than generating and compiling a single product.

---

**Implementation Details**




Currently, we implemented a modular type system,
  and we intend to extend it into a full toolchain, with variant generation, refactoring algorithms, etc.

## The IFDJ Type System
  This type system is based on the generation of SAT constraints that are solved using SAT4j.
  Examples of input files are available in src/test/ifdj/
  and the test is implemented in src/test/java/org/gzoumix/ts/ifdj/test/TestTyping.java






[1] I. Schaefer, L. Bettini, V. Bono, F. Damiani, and N. Tanzarella. Delta-oriented Programming of Software
    Product Lines. In _Proc. of SPLC 2010, volume 6287 of LNCS_, pages 77-91. Springer, 2010.
[2] I. Schaefer and F. Damiani. Pure Delta-oriented Programming. In _Proc. of FOSD 2010, pages 49-56. ACM_, 2010.

[3] L. Bettini, F. Damiani, and I. Schaefer. Compositional type checking of delta-
    oriented software product lines. Acta Informatica, 50(2):77-122, 2013.
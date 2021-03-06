[![Build Status](https://travis-ci.org/logic-ng/LogicNG.svg)](https://travis-ci.org/logic-ng/LogicNG) [![Coverage Status](https://coveralls.io/repos/logic-ng/LogicNG/badge.svg?branch=master&service=github)](https://coveralls.io/github/logic-ng/LogicNG?branch=master) ![License](https://img.shields.io/badge/license-Apache%202-blue.svg) ![Version](https://img.shields.io/badge/version-1.0--RC4-ff69b4.svg)

<img src="https://github.com/logic-ng/LogicNG/blob/master/doc/logo/logo_big.png" alt="logo" width="300">

## Introduction
LogicNG is a Java Library for creating, manipulating and solving Boolean and Pseudo-Boolean formulas. It includes 100% Java implementations of popular tools like [MiniSAT](http://minisat.se), [CleaneLing](http://fmv.jku.at/cleaneling/), [Glucose](http://www.labri.fr/perso/lsimon/glucose/), or [OpenWBO](http://sat.inesc-id.pt/open-wbo/).

Its main focus lies on memory-efficient data-structures for Boolean formulas and efficient algorithms for manipulating and solving them.
The library is designed to be used in industrial systems which have to manipulate and solve millions of formulas per day.

## Philosophy
The most important philosophy of the library is to avoid unnecessary object creation.  Therefore formulas can only be generated via formula factories.  A formula factory assures that a formula is only created once in memory.  If another instance of the same formula is created by the user, the already existing one is returned by the factory. This leads to a small memory footprint and fast execution of algorithms.  Formulas can cache the results of algorithms executed on them and since every formula is hold only once in memory it is assured that the same algorithm on the same formula is also executed only once.

Compared to other implementation of logic libraries on the JVM this is a huge memory and performance improvement.

## Release Plan
The current release is declared as 1.0 Release Candidate 3.  The API is considered stable but some testing and documentation has to be finished for a final version 1.0 in January 2016.

## Changelog
### RC1 → RC2
* Cache number of nodes and number of atoms in the formulas
* Implemented variable and literal profile functions as `FormulaFunction` instead of methods of `Formula`
* Moved dot string generation to its writer
* Implemented universal and existential quantifier elimination by Shannon expansion
* Improved overall test quality and coverage
* Some minor performance and code style improvements

### RC2 → RC3
* Removed certified UNSAT option in Glucose (this will come in a later release)
* Improved overall test quality and coverage
* Some minor performance and code style improvements

### RC3 → RC4 (Feature Freeze)
* Introduced a `Variable` class which is a shortcut for positive literals.  It is now possible to enforce positive literals on a type level
* Introduced a name for formula factories to avoid name clashing for generated variables of different factories
* Extended MiniSAT configuration with an initial phase
* Improved CleaneLing configuration
* Improved overall test quality and coverage
* Some minor performance and code style improvements

## Getting Started
The following code creates the Boolean Formula *A and not (B or not C)* programatically:
```java
final FormulaFactory f = new FormulaFactory();
final Literal a = f.literal("A");
final Literal b = f.literal("B");
final Literal notC = f.literal("C", false);
final Formula formula = f.and(a, f.not(f.or(b, notC)));
```
Alternatively you can just parse the formula from a string:
```java
final FormulaFactory f = new FormulaFactory();
final PropositionalParser p = new PropositionalParser(f);
final Formula formula = p.parse("A & ~(B | ~C)");
```
Once you created the formula you can for example convert it to NNF or CNF or solve it with an instance of MiniSAT:
```java
final Formula nnf = formula.nnf();
final Formula cnf = formula.cnf();
final SATSolver miniSat = MiniSat.miniSat(f);
miniSat.add(formula);
final Tristate result = miniSat.sat();
```

## License & Commercial Support
The library is released under the Apache License and therefore is free to use in any private, educational, or commercial project.  Commercial support will be available soon.


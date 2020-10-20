# Informix Fourth Generation Language

The Informix 4GL is a Pascal like functional language that embeds SQL statements.

The grammar is implemented in [Antlr 4](https://www.antlr.org/), the parser builds AST nodes for the 4GL specific code (functions, control flow, variables, etc), wich are implemented as a Tuffle language on top of [GraalVM](https://www.graalvm.org/). The SQL specific code is handled to the [Squirrel SQL](http://squirrel-sql.sourceforge.net/) library for further parsing and later execution.

WORK IN PROGRESS...

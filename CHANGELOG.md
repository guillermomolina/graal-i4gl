Version 0.3.0

- Fix test Sqlca
- Major refactor, Take out I4GL from most classes names
- Implement date type


Version 0.2.0

- Add support for single quoted string literals
- Remove i4gl sources from final image
- Implement initial icgi library support
- Add fgl_getenv builtin
- Implement visitFactorTypes in Node Factory
- Implement using operator
- Fix function definition parameters
- Add length builtin
- Implement NULL assignment
- Fix CHAR uninitialized value
- Implement ASCII expression
- Implement SQL ... END SQL statement
- Add complex record as variable in parser
- Implement array of records
- Remove jdk8 support (as per graalvm removal)
- Implement INITIALIZE statement
- Add basic I4GLExpressionNode.getType() support
- Add sqlcode global variable and the database side effects
- Implement array of chars
- Add fill() to array values

Version 0.1.0

- Initial version
- Basic functionality
- Decouple project version from graal version
- Split parser from language
- Take out com.guillermomolina from class path
- Move launcher.I4GLMain to Launcher
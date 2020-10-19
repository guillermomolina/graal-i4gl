/*
 * Based on https://github.com/antlr/grammars-v4/blob/master/informix/informix.g4
 */

parser grammar I4GLParser;

options { tokenVocab=I4GLLexer; }

compilationUnit:
	databaseDeclaration? globalDeclaration? typeDeclarations? mainBlock? functionOrReportDefinitions
		? EOF;

identifier: IDENT;

mainBlock:
	MAIN typeDeclarations? mainStatements? END MAIN;

mainStatements: (
		databaseDeclaration
		| deferStatement
		| statement
	)+;

deferStatement: DEFER (INTERRUPT | QUIT);

functionOrReportDefinitions: (
		reportDefinition
		| functionDefinition
	)+;

returnStatement: RETURN expressionList?;

functionDefinition:
	FUNCTION identifier parameterList? typeDeclarations? codeBlock? END FUNCTION;

parameterList: LPAREN parameterGroup* RPAREN;

parameterGroup: identifier (COMMA identifier)*;

globalDeclaration:
	GLOBALS (string | typeDeclarations END GLOBALS);

typeDeclarations: typeDeclaration+;

typeDeclaration:
	DEFINE variableDeclaration (COMMA variableDeclaration)*;

variableDeclaration: identifier (COMMA identifier)* type;
//| identifier type (COMMA identifier type)*

type:
	typeIdentifier
	| indirectType
	| largeType
	| structuredType;

indirectType: LIKE tableIdentifier DOT identifier;

typeIdentifier: charType | numberType | timeType;

largeType: TEXT | BYTE;

numberType: (
		BIGINT
		| INTEGER
		| INT
		| SMALLINT
		| REAL
		| SMALLFLOAT
	)
	| (DECIMAL | DEC | NUMERIC | MONEY) (
		LPAREN numericConstant (COMMA numericConstant)? RPAREN
	)?
	| (FLOAT | DOUBLE PRECISION) (LPAREN numericConstant RPAREN)?;

charType:
	varchar LPAREN numericConstant (COMMA numericConstant)? RPAREN
	| character (LPAREN numericConstant RPAREN)?;

character: CHAR | NCHAR | CHARACTER;

varchar: VARCHAR | NVARCHAR;

timeType:
	DATE
	| DATETIME datetimeQualifier
	| INTERVAL intervalQualifier;

datetimeQualifier:
	YEAR TO yearQualifier
	| MONTH TO monthQualifier
	| DAY TO dayQualifier
	| HOUR TO hourQualifier
	| MINUTE TO minuteQualifier
	| SECOND TO secondQualifier
	| FRACTION TO fractionQualifier;

intervalQualifier:
	YEAR (LPAREN numericConstant RPAREN)? TO yearQualifier
	| MONTH (LPAREN numericConstant RPAREN)? TO monthQualifier
	| DAY (LPAREN numericConstant RPAREN)? TO dayQualifier
	| HOUR (LPAREN numericConstant RPAREN)? TO hourQualifier
	| MINUTE (LPAREN numericConstant RPAREN)? TO minuteQualifier
	| SECOND (LPAREN numericConstant RPAREN)? TO secondQualifier
	| FRACTION TO fractionQualifier;

unitType: yearQualifier;

yearQualifier: YEAR | monthQualifier;

monthQualifier: MONTH | dayQualifier;

dayQualifier: DAY | hourQualifier;

hourQualifier: HOUR | minuteQualifier;

minuteQualifier: MINUTE | secondQualifier;

secondQualifier: SECOND | fractionQualifier;

fractionQualifier: FRACTION (LPAREN numericConstant RPAREN)?;

structuredType: recordType | arrayType | dynArrayType;

recordType:
	RECORD (
		(variableDeclaration (COMMA variableDeclaration)*) END RECORD
		| (LIKE tableIdentifier DOT STAR)
	);

arrayIndexer:
	LBRACK dimensionSize (
		COMMA dimensionSize
		| COMMA dimensionSize COMMA dimensionSize
	)? RBRACK;

dimensionSize: UNSIGNED_INTEGER;

arrayType: ARRAY arrayIndexer OF arrayTypeType;

arrayTypeType: recordType | typeIdentifier | largeType;

dynArrayType:
	DYNAMIC ARRAY WITH numericConstant DIMENSIONS OF dynArrayTypeType;

dynArrayTypeType: recordType | typeIdentifier;

string: STRING_LITERAL;

statement: (label COLON)? unlabelledStatement;

codeBlock: (statement | databaseDeclaration)+;

label: identifier;

unlabelledStatement: simpleStatement | structuredStatement;

simpleStatement:
	assignmentStatement
	| callStatement
	| sqlStatements SEMI?
	| otherI4GLStatement
	| menuInsideStatement
	| constructInsideStatement
	| displayInsideStatement
	| inputInsideStatement
	| debugStatement;

debugStatement: BREAKPOINT;

runStatement:
	RUN (variable | string) (IN FORM MODE | IN LINE MODE)? (
		WITHOUT WAITING
		| RETURNING variable
	)?;

//Exploded for an easier visitor variable: identifier (DOT identifier)* variableIndex?;
variable: notIndexedVariable | indexedVariable;

notIndexedVariable: simpleVariable | recordVariable;

simpleVariable: identifier;

recordVariable: simpleVariable (DOT identifier)+;

indexedVariable: notIndexedVariable variableIndex;

variableIndex: LBRACK expressionList RBRACK;

/*
 thruNotation : ( (THROUGH |THRU) (SAME DOT)? identifier )? ;
 */
componentVariable:
	identifier (
		(DOT STAR)
		| (
			DOT componentVariable (
				(THROUGH | THRU) componentVariable
			)?
		)
	);

assignmentStatement:
	LET (simpleAssignmentStatement | multipleAssignmentStatement);

simpleAssignmentStatement: variable EQUAL assignmentValue;

assignmentValue: expressionList | NULL;

multipleAssignmentStatement:
	identifier DOT STAR EQUAL identifier DOT STAR;

callStatement:
	CALL function (RETURNING variableList)?;

functionIdentifier: (
		DAY
		| YEAR
		| MONTH
		| TODAY
		| WEEKDAY
		| MDY
		| COLUMN
		| SUM
		| COUNT
		| AVG
		| MIN
		| MAX
		| EXTEND
		| DATE
		| TIME
		| INFIELD
		| PREPARE
	)
	| constantIdentifier;

actualParameter: STAR | expression;

gotoStatement: GOTO COLON? label;

condition: (TRUE | FALSE) | logicalTerm (OR logicalTerm)*;

logicalTerm: logicalFactor (AND logicalFactor)*;

logicalFactor:
	// Added "prior" to a comparison expression to support use of a condition in a connect_clause.
	// (sqlExpression NOT? IN) sqlExpression NOT? IN expressionSet
	| (sqlExpression NOT? LIKE) sqlExpression NOT? LIKE sqlExpression (
		ESCAPE CHAR_LITERAL
	)?
	| (sqlExpression NOT? BETWEEN) sqlExpression NOT? BETWEEN sqlExpression AND sqlExpression
	| (sqlExpression IS NOT? NULL) sqlExpression IS NOT? NULL
	| quantifiedFactor quantifiedFactor
	| (NOT condition) NOT condition
	| LPAREN condition RPAREN
	| sqlExpression relationalOperator sqlExpression;

quantifiedFactor: (
		sqlExpression relationalOperator (ALL | ANY)? subquery
	) sqlExpression relationalOperator (ALL | ANY)? subquery
	| (NOT? EXISTS subquery) NOT? EXISTS subquery
	| subquery;

expressionSet: sqlExpression sqlExpression | subquery;

subquery: LPAREN sqlSelectStatement RPAREN;

sqlExpression: sqlTerm ((PLUS | MINUS) sqlTerm)*;

sqlAlias: AS identifier;

sqlTerm: sqlFactor ((sqlMultiply | DIV | SLASH) sqlFactor)*;

sqlMultiply: STAR;

sqlFactor: sqlFactor2 (DOUBLEVERTBAR sqlFactor2)*;

sqlFactor2:
	sqlVariable (UNITS unitType)?
	| sqlLiteral (UNITS unitType)?
	| groupFunction LPAREN (STAR | ALL | DISTINCT)? (
		sqlExpression (COMMA sqlExpression)*
	)? RPAREN
	| sqlFunction (
		LPAREN sqlExpression (COMMA sqlExpression)* RPAREN
	)
	| (PLUS | MINUS) sqlExpression
	| LPAREN sqlExpression RPAREN
	| sqlExpressionList;

sqlExpressionList:
	LPAREN sqlExpression (COMMA sqlExpression)+ RPAREN;

sqlLiteral: numericConstant | string | NULL | FALSE | TRUE;

sqlVariable: columnsTableId;

sqlFunction:
	numberFunction
	| charFunction
	| dateFunction
	| otherFunction;

dateFunction: YEAR | DATE | DAY | MONTH;

numberFunction: MOD;

charFunction: LENGTH;

groupFunction: AVG | COUNT | MAX | MIN | SUM;

otherFunction: (DECODE | NVL) | constantIdentifier;

// This is not being used currently, but might be useful at some point.
sqlPseudoColumn: USER;

relationalOperator:
	EQUAL
	| NOT_EQUAL
	| LE
	| LT
	| GE
	| GT
	| LIKE
	| NOT? MATCHES;

ifCondition:
	TRUE
	| FALSE
	| ifCondition2 (relationalOperator ifCondition2)?;

ifCondition2: ifLogicalTerm (OR ifLogicalTerm)*;

ifLogicalTerm: ifLogicalFactor (AND ifLogicalFactor)*;

ifLogicalFactor:
	// Added "prior" to a comparison expression to support use of a condition in a connect_clause.
	expression IS NOT? NULL
	| NOT ifCondition
	| LPAREN ifCondition RPAREN
	| expression;

expressionList: expression (COMMA expression)*;

expression: /*sign?*/ term (addingOperator term)*;

addingOperator: PLUS | MINUS;

term: factor (multiplyingOperator factor)*;

multiplyingOperator: STAR | SLASH | DIV | MOD;

factor: factorTypes (UNITS unitType)?;

factorTypes:
	GROUP? function
	| variable // or function without arguments
	| constant
	| LPAREN expression RPAREN
	| NOT factor;

function:
	functionIdentifier LPAREN (
		actualParameter (COMMA actualParameter)*
	)? RPAREN;

constant: numericConstant | string;

numericConstant: integer | real;

structuredStatement: conditionalStatement | repetetiveStatement;

conditionalStatement: ifStatement | caseStatement;

ifStatement:
	IF ifCondition THEN codeBlock? (ELSE codeBlock?)? END IF;

repetetiveStatement:
	whileStatement
	| forEachStatement
	| forStatement;

whileStatement: WHILE ifCondition codeBlock? END WHILE;

forStatement:
	FOR controlVariable EQUAL initialValue TO finalValue (
		STEP numericConstant
	)? codeBlock? END FOR;

controlVariable: identifier;

initialValue: expression;

finalValue: expression;

forEachStatement:
	FOREACH identifier (USING variableList)? (INTO variableList)? (
		WITH REOPTIMIZATION
	)? codeBlock? END FOREACH;

variableList: variable (COMMA variable)*;

variableOrConstantList: expression (COMMA expression)*;

caseStatement: caseStatement1 | caseStatement2;

caseStatement1:
	CASE expression (WHEN expression codeBlock)+ (
		OTHERWISE codeBlock
	)? END CASE;

caseStatement2:
	CASE (WHEN ifCondition codeBlock)+ (OTHERWISE codeBlock)? END CASE;

otherI4GLStatement:
	otherProgramFlowStatement
	| otherStorageStatement
	| reportStatement
	| screenStatement;

otherProgramFlowStatement:
	runStatement
	| gotoStatement
	| SLEEP expression
	| exitStatements
	| continueStatements
	| returnStatement;

exitTypes:
	FOREACH
	| FOR
	| CASE
	| CONSTRUCT
	| DISPLAY
	| INPUT
	| MENU
	| REPORT
	| WHILE;

exitStatements:
	EXIT exitTypes
	| EXIT PROGRAM (LPAREN expression RPAREN | expression)?;

continueStatements: CONTINUE exitTypes;

otherStorageStatement:
	ALLOCATE ARRAY identifier arrayIndexer
	| LOCATE variableList IN (MEMORY | FILE (variable | string)?)
	| DEALLOCATE ARRAY identifier
	| RESIZE ARRAY identifier arrayIndexer
	| FREE variable (COMMA variable)*
	| INITIALIZE variable (COMMA variable)* (
		TO NULL
		| LIKE expressionList
	)
	| VALIDATE variable (COMMA variable)* LIKE expression (
		COMMA expression
	)*;

printExpressionItem:
	COLUMN expression
	| (PAGENO | LINENO)
	| BYTE variable
	| TEXT variable
	| expression (SPACE | SPACES)? (
		WORDWRAP (RIGHT MARGIN numericConstant)?
	)?;

printExpressionList:
	printExpressionItem (COMMA printExpressionItem)*;

reportStatement:
	START REPORT identifier (
		TO (expression | PIPE expression | PRINTER)
	)? (
		WITH (
			(LEFT MARGIN numericConstant)
			| (RIGHT MARGIN numericConstant)
			| (TOP MARGIN numericConstant)
			| (BOTTOM MARGIN numericConstant)
			| (PAGE LENGTH numericConstant)
			| (TOP OF PAGE string)
		)*
	)?
	| TERMINATE REPORT identifier
	| FINISH REPORT identifier
	| PAUSE string?
	| NEED expression LINES
	| PRINT (printExpressionList SEMI? | FILE string)?
	| SKIP2 (expression (LINE | LINES) | TO TOP OF PAGE)
	| OUTPUT TO REPORT identifier LPAREN expressionList? RPAREN;

fieldName: ((identifier (LBRACK numericConstant RBRACK)?) DOT)? identifier
	| (identifier (LBRACK numericConstant RBRACK)?) DOT (
		STAR
		| identifier thruNotation?
	);

thruNotation: (THROUGH | THRU) (SAME DOT)? identifier;

fieldList: expression (COMMA expression)*;

keyList: expression (COMMA expression)*;

constructEvents:
	BEFORE CONSTRUCT
	| AFTER CONSTRUCT
	| BEFORE FIELD fieldList
	| AFTER FIELD fieldList
	| ON KEY LPAREN keyList RPAREN;

constructInsideStatement:
	NEXT FIELD (fieldName | NEXT | PREVIOUS)
	| CONTINUE CONSTRUCT
	| EXIT CONSTRUCT;

specialAttribute: REVERSE | BLINK | UNDERLINE;

attribute: (
		BLACK
		| BLUE
		| CYAN
		| GREEN
		| MAGENTA
		| RED
		| WHITE
		| YELLOW
		| BOLD
		| DIM
		| NORMAL
		| INVISIBLE
	)? specialAttribute (COMMA specialAttribute)*;

attributeList: (ATTRIBUTE | ATTRIBUTES) LPAREN attribute RPAREN;

constructGroupStatement: constructEvents codeBlock+;

constructStatement:
	CONSTRUCT (
		BY NAME variable ON columnsList
		| variable ON columnsList FROM fieldList
	) attributeList? (HELP numericConstant)? (
		constructGroupStatement+ END CONSTRUCT
	)?;

displayArrayStatement:
	DISPLAY ARRAY expression TO expression attributeList? displayEvents* (
		END DISPLAY
	)?;

displayInsideStatement: CONTINUE DISPLAY | EXIT DISPLAY;

displayEvents: ON KEY LPAREN keyList RPAREN codeBlock+;

displayStatement:
	DISPLAY (
		BY NAME variableList
		| (displayValue (COMMA displayValue)*) (
			TO fieldList
			| AT expression COMMA expression
		)?
	) attributeList?;

displayValue:
	expression (CLIPPED | USING string)?
	| ASCII UNSIGNED_INTEGER;

errorStatement: ERROR expressionList attributeList?;

messageStatement: MESSAGE expressionList attributeList?;

promptStatement:
	PROMPT expressionList attributeList? FOR CHAR? variable (
		HELP numericConstant
	)? attributeList? (
		(ON KEY LPAREN keyList RPAREN codeBlock?)* END PROMPT
	)?;

inputEvents: (BEFORE | AFTER) (INPUT | ROW | INSERT | DELETE)
	| BEFORE FIELD fieldList
	| AFTER FIELD fieldList
	| ON KEY LPAREN keyList RPAREN;

inputInsideStatement:
	NEXT FIELD (fieldName | (NEXT | PREVIOUS))
	| (CONTINUE INPUT | EXIT INPUT);

inputGroupStatement: inputEvents codeBlock*;

inputStatement:
	INPUT (
		BY NAME expressionList (WITHOUT DEFAULTS)?
		| expressionList (WITHOUT DEFAULTS)? FROM fieldList
	) attributeList? (HELP numericConstant)? (
		inputGroupStatement+ END INPUT
	)?;

inputArrayStatement:
	INPUT ARRAY expression (WITHOUT DEFAULTS)? FROM expression (
		COMMA expression
	)* (HELP numericConstant)? attributeList? (
		inputGroupStatement+ END INPUT
	)?;

menuEvents:
	BEFORE MENU
	| COMMAND (
		(KEY LPAREN keyList RPAREN)? expression expression? (
			HELP numericConstant
		)?
	);

menuInsideStatement:
	NEXT OPTION (expression | ALL) (COMMA expression)*
	| SHOW OPTION (expression | ALL) (COMMA expression)*
	| HIDE OPTION (expression | ALL) (COMMA expression)*
	| CONTINUE MENU
	| EXIT MENU;

menuGroupStatement: menuEvents codeBlock?;

menuStatement: MENU expression menuGroupStatement* END MENU;

reservedLinePosition:
	FIRST (PLUS numericConstant)?
	| numericConstant
	| LAST (MINUS numericConstant)?;

specialWindowAttribute: (
		BLACK
		| BLUE
		| CYAN
		| GREEN
		| MAGENTA
		| RED
		| WHITE
		| YELLOW
		| BOLD
		| DIM
		| NORMAL
		| INVISIBLE
	)
	| REVERSE
	| BORDER
	| (PROMPT | FORM | MENU | MESSAGE) LINE reservedLinePosition
	| COMMENT LINE (reservedLinePosition | OFF);

windowAttribute:
	specialWindowAttribute (COMMA specialWindowAttribute)*;

windowAttributeList: (ATTRIBUTE | ATTRIBUTES) LPAREN windowAttribute RPAREN;

optionStatement: (
		MESSAGE LINE expression
		| PROMPT LINE expression
		| MENU LINE expression
		| COMMENT LINE expression
		| ERROR LINE expression
		| FORM LINE expression
		| INPUT (WRAP | NO WRAP)
		| INSERT KEY expression
		| DELETE KEY expression
		| NEXT KEY expression
		| PREVIOUS KEY expression
		| ACCEPT KEY expression
		| HELP FILE expression
		| HELP KEY expression
		| INPUT attributeList
		| DISPLAY attributeList
		| SQL INTERRUPT (ON | OFF)
		| FIELD ORDER (CONSTRAINED | UNCONSTRAINED)
	);

optionsStatement:
	OPTIONS optionStatement (COMMA optionStatement)*;

screenStatement:
	CLEAR (FORM | WINDOW identifier | WINDOW? SCREEN | fieldList)
	| CLOSE WINDOW identifier
	| CLOSE FORM identifier
	| constructStatement
	| CURRENT WINDOW IS (SCREEN | identifier)
	| displayStatement
	| displayArrayStatement
	| DISPLAY FORM identifier attributeList?
	| errorStatement
	| messageStatement
	| promptStatement
	| inputStatement
	| inputArrayStatement
	| menuStatement
	| OPEN FORM expression FROM expression
	| OPEN WINDOW expression AT expression COMMA expression (
		WITH FORM expression
		| WITH expression ROWS COMMA expression COLUMNS
	) windowAttributeList?
	| optionsStatement
	| SCROLL fieldList (COMMA fieldList)* (UP | DOWN) (
		BY numericConstant
	)?;

sqlStatements:
	cursorManipulationStatement
	| dataDefinitionStatement
	| dataManipulationStatement
	| dynamicManagementStatement
	| queryOptimizationStatement
	| dataIntegrityStatement
	| clientServerStatement;

cursorManipulationStatement:
	CLOSE cursorName
	| DECLARE cursorName (
		CURSOR (WITH HOLD)? FOR (
			sqlSelectStatement (FOR UPDATE (OF columnsList)?)?
			| sqlInsertStatement
			| statementId
		)
		| SCROLL CURSOR (WITH HOLD)? FOR (
			sqlSelectStatement
			| statementId
		)
	)
	| FETCH (
		NEXT
		| (PREVIOUS | PRIOR)
		| FIRST
		| LAST
		| CURRENT
		| RELATIVE expression
		| ABSOLUTE expression
	)? cursorName (INTO variableList)?
	| FLUSH cursorName
	| OPEN cursorName (USING variableList)?
	| PUT cursorName (FROM variableOrConstantList)?;

columnsList: columnsTableId (COMMA columnsTableId)*;

statementId: identifier;

cursorName: identifier;

dataType: type;

columnItem:
	identifier (
		dataType
		| (BYTE | TEXT) (IN (TABLE | identifier))?
	) (NOT NULL)?
	| UNIQUE LPAREN (identifier (COMMA identifier)*)? RPAREN (
		CONSTRAINT identifier
	)?;

dataDefinitionStatement:
	DROP TABLE identifier
	| CREATE TEMP? TABLE identifier LPAREN columnItem (
		COMMA columnItem
	)* RPAREN (WITH NO LOG)? (IN identifier)? (
		EXTENT SIZE numericConstant
	)? (NEXT SIZE numericConstant)? (
		LOCK MODE LPAREN (PAGE | ROW) RPAREN
	)?
	| CREATE UNIQUE? CLUSTER? INDEX identifier ON identifier LPAREN identifier (
		ASC
		| DESC
	)? (COMMA identifier (ASC | DESC)?)* RPAREN
	| DROP INDEX identifier;

dataManipulationStatement:
	sqlInsertStatement
	| sqlDeleteStatement
	| sqlSelectStatement
	| sqlUpdateStatement
	| sqlLoadStatement
	| sqlUnLoadStatement;

sqlSelectStatement: mainSelectStatement;

columnsTableId:
	STAR
	| (tableIdentifier variableIndex?) (
		DOT STAR
		| DOT columnsTableId
	)?;

selectList: (
		sqlExpression sqlAlias? (COMMA sqlExpression sqlAlias?)*
	);

headSelectStatement:
	SELECT (ALL | (DISTINCT | UNIQUE))? selectList;

tableQualifier:
	identifier COLON
	| identifier ATSYMBOL identifier COLON
	| string;

tableIdentifier: tableQualifier? identifier;

fromTable: OUTER? tableIdentifier sqlAlias?;

tableExpression: simpleSelectStatement;

fromSelectStatement:
	FROM (fromTable | LPAREN tableExpression RPAREN sqlAlias?) (
		COMMA (
			fromTable
			| LPAREN tableExpression RPAREN sqlAlias?
		)
	)*;

aliasName: identifier;

mainSelectStatement:
	headSelectStatement (INTO variableList)? fromSelectStatement whereStatement?
		groupByStatement? havingStatement? unionSelectStatement? orderbyStatement?
		selectIntoTempStatement? selectWithNoLogStatement?;

selectIntoTempStatement: INTO TEMP identifier;

selectWithNoLogStatement: WITH NO LOG;

unionSelectStatement: (UNION ALL? simpleSelectStatement);

simpleSelectStatement:
	headSelectStatement fromSelectStatement whereStatement? groupByStatement? havingStatement?
		unionSelectStatement?;

whereStatement: WHERE condition;

groupByStatement: GROUP BY variableOrConstantList;

havingStatement: HAVING condition;

orderbyColumn: expression (ASC | DESC)?;

orderbyStatement: ORDER BY orderbyColumn (COMMA orderbyColumn)*;

sqlLoadStatement:
	LOAD FROM (variable | string) (DELIMITER (variable | string))? (
		INSERT INTO tableIdentifier (LPAREN columnsList RPAREN)?
		| sqlInsertStatement
	);

sqlUnLoadStatement:
	UNLOAD TO (variable | string) (DELIMITER (variable | string))? sqlSelectStatement;

sqlInsertStatement:
	INSERT INTO tableIdentifier (LPAREN columnsList RPAREN)? (
		VALUES LPAREN expressionList RPAREN
		| simpleSelectStatement
	);

sqlUpdateStatement:
	UPDATE tableIdentifier SET (
		(
			columnsTableId EQUAL expression (
				COMMA columnsTableId EQUAL expression
			)*
		)
		| (
			(LPAREN columnsList RPAREN | (aliasName DOT)? STAR) EQUAL (
				LPAREN expressionList RPAREN
				| (aliasName DOT)? STAR
			)
		)
	) (WHERE (condition | CURRENT OF cursorName))?;

sqlDeleteStatement:
	DELETE FROM tableIdentifier (
		WHERE (condition | CURRENT OF cursorName)
	)?;

actualParameterList: actualParameter (COMMA actualParameter)*;

dynamicManagementStatement:
	PREPARE cursorName FROM expression
	| EXECUTE cursorName (USING variableList)?
	| FREE (cursorName | statementId)
	| LOCK TABLE expression IN (SHARE | EXCLUSIVE) MODE;

queryOptimizationStatement:
	UPDATE STATISTICS (FOR TABLE tableIdentifier)?
	| SET LOCK MODE TO (WAIT seconds? | NOT WAIT)
	| SET EXPLAIN (ON | OFF)
	| SET ISOLATION TO (
		CURSOR STABILITY
		| (DIRTY | COMMITTED | REPEATABLE) READ
	)
	| SET BUFFERED? LOG;

databaseDeclaration:
	DATABASE (identifier (ATSYMBOL identifier)?) EXCLUSIVE? SEMI?;

clientServerStatement: CLOSE DATABASE;

dataIntegrityStatement:
	wheneverStatement
	| BEGIN WORK
	| COMMIT WORK
	| ROLLBACK WORK;

wheneverStatement: WHENEVER wheneverType wheneverFlow;

wheneverType:
	NOT FOUND
	| ANY? (SQLERROR | ERROR)
	| (SQLWARNING | WARNING);

wheneverFlow: (CONTINUE | STOP)
	| CALL identifier
	| (GO TO | GOTO) COLON? identifier;

reportDefinition:
	REPORT identifier parameterList? typeDeclarations? outputReport? (
		ORDER EXTERNAL? BY variableList
	)? formatReport? END REPORT;

outputReport:
	OUTPUT (REPORT TO (string | PIPE string | PRINTER))? (
		(LEFT MARGIN numericConstant)
		| (RIGHT MARGIN numericConstant)
		| (TOP MARGIN numericConstant)
		| (BOTTOM MARGIN numericConstant)
		| (PAGE LENGTH numericConstant)
		| (TOP OF PAGE string)
	)*;

formatReport:
	FORMAT (
		EVERY ROW
		| (
			(
				FIRST? PAGE HEADER
				| PAGE TRAILER
				| ON (EVERY ROW | LAST ROW)
				| (BEFORE | AFTER) GROUP OF variable
			) codeBlock
		)+
	);

integer: sign? UNSIGNED_INTEGER;

real: sign? UNSIGNED_REAL;

seconds: UNSIGNED_INTEGER;

sign: PLUS | MINUS;

constantIdentifier: (
		ACCEPT
		| ASCII
		| COUNT
		| CURRENT
		| FALSE
		| FIRST
		| FOUND
		| GROUP
		| HIDE
		| INDEX
		| INT_FLAG
		| INTERRUPT
		| LAST
		| LENGTH
		| LINENO
		| MDY
		| NO
		| NOT
		| NOTFOUND
		| NULL
		| PAGENO
		| REAL
		| SIZE
		| SQL
		| STATUS
		| TEMP
		| TIME
		| TODAY
		| TRUE
		| USER
		| WAIT
		| WEEKDAY
		| WORK
	)
	| identifier;

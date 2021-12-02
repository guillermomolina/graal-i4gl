/*
 * Based on https://github.com/antlr/grammars-v4/blob/master/informix/informix.g4
 */

parser grammar I4GLParser;

options {
	tokenVocab = I4GLLexer;
}

module:
	databaseDeclaration? globalsDeclaration? typeDeclarations? mainFunctionDefinition?
		functionOrReportDefinitions? EOF;

identifier: IDENT;

mainFunctionDefinition:
	MAIN typeDeclarations? mainStatements? END MAIN;

mainStatements: (
		exitProgramStatement
		| databaseDeclaration
		| deferStatement
		| statement
	)+;

deferStatement: DEFER (INTERRUPT | QUIT);

exitProgramStatement:
	EXIT PROGRAM (LPAREN expression RPAREN | expression)?;

functionOrReportDefinitions: (
		reportDefinition
		| functionDefinition
	)+;

functionDefinition:
	FUNCTION identifier parameterList? typeDeclarations? codeBlock? END FUNCTION;

parameterList: LPAREN parameterGroup? RPAREN;

parameterGroup: identifier (COMMA identifier)*;

globalsDeclaration:
	GLOBALS (string | typeDeclarations END GLOBALS);

typeDeclarations: typeDeclaration+;

typeDeclaration:
	DEFINE variableDeclaration (COMMA variableDeclaration)*;

variableDeclaration: identifier (COMMA identifier)* type;

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
		| INT8
		| SMALLINT
		| LONG
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

structuredType:
	recordLikeType
	| recordType
	| arrayType
	| dynArrayType;

recordLikeType: RECORD LIKE tableIdentifier DOT STAR;

recordType:
	RECORD (variableDeclaration (COMMA variableDeclaration)*) END RECORD;

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

codeBlock: (statement | databaseDeclaration | returnStatement)+;

returnStatement: RETURN expressionOrComponentVariableList?;

label: identifier;

unlabelledStatement: simpleStatement | structuredStatement;

simpleStatement:
	assignmentStatement
	| callStatement
	| sqlStatement
	| otherI4GLStatement
	| menuInsideStatement
	| constructInsideStatement
	| displayInsideStatement
	| inputInsideStatement
	| breakpointStatement;

breakpointStatement: BREAKPOINT;

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

assignmentStatement:
	LET (simpleAssignmentStatement | multipleAssignmentStatement);

simpleAssignmentStatement: variable EQUAL assignmentValue;

assignmentValue: concatExpression | function SEMI | NULL;

multipleAssignmentStatement:
	identifier DOT STAR EQUAL identifier DOT STAR;

callStatement:
	CALL function (SEMI | RETURNING variableOrComponentList)?;

gotoStatement: GOTO COLON? label;

ifCondition:
	booleanConstant
	| ifLogicalTerm (OR ifLogicalTerm)*;

ifLogicalTerm: ifLogicalFactor (AND ifLogicalFactor)*;

ifLogicalFactor:
	// Added "prior" to a comparison expression to support use of a condition in a connect_clause.
	expression IS NOT? NULL
	| NOT ifCondition
	| LPAREN ifCondition RPAREN
	| ifLogicalRelation;

ifLogicalRelation: expression (relationalOperator expression)?;

relationalOperator:
	EQUAL
	| NOT_EQUAL
	| LE
	| LT
	| GE
	| GT
	| LIKE
	| NOT? MATCHES;

expressionList: expression (COMMA expression)*;

expression: /*sign?*/ term (addingOperator term)*;

addingOperator: PLUS | MINUS;

term: factor (multiplyingOperator factor)*;

multiplyingOperator: STAR | SLASH | DIV | MOD;

factor: factorTypes CLIPPED? (USING string)? (UNITS unitType)?;

factorTypes:
	GROUP? function
	| constant
	| variable // or function without arguments
	| LPAREN expression RPAREN
	| NOT factor;

function:
	identifier LPAREN expressionOrComponentVariableList? RPAREN;

constant:
	asciiConstant
	| booleanConstant
	| numericConstant
	| string;

asciiConstant: ASCII UNSIGNED_INTEGER;

booleanConstant: TRUE | FALSE;

numericConstant: integer | real;

structuredStatement: conditionalStatement | repetetiveStatement;

conditionalStatement: ifStatement | caseStatement;

ifStatement:
	IF ifCondition THEN codeBlock? (ELSE codeBlock?)? END IF;

caseStatement: caseStatement1 | caseStatement2;

caseStatement1:
	CASE expression (WHEN expression codeBlock)+ (
		OTHERWISE codeBlock
	)? END CASE;

caseStatement2:
	CASE (WHEN ifCondition codeBlock)+ (OTHERWISE codeBlock)? END CASE;

repetetiveStatement:
	whileStatement
	| forEachStatement
	| forStatement;

whileStatement: WHILE ifCondition codeBlock? END WHILE;

forEachStatement:
	FOREACH cursorName usingVariableList? intoVariableList? (
		WITH REOPTIMIZATION
	)? codeBlock? END FOREACH;

usingVariableList: USING variableList;

intoVariableList: INTO variableOrComponentList;

variableOrComponentList:
	variableOrComponent (COMMA variableOrComponent)*;

variableOrComponent: variable | componentVariable;

componentVariable:
	starComponentVariable
	| thruComponentVariable;

starComponentVariable:
	simpleVariable (DOT identifier)* DOT STAR;

thruComponentVariable:
	recordVariable (THROUGH | THRU) recordVariable;

forStatement:
	FOR controlVariable EQUAL initialValue TO finalValue (
		STEP numericConstant
	)? codeBlock? END FOR;

controlVariable: identifier;

initialValue: expression;

finalValue: expression;

variableList: variable (COMMA variable)*;

variableOrConstantList: expression (COMMA expression)*;

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
	| continueStatements;

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

exitStatements: EXIT exitTypes;

continueStatements: CONTINUE exitTypes;

otherStorageStatement:
	ALLOCATE ARRAY identifier arrayIndexer
	| LOCATE variableList IN (MEMORY | FILE (variable | string)?)
	| DEALLOCATE ARRAY identifier
	| RESIZE ARRAY identifier arrayIndexer
	| FREE variableList // name clash, marked as SQL
	| INITIALIZE variableList (TO NULL | LIKE expressionList)
	| VALIDATE variableList LIKE expression (COMMA expression)*;

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
	START_REPORT identifier (
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

columnsList: columnsTableId (COMMA columnsTableId)*;

displayArrayStatement:
	DISPLAY ARRAY expression TO expression attributeList? displayEvents* (
		END DISPLAY
	)?;

displayInsideStatement: CONTINUE DISPLAY | EXIT DISPLAY;

displayEvents: ON KEY LPAREN keyList RPAREN codeBlock+;

concatExpression: expressionOrComponentVariableList;

expressionOrComponentVariableList:
	expressionOrComponentVariable (
		COMMA expressionOrComponentVariable
	)*;

expressionOrComponentVariable: expression | componentVariable;

displayStatement:
	DISPLAY (
		BY NAME variableList
		| concatExpression (
			TO fieldList
			| AT expression COMMA expression
		)?
	) attributeList?;

errorStatement: ERROR concatExpression attributeList?;

messageStatement: MESSAGE concatExpression attributeList?;

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
	| CLOSE_WINDOW identifier
	| CLOSE_FORM identifier
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
	| OPEN_FORM expression FROM expression
	| OPEN_WINDOW expression AT expression COMMA expression (
		WITH FORM expression
		| WITH expression ROWS COMMA expression COLUMNS
	) windowAttributeList?
	| optionsStatement
	| SCROLL fieldList (COMMA fieldList)* (UP | DOWN) (
		BY numericConstant
	)?;

sqlStatement:
	cursorManipulationStatement
	| dataDefinitionStatement
	| dataManipulationStatement
	| dynamicManagementStatement
	| queryOptimizationStatement
	| dataIntegrityStatement
	| clientServerStatement;

cursorManipulationStatement:
	closeCursorStatement
	| declareCursorStatement
	| fetchCursorStatement
	| flushCursorStatement
	| openCursorStatement
	| putCursorStatement;

closeCursorStatement: CLOSE cursorName;

declareCursorStatement:
	DECLARE cursorName (
		CURSOR (WITH HOLD)? FOR (
			sqlSelectStatement (FOR UPDATE (OF columnsList)?)?
			| sqlInsertStatement
			| statementId
		)
		| SCROLL CURSOR (WITH HOLD)? FOR (
			sqlSelectStatement
			| statementId
		)
	);

fetchCursorStatement:
	FETCH (
		NEXT
		| (PREVIOUS | PRIOR)
		| FIRST
		| LAST
		| CURRENT
		| RELATIVE expression
		| ABSOLUTE expression
	)? cursorName intoVariableList?;

flushCursorStatement: FLUSH cursorName;

openCursorStatement: OPEN cursorName usingVariableList?;

putCursorStatement:
	PUT cursorName (FROM variableOrConstantList)?;

statementId: identifier;

cursorName: identifier;

dataDefinitionStatement:
	DROP SQL_MODE_WORD+
	| CREATE SQL_MODE_WORD+;

dataManipulationStatement:
	sqlInsertStatement
	| sqlDeleteStatement
	| sqlSelectStatement
	| sqlUpdateStatement
	| sqlLoadStatement
	| sqlUnLoadStatement;

sqlInsertStatement: INSERT_INTO SQL_MODE_WORD+;

sqlDeleteStatement: DELETE_FROM SQL_MODE_WORD+;

sqlSelectStatement:
	SELECT SQL_MODE_WORD+ intoVariableList? FROM SQL_MODE_WORD+;

sqlUpdateStatement: UPDATE SQL_MODE_WORD+;

sqlLoadStatement:
	LOAD_FROM (variable | SQL_MODE_WORD) (
		DELIMITER (variable | SQL_MODE_WORD)
	)? SQL_MODE_WORD+;

sqlUnLoadStatement:
	UNLOAD_TO (variable | SQL_MODE_WORD) (
		DELIMITER (variable | SQL_MODE_WORD)
	)? SQL_MODE_WORD+;

dynamicManagementStatement:
	PREPARE cursorName FROM expression
	| EXECUTE cursorName usingVariableList?
	| FREE (cursorName | statementId)
	| LOCK SQL_MODE_WORD+;

queryOptimizationStatement:
	UPDATE_STATISTICS SQL_MODE_WORD*
	| SET SQL_MODE_WORD+;

dataIntegrityStatement:
	wheneverStatement
	| START SQL_MODE_WORD* // mysql dialect
	| BEGIN SQL_MODE_WORD*
	| COMMIT SQL_MODE_WORD*
	| ROLLBACK SQL_MODE_WORD*;

wheneverStatement: WHENEVER wheneverType wheneverFlow;

wheneverType:
	NOT FOUND
	| ANY? (SQLERROR | ERROR)
	| (SQLWARNING | WARNING);

wheneverFlow: (CONTINUE | STOP)
	| CALL identifier
	| (GO TO | GOTO) COLON? identifier;

clientServerStatement: CLOSE_DATABASE;

columnsTableId:
	STAR
	| (tableIdentifier variableIndex?) (
		DOT STAR
		| DOT columnsTableId
	)?;

tableQualifier:
	identifier COLON
	| identifier ATSYMBOL identifier COLON
	| string;

tableIdentifier: tableQualifier? identifier;

databaseDeclaration:
	DATABASE (identifier (ATSYMBOL identifier)?) EXCLUSIVE? SEMI?;

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

sign: PLUS | MINUS;

/*
 * Based on https://github.com/antlr/grammars-v4/blob/master/informix/informix.g4
 */

lexer grammar I4GLLexer;

channels {
	COMMENT_CHANNEL
}

fragment A: ('a' | 'A');

fragment B: ('b' | 'B');

fragment C: ('c' | 'C');

fragment D: ('d' | 'D');

fragment E: ('e' | 'E');

fragment F: ('f' | 'F');

fragment G: ('g' | 'G');

fragment H: ('h' | 'H');

fragment I: ('i' | 'I');

fragment J: ('j' | 'J');

fragment K: ('k' | 'K');

fragment L: ('l' | 'L');

fragment M: ('m' | 'M');

fragment N: ('n' | 'N');

fragment O: ('o' | 'O');

fragment P: ('p' | 'P');

fragment Q: ('q' | 'Q');

fragment R: ('r' | 'R');

fragment S: ('s' | 'S');

fragment T: ('t' | 'T');

fragment U: ('u' | 'U');

fragment V: ('v' | 'V');

fragment W: ('w' | 'W');

fragment X: ('x' | 'X');

fragment Y: ('y' | 'Y');

fragment Z: ('z' | 'Z');

ABSOLUTE: A B S O L U T E;

AFTER: A F T E R;

ACCEPT: A C C E P T;

AGGREGATE: A G G R E G A T E;

ALLOCATE: A L L O C A T E;

ALL: A L L;

ALL_ROWS: A L L '_' R O W S;

AND: A N D;

ANY: A N Y;

AS: A S;

ASC: A S C;

ASCII: A S C I I;

AT: A T;

ATTRIBUTE: A T T R I B U T E;

ATTRIBUTES: A T T R I B U T E S;

AVERAGE: A V E R A G E;

AVG: A V G;

ARRAY: A R R A Y;

BEFORE: B E F O R E;

BEGIN: B E G I N;

//TRANSACTION CONTROL

BETWEEN: B E T W E E N;

BIGINT: B I G I N T;

BLACK: B L A C K;

BLINK: B L I N K;

BLUE: B L U E;

BOLD: B O L D;

BORDER: B O R D E R;

BOTTOM: B O T T O M;

// Not standard
BREAKPOINT: B R E A K P O I N T;

BUFFERED: B U F F E R E D;

BY: B Y;

BYTE: B Y T E;

CACHE: C A C H E;

CALL: C A L L;

CASE: C A S E;

CHAR: C H A R;

CHARACTER: C H A R A C T E R;

CHAR_LENGTH: C H A R '_' L E N G T H;

CHECK: C H E C K;

CLEAR: C L E A R;

CLIPPED: C L I P P E D;

CLOSE: C L O S E;

CLUSTER: C L U S T E R;

COLUMN: C O L U M N;

COLUMNS: C O L U M N S;

COMMAND: C O M M A N D;

COMMENT: C O M M E N T;

COMMIT: C O M M I T;

COMMITTED: C O M M I T T E D;

CONSTANT: C O N S T A N T;

CONSTRAINED: C O N S T R A I N E D;

CONSTRAINT: C O N S T R A I N T;

CONSTRUCT: C O N S T R U C T;

CONTINUE: C O N T I N U E;

COUNT: C O U N T;

COPY: C O P Y;

CRCOLS: C R C O L S;

CREATE: C R E A T E;

CURRENT: C U R R E N T;

CURSOR: C U R S O R;

CYAN: C Y A N;

DATABASE: D A T A B A S E;

DATE: D A T E;

DATETIME: D A T E T I M E;

DAY: D A Y;

DEALLOCATE: D E A L L O C A T E;

DEC: D E C;

DECIMAL: D E C I M A L;

DECODE: D E C O D E;

DECLARE: D E C L A R E;

DEFAULT: D E F A U L T;

DEFAULTS: D E F A U L T S;

DEFER: D E F E R;

DEFINE: D E F I N E;

DELETE: D E L E T E;

//SQL

DELIMITER: D E L I M I T E R;

//SQL

DESC: D E S C;

DIM: D I M;

DIMENSIONS: D I M E N S I O N S;

DIRTY: D I R T Y;

DISPLAY: D I S P L A Y -> mode(DEFAULT_MODE);

DISTINCT: D I S T I N C T;

DIV: D I V;

DO: D O;

DOUBLE: D O U B L E;

DOWN: D O W N;

DROP: D R O P;

DYNAMIC: D Y N A M I C;

ELSE: E L S E;

END: E N D;

ERROR: E R R O R;

ESCAPE: E S C A P E;

EVERY: E V E R Y;

EXCLUSIVE: E X C L U S I V E;

EXEC: E X E C;

EXECUTE: E X E C U T E;

EXIT: E X I T;

EXISTS: E X I S T S;

EXPLAIN: E X P L A I N;

EXTEND: E X T E N D;

EXTENT: E X T E N T;

EXTERNAL: E X T E R N A L;

FALSE: F A L S E;

FETCH: F E T C H;

FIELD: F I E L D;

FIELD_TOUCHED: F I E L D '_' T O U C H E D;

FILE: F I L E;

FINISH: F I N I S H;

FIRST: F I R S T;

FIRST_ROWS: F I R S T '_' R O W S;

FLOAT: F L O A T;

FLUSH: F L U S H;

FOR: F O R;

FORM: F O R M;

FORMAT: F O R M A T;

FORMONLY: F O R M O N L Y;

FOREACH: F O R E A C H;

FOUND: F O U N D;

FRACTION: F R A C T I O N;

FREE: F R E E;

FROM: F R O M;

//SQL & PREPARE

FUNCTION: F U N C T I O N;

GET_FLDBUF: G E T F L D B U F;

GLOBALS: G L O B A L S;

GO: G O;

GOTO: G O T O;

GREEN: G R E E N;

GROUP: G R O U P;

//SQL

HAVING: H A V I N G;

//SQL

HEADER: H E A D E R;

HELP: H E L P;

HIDE: H I D E;

HOLD: H O L D;

//CURSOR

HOUR: H O U R;

//CURSOR

IF: I F;

IN: I N;

//SQL

INNER: I N N E R;

//SQL

INDEX: I N D E X;

//SQL

INDICATOR: I N D I C A T O R;

//SQL

INFIELD: I N F I E L D;

INITIALIZE: I N I T I A L I Z E;

INPUT: I N P U T;

INSERT: I N S E R T;

//SQL

INSTRUCTIONS: I N S T R U C T I O N S;

INTO: I N T O;

//SQL & CURSOR 

INT: I N T;

INT8: I N T '8';

INT_FLAG: I N T '_' F L A G;

INTEGER: I N T E G E R;

INTERRUPT: I N T E R R U P T;

INTERVAL: I N T E R V A L;

INVISIBLE: I N V I S I B L E;

IS: I S;

ISOLATION: I S O L A T I O N;

JOIN: J O I N;

KEY: K E Y;

LABEL: L A B E L;

LAST: L A S T;

LEFT: L E F T;

LENGTH: L E N G T H;

LET: L E T;

LIKE: L I K E;

LINE: L I N E;

LINENO: L I N E N O;

LINES: L I N E S;

LOAD: L O A D;

LOCATE: L O C A T E;

LOCK: L O C K;

LOG: L O G;

LONG: L O N G;

MAGENTA: M A G E N T A;

MATCHES: M A T C H E S;

MENU: M E N U;

MESSAGE: M E S S A G E;

MAIN: M A I N;

MARGIN: M A R G I N;

MAX: M A X;

MDY: M D Y;

MEMORY: M E M O R Y;

MIN: M I N;

MINUTE: M I N U T E;

MOD: M O D;

MODE: M O D E;

MODULE: M O D U L E;

MONTH: M O N T H;

MONEY: M O N E Y;

NCHAR: N C H A R;

NAME: N A M E;

NEED: N E E D;

NEXT: N E X T;

NEW: N E W;

NORMAL: N O R M A L;

NO: N O;

NOT: N O T;

NOTFOUND: N O T F O U N D;

NOW: N O W;

NUMERIC: N U M E R I C;

NULL: N U L L;

NVARCHAR: N V A R C H A R;

//SQL

NVL: N V L;

OF: O F;

OFF: O F F;

ON: O N;

OPEN: O P E N;

OPTION: O P T I O N;

OPTIONS: O P T I O N S;

OR: O R;

ORD: O R D;

ORDER: O R D E R;

//SQL

OUTPUT: O U T P U T;

OUTER: O U T E R;

//SQL

OTHERWISE: O T H E R W I S E;

PAGE: P A G E;

PAGENO: P A G E N O;

PAUSE: P A U S E;

PERCENT: P E R C E N T;

PIPE: P I P E;

PRECISION: P R E C I S I O N;

PREPARE: P R E P A R E;

PREVIOUS: P R E V I O U S;

PRINT: P R I N T;

PRINTER: P R I N T E R;

PRIOR: P R I O R;

PROGRAM: P R O G R A M;

PROMPT: P R O M P T;

PUT: P U T;

QUIT: Q U I T;

QUIT_FLAG: Q U I T '_' F L A G;

RECORD: R E C O R D;

REAL: R E A L;

READ: R E A D;

RED: R E D;

RELATIVE: R E L A T I V E;

REMOVE: R E M O V E;

REOPTIMIZATION: R E O P T I M I Z A T I O N;

REPEATABLE: R E P E A T A B L E;

REPEAT: R E P E A T;

REPORT: R E P O R T;

RESIZE: R E S I Z E;

RETURN: R E T U R N;

RETURNING: R E T U R N I N G;

REVERSE: R E V E R S E;

RIGHT: R I G H T;

ROLLBACK: R O L L B A C K;

ROW: R O W;

ROWS: R O W S;

RUN: R U N;

SAME: S A M E;

SCREEN: S C R E E N;

SCROLL: S C R O L L;

SECOND: S E C O N D;

SKIP2: S K I P;

SELECT: S E L E C T -> mode(SQL_MODE);

SET: S E T;

SHARE: S H A R E;

SHOW: S H O W;

SIZE: S I Z E;

SLEEP: S L E E P;

SMALLFLOAT: S M A L L F L O A T;

SMALLINT: S M A L L I N T;

SPACE: S P A C E;

SPACES: S P A C E S;

SQL: S Q L;

SQLERROR: S Q L E R R O R;

SQLWARNING: S Q L W A R N I N G;

START: S T A R T;

STABILITY: S T A B I L I T Y;

STATISTICS: S T A T I S T I C S;

STATUS: S T A T U S;

STEP: S T E P;

STOP: S T O P;

SUM: S U M;

TABLE: T A B L E;

TABLES: T A B L E S;

TERMINATE: T E R M I N A T E;

TEMP: T E M P;

TEXT: T E X T;

THEN: T H E N;

THROUGH: T H R O U G H;

THRU: T H R U;

TIME: T I M E;

TO: T O;

TODAY: T O D A Y;

TOP: T O P;

TRAILER: T R A I L E R;

TRUE: T R U E;

TYPE: T Y P E;

UNCONSTRAINED: U N C O N S T R A I N E D;

UNDERLINE: U N D E R L I N E;

UNION: U N I O N;

UNIQUE: U N I Q U E;

UNITS: U N I T S;

UNLOAD: U N L O A D;

UP: U P;

UPDATE: U P D A T E;

//SQL

USER: U S E R;

USING: U S I N G;

VALIDATE: V A L I D A T E;

VALUES: V A L U E S;

//SQL

VARCHAR: V A R C H A R;

//SQL

WEEKDAY: W E E K D A Y;

VIEW: V I E W;

//SQL

WAIT: W A I T;

WAITING: W A I T I N G;

WARNING: W A R N I N G;

WHEN: W H E N;

WHENEVER: W H E N E V E R;

WHERE: W H E R E;

//SQL

WHILE: W H I L E;

WHITE: W H I T E;

WITH: W I T H;

WITHOUT: W I T H O U T;

WINDOW: W I N D O W;

WORDWRAP: W O R D W R A P;

WORK: W O R K;

WRAP: W R A P;

YEAR: Y E A R;

YELLOW: Y E L L O W;

//----------------------------------------------------------------------------
// OPERATORS ----------------------------------------------------------------------------

PLUS: '+';

MINUS: '-';

STAR: '*';

SLASH: '/';

COMMA: ',';

SEMI: ';';

COLON: ':';

EQUAL: '=';

//NOT_EQUAL : "<>" | "!=" ; Why did I do this? Isn't this handle by just increasing the look ahead?

NOT_EQUAL: '<>' | '!=' | '^=';
//   : '<' (('>') | ('='))? | '!=' | '^='

LT: '<';

LE: '<=';

GE: '>=';

GT: '>';

//GT
// : '>' ('=')? ;

LPAREN: '(';

RPAREN: ')';

LBRACK: '[';

RBRACK: ']';

DOT: '.';

ATSYMBOL: '@';

DOUBLEVERTBAR: '||';

//COMMENT_1
// : '{' ( : '\r' '\n' | '\r' | '\n' | ~('}' | '\n' | '\r') )* '}' ; an identifier. Note that
// testLiterals is set to true! This means that after we match the rule, we look in the literals
// table to see if it's a literal or really an identifer

IDENT: ('a' .. 'z' | '_') (
		'a' .. 'z'
		| 'A' .. 'Z'
		| '_'
		| '0' .. '9'
	)*;

// character literals
CHAR_LITERAL: '\'' ( /*ESC |*/ ~'\'') '\'';

// string literals
STRING_LITERAL: ('"') (
/*ESC|*/ ~ ('"' | '\\'))* ('"')
	| ('\'') (
/*ESC|~*/ ('\\' | '\''))* ('\'');

// a numeric literal

UNSIGNED_INTEGER:
	'.' (('0' .. '9')+)?
	| (('0' .. '9') ('0' .. '9')*);

UNSIGNED_REAL: ('0' .. '9')+ '.' ('0' .. '9')* EXPONENT?
	| '.' ('0' .. '9')+ EXPONENT?
	| ('0' .. '9')+ EXPONENT;

fragment EXPONENT: ('e' | 'E') ('+' | '-')? ('0' .. '9')+;

// escape sequence -- note that this is protected; it can only be called from another lexer rule --
// it will not ever directly return a token to the parser There are various ambiguities hushed in
// this rule. The optional '0'...'9' digit matches should be matched here rather than letting them
// go back to STRING_LITERAL to be matched. ANTLR does the right thing by matching immediately;
// hence, it's ok to shut off the FOLLOW ambig warnings.
/*
 ESC : '\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '"' | '\'' | '\\' | ('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT
 HEX_DIGIT | '0'..'3' (
 
 : '0'..'7' (
 
 : '0'..'7' )? )? | '4'..'7' (
 
 : '0'..'7' )? ) ;
 */
// hexadecimal digit (again, note it's protected!)

HEX_DIGIT: ('0' .. '9' | 'a' .. 'f');

WS: [ \t\r\n\u000C]+ -> channel(HIDDEN);

// Multi-line comment not standard
ML_COMMENT: '/*' .*? '*/' -> channel(COMMENT_CHANNEL);

// Single-line comments

SL_COMMENT: '#' ~[\r\n]* -> channel(COMMENT_CHANNEL);

SL_COMMENT_2: '--' ~[\r\n]* -> channel(COMMENT_CHANNEL);

// SQL Mode

mode SQL_MODE;

SQL_MODE_DISPLAY: DISPLAY -> type(DISPLAY), mode(DEFAULT_MODE);

SQL_MODE_IGNORED_WORDS: ~[ \t\r\n\u000C]+;



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

ALLOCATE: A L L O C A T E;

ALL: A L L;

AND: A N D;

ANY: A N Y;

ASC: A S C;

ASCII: A S C I I;

AT: A T;

ATTRIBUTE: A T T R I B U T E;

ATTRIBUTES: A T T R I B U T E S;

ARRAY: A R R A Y;

BEFORE: B E F O R E;

BEGIN: B E G I N -> mode(SQL_MODE);

BIGINT: B I G I N T;

BLACK: B L A C K;

BLINK: B L I N K;

BLUE: B L U E;

BOLD: B O L D;

BORDER: B O R D E R;

BOTTOM: B O T T O M;

// Not standard
BREAKPOINT: B R E A K P O I N T;

BY: B Y;

BYTE: B Y T E;

CALL: C A L L;

CASE: C A S E;

CHAR: C H A R;

CHARACTER: C H A R A C T E R;

CLEAR: C L E A R;

CLIPPED: C L I P P E D;

CLOSE_DATABASE: C L O S E WS D A T A B A S E;

CLOSE_FORM: C L O S E WS F O R M;

CLOSE_WINDOW: C L O S E WS W I N D O W;

CLOSE: C L O S E -> mode(SQL_MODE);

COLUMN: C O L U M N;

COLUMNS: C O L U M N S;

COMMAND: C O M M A N D;

COMMENT: C O M M E N T;

COMMIT: C O M M I T -> mode(SQL_MODE);

CONSTRAINED: C O N S T R A I N E D;

CONSTRUCT: C O N S T R U C T;

CONTINUE: C O N T I N U E;

CREATE: C R E A T E -> mode(SQL_MODE);

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

DECLARE: D E C L A R E;

DEFAULTS: D E F A U L T S;

DEFER: D E F E R;

DEFINE: D E F I N E;

DELETE_FROM: D E L E T E WS F R O M -> mode(SQL_MODE);

DELETE: D E L E T E;

//SQL

DELIMITER: D E L I M I T E R -> mode(SQL_MODE);

//SQL

DESC: D E S C;

DIM: D I M;

DIMENSIONS: D I M E N S I O N S;

DISPLAY: D I S P L A Y -> mode(DEFAULT_MODE);

DIV: D I V;

DOUBLE: D O U B L E;

DOWN: D O W N;

DROP: D R O P -> mode(SQL_MODE);

DYNAMIC: D Y N A M I C;

ELSE: E L S E;

END: E N D;

ERROR: E R R O R;

EVERY: E V E R Y;

EXCLUSIVE: E X C L U S I V E;

EXECUTE: E X E C U T E;

EXIT: E X I T;

EXTERNAL: E X T E R N A L;

FALSE: F A L S E;

FETCH: F E T C H -> mode(SQL_MODE);

FIELD: F I E L D;

FILE: F I L E;

FINISH: F I N I S H;

FIRST: F I R S T;

FLOAT: F L O A T;

FLUSH: F L U S H -> mode(SQL_MODE);

FOR: F O R;

FORM: F O R M;

FORMAT: F O R M A T;

FOREACH: F O R E A C H;

FOUND: F O U N D;

FRACTION: F R A C T I O N;

FREE: F R E E;

FROM: F R O M -> mode(SQL_MODE);

FUNCTION: F U N C T I O N;

GLOBALS: G L O B A L S;

GO: G O;

GOTO: G O T O;

GREEN: G R E E N;

GROUP: G R O U P;

HEADER: H E A D E R;

HELP: H E L P;

HIDE: H I D E;

HOLD: H O L D;

HOUR: H O U R;

IF: I F;

IN: I N;

INITIALIZE: I N I T I A L I Z E;

INPUT: I N P U T;

INSERT_INTO: I N S E R T WS I N T O -> mode(SQL_MODE);

INSERT: I N S E R T;

INTO: I N T O;

INT: I N T;

INT8: I N T '8';

INTEGER: I N T E G E R;

INTERRUPT: I N T E R R U P T;

INTERVAL: I N T E R V A L;

INVISIBLE: I N V I S I B L E;

IS: I S;

KEY: K E Y;

LAST: L A S T;

LEFT: L E F T;

LENGTH: L E N G T H;

LET: L E T;

LIKE: L I K E;

LINE: L I N E;

LINENO: L I N E N O;

LINES: L I N E S;

LOAD_FROM: L O A D WS F R O M -> mode(SQL_MODE);

LOCATE: L O C A T E;

LOCK: L O C K -> mode(SQL_MODE);

LONG: L O N G;

MAGENTA: M A G E N T A;

MATCHES: M A T C H E S;

MENU: M E N U;

MESSAGE: M E S S A G E;

MAIN: M A I N;

MARGIN: M A R G I N;

MEMORY: M E M O R Y;

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

NORMAL: N O R M A L;

NO: N O;

NOT: N O T;

NOTFOUND: N O T F O U N D;

NUMERIC: N U M E R I C;

NULL: N U L L;

NVARCHAR: N V A R C H A R;

OF: O F;

OFF: O F F;

ON: O N;

OPEN_FORM: O P E N WS F O R M;

OPEN_WINDOW: O P E N WS W I N D O W;

OPEN: O P E N -> mode(SQL_MODE);

OPTION: O P T I O N;

OPTIONS: O P T I O N S;

OR: O R;

ORDER: O R D E R;

OUTPUT: O U T P U T;

OTHERWISE: O T H E R W I S E;

PAGE: P A G E;

PAGENO: P A G E N O;

PAUSE: P A U S E;

PIPE: P I P E;

PRECISION: P R E C I S I O N;

PREPARE: P R E P A R E;

PREVIOUS: P R E V I O U S;

PRINT: P R I N T;

PRINTER: P R I N T E R;

PRIOR: P R I O R;

PROGRAM: P R O G R A M;

PROMPT: P R O M P T;

PUT: P U T -> mode(SQL_MODE);

QUIT: Q U I T;

RECORD: R E C O R D;

REAL: R E A L;

RED: R E D;

RELATIVE: R E L A T I V E;

REOPTIMIZATION: R E O P T I M I Z A T I O N;

REPORT: R E P O R T;

RESIZE: R E S I Z E;

RETURN: R E T U R N;

RETURNING: R E T U R N I N G;

REVERSE: R E V E R S E;

RIGHT: R I G H T;

ROLLBACK: R O L L B A C K -> mode(SQL_MODE);

ROW: R O W;

ROWS: R O W S;

RUN: R U N;

SAME: S A M E;

SCREEN: S C R E E N;

SCROLL: S C R O L L;

SECOND: S E C O N D;

SKIP2: S K I P;

SELECT: S E L E C T -> mode(SQL_MODE);

SET: S E T -> mode(SQL_MODE);

SHOW: S H O W;

SLEEP: S L E E P;

SMALLFLOAT: S M A L L F L O A T;

SMALLINT: S M A L L I N T;

SPACE: S P A C E;

SPACES: S P A C E S;

SQL: S Q L;

SQLERROR: S Q L E R R O R;

SQLWARNING: S Q L W A R N I N G;

START_REPORT: S T A R T WS R E P O R T;

START: S T A R T -> mode(SQL_MODE);

STEP: S T E P;

STOP: S T O P;

TERMINATE: T E R M I N A T E;

TEMP: T E M P;

TEXT: T E X T;

THEN: T H E N;

THROUGH: T H R O U G H;

THRU: T H R U;

TO: T O;

TOP: T O P;

TRAILER: T R A I L E R;

TRUE: T R U E;

UNCONSTRAINED: U N C O N S T R A I N E D;

UNDERLINE: U N D E R L I N E;

UNION: U N I O N;

UNIQUE: U N I Q U E;

UNITS: U N I T S;

UNLOAD_TO: U N L O A D WS T O -> mode(SQL_MODE);

UP: U P;

UPDATE_STATISTICS:
	U P D A T E WS S T A T I S T I C S -> mode(SQL_MODE);

UPDATE: U P D A T E -> mode(SQL_MODE);

USING: U S I N G;

VALIDATE: V A L I D A T E;

VARCHAR: V A R C H A R;

WAITING: W A I T I N G;

WARNING: W A R N I N G;

WHEN: W H E N;

WHENEVER: W H E N E V E R;

WHILE: W H I L E;

WHITE: W H I T E;

WITH: W I T H;

WITHOUT: W I T H O U T;

WINDOW: W I N D O W;

WORDWRAP: W O R D W R A P;

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

DOLLAR: '$';

IDENT: ('a' .. 'z' | 'A' .. 'Z' | '_') (
		'a' .. 'z'
		| 'A' .. 'Z'
		| '_'
		| '0' .. '9'
	)*;

STRING_LITERAL: (
		'"' STRING_CHARACTER_DQ* '"'
		| '\'' STRING_CHARACTER_SQ* '\''
	);

fragment STRING_CHARACTER_DQ:
	~["\\\r\n]
	| '\\' ESCAPE_CHARACTER;

fragment STRING_CHARACTER_SQ:
	~['\\\r\n]
	| '\\' ESCAPE_CHARACTER;

fragment ESCAPE_CHARACTER: ['"\\bfnrtv];

// a numeric literal

UNSIGNED_INTEGER:
	'.' (('0' .. '9')+)?
	| (('0' .. '9') ('0' .. '9')*);

UNSIGNED_REAL: ('0' .. '9')+ '.' ('0' .. '9')* EXPONENT?
	| '.' ('0' .. '9')+ EXPONENT?
	| ('0' .. '9')+ EXPONENT;

fragment EXPONENT: ('e' | 'E') ('+' | '-')? ('0' .. '9')+;

HEX_DIGIT: ('0' .. '9' | 'a' .. 'f');

WS: [ \t\r\n\u000C]+ -> channel(HIDDEN);

// Multi-line comment not standard
ML_COMMENT: '/*' .*? '*/' -> channel(COMMENT_CHANNEL);

// Single-line comments

SL_COMMENT: '#' ~[\r\n]* -> channel(COMMENT_CHANNEL);

SL_COMMENT_2: '--' ~[\r\n]* -> channel(COMMENT_CHANNEL);

mode SQL_MODE;

SQL_MODE_BEGIN: BEGIN -> type(BEGIN), mode(DEFAULT_MODE);

SQL_MODE_CALL: CALL -> type(CALL), mode(DEFAULT_MODE);

SQL_MODE_CASE: CASE -> type(CASE), mode(DEFAULT_MODE);

SQL_MODE_CLOSE: CLOSE -> type(CLOSE), mode(DEFAULT_MODE);

SQL_MODE_COMMIT: COMMIT -> type(COMMIT), mode(DEFAULT_MODE);

SQL_MODE_CREATE: CREATE -> type(CREATE), mode(DEFAULT_MODE);

SQL_MODE_DECLARE:
	DECLARE -> type(DECLARE), mode(DEFAULT_MODE);

SQL_MODE_DELETE_FROM:
	DELETE_FROM -> type(DELETE_FROM), mode(DEFAULT_MODE);

SQL_MODE_DELIMITER:
	DELIMITER -> type(DELIMITER), mode(DEFAULT_MODE);

SQL_MODE_DISPLAY:
	DISPLAY -> type(DISPLAY), mode(DEFAULT_MODE);

SQL_MODE_DROP: DROP -> type(DROP), mode(DEFAULT_MODE);

SQL_MODE_END: END -> type(END), mode(DEFAULT_MODE);

SQL_MODE_EXECUTE:
	EXECUTE -> type(EXECUTE), mode(DEFAULT_MODE);

SQL_MODE_FETCH: FETCH -> type(FETCH), mode(DEFAULT_MODE);

SQL_MODE_FLUSH: FLUSH -> type(FLUSH), mode(DEFAULT_MODE);

SQL_MODE_FOREACH:
	FOREACH -> type(FOREACH), mode(DEFAULT_MODE);

SQL_MODE_FOR: FOR -> type(FOR), mode(DEFAULT_MODE);

SQL_MODE_FREE: FREE -> type(FREE), mode(DEFAULT_MODE);

SQL_MODE_FROM: FROM -> type(FROM);

SQL_MODE_IF: IF -> type(IF), mode(DEFAULT_MODE);

SQL_MODE_INSERT_INTO:
	INSERT_INTO -> type(INSERT_INTO), mode(DEFAULT_MODE);

SQL_MODE_INTO: INTO -> type(INTO), mode(DEFAULT_MODE);

SQL_MODE_LET: LET -> type(LET), mode(DEFAULT_MODE);

SQL_MODE_LOAD_FROM:
	LOAD_FROM -> type(LOAD_FROM), mode(DEFAULT_MODE);

SQL_MODE_LOCK: LOCK -> type(LOCK), mode(DEFAULT_MODE);

SQL_MODE_OPEN: OPEN -> type(OPEN), mode(DEFAULT_MODE);

SQL_MODE_PREPARE:
	PREPARE -> type(PREPARE), mode(DEFAULT_MODE);

SQL_MODE_PUT: PUT -> type(PUT), mode(DEFAULT_MODE);

SQL_MODE_ROLLBACK:
	ROLLBACK -> type(ROLLBACK), mode(DEFAULT_MODE);

SQL_MODE_SELECT: SELECT -> type(SELECT), mode(DEFAULT_MODE);

SQL_MODE_SET: SET -> type(SET), mode(DEFAULT_MODE);

SQL_MODE_START: START -> type(START), mode(DEFAULT_MODE);

SQL_MODE_UNLOAD_TO:
	UNLOAD_TO -> type(UNLOAD_TO), mode(DEFAULT_MODE);

SQL_MODE_UPDATE_STATISTICS:
	UPDATE_STATISTICS -> type(UPDATE_STATISTICS), mode(DEFAULT_MODE);

SQL_MODE_UPDATE: UPDATE -> type(UPDATE), mode(DEFAULT_MODE);

SQL_MODE_WHILE: WHILE -> type(WHILE), mode(DEFAULT_MODE);

SQL_MODE_WORD: ~[ \t\r\n\u000C]+;

SQL_MODE_WS: WS -> type(WS), channel(HIDDEN);


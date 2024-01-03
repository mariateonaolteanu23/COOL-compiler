lexer grammar CoolLexer;

tokens { ERROR }

@header{
    package cool.lexer;	
}

@members{    
    private void raiseError(String msg) {
        setText(msg);
        setType(ERROR);
    }
}

/* KEYWORDS */
/* case insensitive */
CLASS: ('C'| 'c')('L'| 'l')('A'| 'a')('S'| 's')('S'| 's');
IF : ('I'| 'i')('F'| 'f');
FI : ('F'| 'f')('I'| 'i');
ELSE: ('E'| 'e')('L'| 'l')('S'| 's')('E'| 'e');
THEN: ('T'| 't')('H'| 'h')('E'| 'e')('N'| 'n');
IN: ('I'| 'i')('N'| 'n');
INHERITS: ('I'| 'i')('N'| 'n')('H'| 'h')('E'| 'e')('R'| 'r')('I'| 'i')('T'| 't')('S'| 's');
ISVOID: ('I'| 'i')('S'| 's')('V'| 'v')('O'| 'o')('I'| 'i')('D'| 'd');
LET: ('L'| 'l')('E'| 'e')('T'| 't');
LOOP: ('L'| 'l')('O'| 'o')('O'| 'o')('P'| 'p');
POOL: ('P'| 'p')('O'| 'o')('O'| 'o')('L'| 'l');
WHILE: ('W'| 'w')('H'| 'h')('I'| 'i')('L'| 'l')('E'| 'e');
CASE: ('C'| 'c')('A'| 'a')('S'| 's')('E'| 'e');
ESAC: ('E'| 'e')('S'| 's')('A'| 'a')('C'| 'c');
NEW: ('N'| 'n')('E'| 'e')('W'| 'w');
OF: ('O'| 'o')('F'| 'f');
NOT: ('N'| 'n')('O'| 'o')('T'| 't');
/* case sensitive */
BOOL: 'true' | 'false';

STRING : '"' (ESCAPED_QUOTE | ESCAPED_NEW_LINE | .)*?

        ( EOF {raiseError("EOF in string constant");}
        | NEW_LINE {raiseError("Unterminated string constant");}
        | ('"' {
                String str = getText();

                if (str.contains("\u0000")) {
                    raiseError("String contains null character");
                    return;
                }

                str = str.substring(1, str.length() - 1)
                         .replaceAll("\\\\n", "\n")
                         .replaceAll("\\\\t", "\t")
                         .replaceAll("\\\\b", "\b")
                         .replaceAll("\\\\f", "\f")
                         .replaceAll("(\\\\)([^\\\\])", "$2");

                if (str.length() > 1024) {
                    raiseError("String constant too long");
                    return;
                }

                setText(str);
        }));

/* Integer - non-empty strings of digits 0-9*/
fragment DIGIT: [0-9];
INT : DIGIT | [1-9] DIGIT+;

/* Identifiers are strings (other than keywords) consisting of
  letters, digits, and the underscore character */
fragment GENERIC_ID : ([a-zA-Z] | '_' | DIGIT)*;

/* Type identifiers begin with a capital letter */
TYPE : [A-Z] GENERIC_ID;

/* Object identifiers begin with a lower case letter*/
ID: [a-z] GENERIC_ID;

COLON: ':';
SEMI : ';';
COMMA : ',';
LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';

ASSIGN : '<-';
TILDE : '~';
PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
DOT: '.';
AT: '@';
RESULT: '=>';

EQ : '=';
LT : '<';
LE : '<=';


fragment CARRIAGE_RETURN : '\r'?;
fragment NEW_LINE : CARRIAGE_RETURN '\n';
fragment ESCAPED_NEW_LINE: '\\' NEW_LINE;
fragment ESCAPED_QUOTE: '\\"';

/* “--” and the next newline
  (or EOF, if there is no next newline) are treated as comments*/
LINE_COMMENT: '--' .*? (NEW_LINE | EOF) -> skip;

/* enclosing text in (∗ . . . ∗) */
BLOCK_COMMENT: '(*'
                    (BLOCK_COMMENT | .)*?
                ('*)' {skip();}| EOF { raiseError("EOF in comment"); });

/* ending of a block comm is not matched */
INVALID_BLOCK_COMMENT: '*)' {raiseError("Unmatched *)");};

/* trim white spaces*/
WS:   [ \n\f\r\t]+ -> skip;

/* invalid character */
INVALID: . {raiseError("Invalid character: " + getText()); };
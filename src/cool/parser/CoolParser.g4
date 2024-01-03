parser grammar CoolParser;

options {
    tokenVocab = CoolLexer;
}

@header{
    package cool.parser;
}

program
    :  (classes+=class)* EOF;


class
    : CLASS name=TYPE (INHERITS parent=TYPE)?
        LBRACE (features+=feature SEMI)* RBRACE SEMI                            # classDef
    ;

feature
    : name=ID COLON type=TYPE (ASSIGN init=expr)*                               # attribute
    | name=ID LPAREN
                (formals+=formal (COMMA formals+=formal)*)?
              RPAREN
                COLON type=TYPE
                LBRACE body=expr RBRACE                                         # funcDef
      ;

formal
    :   name=ID COLON type=TYPE                                                 #formalDef
    ;

expr
    : name=ID LPAREN (args+=expr (COMMA args+=expr)*)? RPAREN       # implicitDispatch
    | e=expr (AT type=TYPE)? DOT
        name=ID LPAREN (args+=expr (COMMA args+=expr)*)? RPAREN     # explicitDispatch
    | IF cond=expr THEN thenBranch=expr ELSE elseBranch=expr FI     # if
    | WHILE cond=expr LOOP body=expr POOL                           # while
    | LBRACE (body+=expr SEMI)+ RBRACE                              # block
    | LET localVar+=local (COMMA localVar+= local)* IN body=expr    # let
    | CASE e=expr OF (cases+=caseBranch)+ ESAC                      # case
    | ISVOID e=expr                                                 # isvoid
    | NEW type=TYPE                                                 # new
    | left=expr op=(MULT | DIV) right=expr                          # multDiv
    | left=expr op=(PLUS | MINUS) right=expr                        # plusMinus
    | left=expr op=(LT | LE | EQ) right=expr                        # relational
    | NOT e=expr                                                    # not
    | name=ID ASSIGN e=expr                                         # assign
    | TILDE e=expr                                                  # bitComplement
    | LPAREN e=expr RPAREN                                          # paren
    | ID                                                            # id
    | STRING                                                        # string
    | INT                                                           # int
    | BOOL                                                          # bool
    ;

caseBranch
    : name=ID COLON type=TYPE RESULT e=expr SEMI                #caseBranchDef
    ;

local
    : name=ID COLON type=TYPE (ASSIGN init=expr)*               #localDef
    ;


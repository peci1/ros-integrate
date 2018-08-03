package com.perfetto.ros.integrate;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.perfetto.ros.integrate.psi.ROSMsgTypes;
import com.intellij.psi.TokenType;

%%

%class ROSMsgLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \t\f]
FIRST_NAME_CHARACTER=[a-zA-Z/]
NAME_CHARACTER=[a-zA-Z0-9_/]
END_OF_LINE_COMMENT=("#")[^\r\n]*
CONST_ASSIGNER="="
NEG_OPERATOR="-"
LEAD_NUMBER=[1-9]
NUMBER=[0-9]
ARRAY_LEAD="["
ARRAY_END="]"
CONST_STRING=[^\n]
FIRST_CONST_STRING=[^\n\ ]
KEYTYPE_INT=u?int(8|16|32|64)
KEYTYPE_FLOAT=float(32|64)
KEYTYPE_TIME=(time)|(duration)
KEYTYPE_STRING=string
KEYTYPE_BOOL=bool
KEYTYPE_NUM={KEYTYPE_BOOL}|{KEYTYPE_INT}|{KEYTYPE_FLOAT}
KEYTYPE_OTHER={KEYTYPE_STRING}|{KEYTYPE_TIME}

%states END_TYPE, IN_ARRAY, END_ARRAY, START_NAME, END_NAME, START_CONST, END_LINE
%states END_INT_TYPE, IN_INT_ARRAY, END_INT_ARRAY, START_INT_NAME, END_INT_NAME, START_INT_CONST, NEG_NUM

%%

<YYINITIAL> ---                                             { yybegin(END_LINE); return ROSMsgTypes.SERVICE_SEPERATOR; }

<YYINITIAL,END_NAME,END_INT_NAME> {END_OF_LINE_COMMENT}     { yybegin(YYINITIAL); return ROSMsgTypes.COMMENT; }

<YYINITIAL> {KEYTYPE_NUM}                                   { yybegin(END_INT_TYPE); return ROSMsgTypes.KEYTYPE; }
<YYINITIAL> {KEYTYPE_OTHER}                                 { yybegin(END_TYPE); return ROSMsgTypes.KEYTYPE; }
<YYINITIAL> {FIRST_NAME_CHARACTER}{NAME_CHARACTER}*         { yybegin(END_TYPE); return ROSMsgTypes.TYPE; }

<END_TYPE> {ARRAY_LEAD}                                     { yybegin(IN_ARRAY); return ROSMsgTypes.LBRACKET; }
<END_INT_TYPE> {ARRAY_LEAD}                                 { yybegin(IN_INT_ARRAY); return ROSMsgTypes.LBRACKET; }

<IN_ARRAY> {NUMBER}+                                        { yybegin(IN_ARRAY); return ROSMsgTypes.NUMBER; }
<IN_INT_ARRAY> {NUMBER}+                                    { yybegin(IN_INT_ARRAY); return ROSMsgTypes.NUMBER; }

<IN_ARRAY> {ARRAY_END}                                      { yybegin(END_ARRAY); return ROSMsgTypes.RBRACKET; }
<IN_INT_ARRAY> {ARRAY_END}                                  { yybegin(END_INT_ARRAY); return ROSMsgTypes.RBRACKET; }

<END_ARRAY,END_TYPE> {WHITE_SPACE}                          { yybegin(START_NAME); return TokenType.WHITE_SPACE; }
<END_INT_ARRAY,END_INT_TYPE> {WHITE_SPACE}                  { yybegin(START_INT_NAME); return TokenType.WHITE_SPACE; }

<START_NAME> {FIRST_NAME_CHARACTER}{NAME_CHARACTER}*        { yybegin(END_NAME); return ROSMsgTypes.NAME; }
<START_INT_NAME> {FIRST_NAME_CHARACTER}{NAME_CHARACTER}*    { yybegin(END_INT_NAME); return ROSMsgTypes.NAME; }

<END_NAME> {CONST_ASSIGNER}                                 { yybegin(START_CONST); return ROSMsgTypes.CONST_ASSIGNER; }
<END_INT_NAME> {CONST_ASSIGNER}                             { yybegin(START_INT_CONST); return ROSMsgTypes.CONST_ASSIGNER; }

<START_CONST> {WHITE_SPACE}+                                { yybegin(START_CONST); return TokenType.WHITE_SPACE; }
<START_INT_CONST> {WHITE_SPACE}+                            { yybegin(START_INT_CONST); return TokenType.WHITE_SPACE; }

<START_INT_CONST> {NEG_OPERATOR}                            { yybegin(NEG_NUM); return ROSMsgTypes.NEG_OPERATOR; }
<NEG_NUM,START_INT_CONST> {NUMBER}+                         { yybegin(END_LINE); return ROSMsgTypes.NUMBER;}

<START_CONST> {FIRST_CONST_STRING}{CONST_STRING}*           { yybegin(END_LINE); return ROSMsgTypes.STRING;}

<END_NAME> {WHITE_SPACE}+                                   { yybegin(END_NAME); return TokenType.WHITE_SPACE; }
<END_INT_NAME> {WHITE_SPACE}+                               { yybegin(END_INT_NAME); return TokenType.WHITE_SPACE; }

<YYINITIAL> {WHITE_SPACE}+                                  { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

{WHITE_SPACE}*{CRLF}                                        { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

.                                                           { return TokenType.BAD_CHARACTER; }
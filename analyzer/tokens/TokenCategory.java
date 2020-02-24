package tokens;

public enum TokenCategory {
    unknown, funDef, funRet, main, id, paramBeg, paramEnd, commaSep, typeInt,
    typeFloat, typeBool, typeChar, typeVoid, typeStr, beginScope, endScope,
    constInt, constFloat, colon, constBool, constChar, constStr,
    opAdd, opSub, opMult, opDiv, arrBegin, arrEnd, opPow, opMod, 
    opGreater, opLesser, opGreq, opLeq, opEquals, opNotEqual,
    opAnd, opOr, opNot, opConcat, opAttrib, EOF, procDef, constVar,
    semicolon, forLoop, whileLoop, condIf, condElseIf, condElse, scan, print
}
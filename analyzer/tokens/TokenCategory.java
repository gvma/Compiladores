package tokens;

public enum TokenCategory {
    unknown, funDef, funRet, main, id, paramBeg, paramEnd, commaSep, typeInt,
    typeFloat, typeBool, typeChar, typeVoid, typeStr, arrInt, arrFloat, arrBool, 
    arrChar, arrString, beginScope, endScope, constInt, constFloat, 
    constBool, constChar, constStr, opAdd, opSub, opMult, opDiv, arrBegin, arrEnd,
    opPow, opMod, opGreater, opLesser, opGreq, opLeq, opEquals, opNotEqual,
    opAnd, opOr, opNegative, opNot, opConcat, opAttrib, EOL, forLoop, loopSep,
    whileLoop, condIf, condElseIf, condElse, scan, print
}
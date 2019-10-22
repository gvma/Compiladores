package token;

//TODO define, using words, what's the meaning of each tokens listed down below to Thiago

public enum TokenCategory {
    unknown, funDef, funRet, funMain, paramBeg, paramEnd, typeInt, 
    typeFloat, typeBool, typeChar, typeStr, arrInt, arrFloat, arrBool, 
    arrChar, arrString, beginScope, endScope, constInt, constFloat, commaSep,
    constBool, constChar, constStr, constArr, opAdd, opSub, opMult, opDiv, 
    opPow, opMod, opGreater, opLesser, opGreq, opLeq, opEquals, opNotEqual,
    opAnd, opOr, opNegative, opNot, opConcat, opAttrib, EOL, forLoop, 
    whileLoop, condIf, condElseIf, condElse, postInc, preInc, postDec, preDec
}
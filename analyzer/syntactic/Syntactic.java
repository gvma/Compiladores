package syntactic;

import java.io.IOException;

import lexical.Lexical;
import tokens.Token;
import tokens.TokenCategory;

public class Syntactic {
	private Lexical lexicalAnalyzer;
	private Token currentToken;
	
	public Syntactic(String[] args) {
		for (String s : args) {
			try {
				lexicalAnalyzer = new Lexical(s);
				setNextToken(false);
				fS();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setNextToken(boolean isRequired) {
		if (lexicalAnalyzer.hasNextToken()) {
			//System.out.println("current token: " + currentToken);
			currentToken = lexicalAnalyzer.nextToken();
			//System.out.println("next token: " + currentToken);
		} else if (isRequired) {
			System.err.println("Unexpected EOF");
		}
	}

	public boolean checkCategory(boolean isTerminal, TokenCategory... categories) {
		for (TokenCategory category: categories) {
			if (currentToken.getTokenCategory() == category) {
				if (isTerminal) {
					setNextToken(true);
				}
				return true;
			}
		}
		return false;
	}

	public void printProduction(String left, String right) {
		String format = "%10s%s = %s";

		System.out.println(String.format(format, "", left, right));
	}

	private void unexpectedToken(String expected) {
		String position = String.format("[%04d, %04d]", currentToken.getLine(), currentToken.getColumn());
		sendError("Expected " + expected + " at " + position +  " but got " + currentToken);
	}

	public void sendError(String message) {
		System.err.println("Error: " + message);
		System.exit(0);
	}

	public void fS() {
		if (checkCategory(false, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr, TokenCategory.constVar)) {
			printProduction("S", "DeclId S");
			fDeclId();
			fS();
		} else if (checkCategory(false, TokenCategory.funDef)) {
			printProduction("S", "FunDecl S");
			fFunDecl();
			fS();
		} else if (checkCategory(false, TokenCategory.procDef)) {
			printProduction("S", "ProcDecl S");
			fProcDecl();
			fS();
		} else {
			printProduction("S", "ε");
		}
	}

	public void fDeclId() {
		if (checkCategory(false, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr)) {
			printProduction("DeclId", "Type LId");
			fType();
			fLId();
		} else if (checkCategory(true, TokenCategory.constVar)) {
			printProduction("DeclId", "'const' Type LId");
			fType();
			fLId();
		} else {
			unexpectedToken("int, float, bool, char, string or const");
		}
	}

	public void fFunDecl() {
		if (checkCategory(true, TokenCategory.funDef)) {
			printProduction("FunDecl", "'funDef' Type FunName Param Body");
			fType();
			fFunName();
			fParam();
			fBody();
		} else {
			unexpectedToken("fun");
		}
	}

	public void fFunName() {
		if (checkCategory(true, TokenCategory.id)) {
			printProduction("FunName", "id");
		} else if (checkCategory(true, TokenCategory.main)) {
			printProduction("FunName", "main");
		} else {
			unexpectedToken("id or main");
		}
	}

	public void fFunCall() {
		if (checkCategory(true, TokenCategory.paramBeg)) {
			printProduction("FunCall", "'(' LParamCall ')'");
			fLParamCall();
			checkCategory(true, TokenCategory.paramEnd);
		} else {
			unexpectedToken("'('");
		}
	}

	public void fReturn() {
		if (checkCategory(true, TokenCategory.funRet)) {
			printProduction("Return", "'funRet' Ec");
			fEc();
		} else {
			unexpectedToken("return");
		}
	}

	public void fProcDecl() {
		if (checkCategory(true, TokenCategory.procDef)) {
			printProduction("ProcDecl", "'procDef' 'id' Param Body");
			if (checkCategory(true, TokenCategory.id)) {
				fParam();
				fBody();
			}
		} else {
			unexpectedToken("proc");
		}
	}

	public void fParam() {
		if (checkCategory(true, TokenCategory.paramBeg)) {
			printProduction("Param", "'(' LParam ')'");
			fLParam();
			checkCategory(true, TokenCategory.paramEnd);
		} else {
			unexpectedToken("'('");
		}
	}

	public void fLParam() {
		if (checkCategory(false, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar,TokenCategory.typeStr)) {
			printProduction("LParam", "Type 'id' ArrayOpt LParamr");
			fType();
			if (checkCategory(true, TokenCategory.id)) {
				fArrayOpt();
				fLParamr();
			}
		} else {
			printProduction("LParam", "ε");
		}
	}

	public void fLParamr() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("LParamr", "',' Type 'id' ArrayOpt LParamr");
			fType();
			if (checkCategory(true, TokenCategory.id)) {
				fArrayOpt();
				fLParamr();
			}
		} else {
			printProduction("LParamr", "ε");
		}
	}

	public void fLParamCall() {
		if (checkCategory(false, TokenCategory.constStr, TokenCategory.constChar, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.constBool,
				TokenCategory.opNot, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("LParamCall", "Ec LParamCallr");
			fEc();
			fLParamCallr();
		} else {
			unexpectedToken("const string, const char, const int, const float, const bool, '!', '-', '(', id");
		}
	}

	public void fLParamCallr() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("LParamCallr", "',' Ec LParamCallr");
			fEc();
			fLParamCallr();
		} else {
			printProduction("LParamCallr", "ε");
		}
	}

	public void fBody() {
		if (checkCategory(true, TokenCategory.beginScope)) {
			printProduction("Body", "'{' BodyPart '}'");
			fBodyPart();
			checkCategory(true, TokenCategory.endScope);
		} else {
			unexpectedToken("'{'");
		}
	}

	public void fBodyPart() {
		if (checkCategory(false, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr, TokenCategory.constVar)) {
			printProduction("BodyPart", "DeclId BodyPart");
			fDeclId();
			fBodyPart();
		} else if (checkCategory(false, TokenCategory.id)) {
			printProduction("BodyPart", "LId BodyPart");
			fLId();
			fBodyPart();
		} else if (checkCategory(false, TokenCategory.print, TokenCategory.scan, TokenCategory.whileLoop, TokenCategory.forLoop, TokenCategory.condIf)) {
			printProduction("BodyPart", "Command BodyPart");
			fCommand();
			fBodyPart();
		} else if (checkCategory(false, TokenCategory.funRet)) {
			printProduction("BodyPart", "Return ';'");
			fReturn();
			checkCategory(true, TokenCategory.semicolon);
		} else {
			printProduction("BodyPart", "ε");
		}
	}

	public void fType() {
		if (checkCategory(true, TokenCategory.typeInt)) {
			printProduction("Type", "'int'");
		} else if (checkCategory(true, TokenCategory.typeFloat)) {
			printProduction("Type", "'float'");
		} else if (checkCategory(true, TokenCategory.typeBool)) {
			printProduction("Type", "'bool'");
		} else if (checkCategory(true, TokenCategory.typeChar)) {
			printProduction("Type", "'char'");
		} else if (checkCategory(true, TokenCategory.typeStr)) {
			printProduction("Type", "'string'");
		} else {
			unexpectedToken("int, float, bool, char or string");
		}
	}

	public void fId() {
		if (checkCategory(true, TokenCategory.id)) {
			printProduction("Id", "'id' IdOpt");
			fIdOpt();
		} else {
			unexpectedToken("id");
		}
	}

	public void fLId() {
		if (checkCategory(true, TokenCategory.id)) {
			printProduction("LId", "'id' IdAttr LIdr ';'");
			fIdAttr();
			fLIdr();
			checkCategory(true, TokenCategory.semicolon);
		} else {
			unexpectedToken("id");
		}
	}

	public void fLIdr() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("LIdr", "',' IdAttr LIdr");
			fIdAttr();
			fLIdr();
		} else {
			printProduction("LIdr", "ε");
		}
	}

	public void fIdAttr() {
		if (checkCategory(false, TokenCategory.arrBegin, TokenCategory.opAttrib, TokenCategory.commaSep, TokenCategory.semicolon)) {
			printProduction("IdAttr", "ArrayOpt AttrOpt");
			fArrayOpt();
			fAttrOpt();
		} else if (checkCategory(false, TokenCategory.paramBeg)) {
			printProduction("IdAttr", "FunCall");
			fFunCall();
		} else {
			unexpectedToken("'[', '=', ',' or ';'");
		}
	}

	public void fIdOpt() {
		if (checkCategory(false, TokenCategory.paramBeg)) {
			printProduction("IdOpt", "FunCall");
			fFunCall();
		} else {
			printProduction("IdOpt", "ArrayOpt");
			fArrayOpt();
		}
	}

	public void fArrayOpt() {
		if (checkCategory(true, TokenCategory.arrBegin)) {
			printProduction("ArrayOpt", "'[' Ea ']'");
			fEa();
			checkCategory(true, TokenCategory.arrEnd);
		} else {
			printProduction("ArrayOpt", "ε");
		}
	}

	public void fAttrOpt() {
		if (checkCategory(true, TokenCategory.opAttrib)) {
			printProduction("AttrOpt", "'opAttrib' Ec");
			fEc();
		} else {
			printProduction("AttrOpt", "ε");
		}
	}

	public void fPrintParam() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("PrintParam", "',' LParamCall");
			fLParamCall();
		} else {
			printProduction("PrintParam", "ε");
		}
	}

	public void fCommand() {
		if (checkCategory(true, TokenCategory.print)) {
			printProduction("Command", "'print' '(' Ec PrintParam ')' ';'");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fEc();
				fPrintParam();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					checkCategory(true, TokenCategory.semicolon);
				}
			}
		} else if (checkCategory(true, TokenCategory.scan)) {
			printProduction("Command", "'scan' '(' LParamCall ')' ';'");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fLParamCall();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					checkCategory(true, TokenCategory.semicolon);
				}
			}
		} else if (checkCategory(true, TokenCategory.whileLoop)) {
			printProduction("Command", "'whileLoop' '(' Eb ')' Body");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fEb();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					fBody();
				}
			}
		} else if (checkCategory(true, TokenCategory.forLoop)) {
			printProduction("Command", "'forLoop' '(' 'typeInt' 'id' ':' '(' Ec ',' Ec ',' Ec ')' ')' Body");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				if (checkCategory(true, TokenCategory.typeInt)) {
					if (checkCategory(true, TokenCategory.id)) {
						if (checkCategory(true, TokenCategory.colon)) {
							if (checkCategory(true, TokenCategory.paramBeg)) {
								fEc();
								if (checkCategory(true, TokenCategory.commaSep)) {
									fEc();
									if (checkCategory(true, TokenCategory.commaSep)) {
										fEc();
										if (checkCategory(true, TokenCategory.paramEnd)) {
											if (checkCategory(true, TokenCategory.paramEnd)) {
												fBody();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else if (checkCategory(true, TokenCategory.condIf)) {
			printProduction("Command", "'condIf '(' Eb ')' Body Ifr'");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fEb();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					fBody();
					fIfr();
				}
			}
		} else {
			unexpectedToken("print, scan, while, for or if");
		}
	}

	public void fIfr() {
		if (checkCategory(true, TokenCategory.condElse)) {
			printProduction("Ifr", "'condElse' Body");
			fBody();
		} else if (checkCategory(true, TokenCategory.condElseIf)) {
			printProduction("Ifr", "'condElseIf' '(' Eb ')' Body Ifr");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fEb();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					fBody();
					fIfr();
				}
			}
		} else {
			printProduction("Ifr", "ε");
		}
	}

	public void fEc() {
		if (checkCategory(false, TokenCategory.constStr, TokenCategory.constChar, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.constBool,
				TokenCategory.opNot, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Ec", "Fc Ecr");
			fFc();
			fEcr();
		} else {
			unexpectedToken("const string, const char, const int, const float, const bool, '!', '-', '(', id");
		}
	}

	public void fEcr() {
		if (checkCategory(true, TokenCategory.opConcat)) {
			printProduction("Ecr", "'opConcat' Fc Ecr");
			fFc();
			fEcr();
		} else {
			printProduction("Ecr", "ε");
		}
	}

	public void fFc() {
		if (checkCategory(true, TokenCategory.constStr)) {
			printProduction("Fc", "'constStr'");
		} else if (checkCategory(true, TokenCategory.constChar)) {
			printProduction("Fc", "'constChar'");
		} else if (checkCategory(false, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.constBool,
				TokenCategory.opNot, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Fc", "Eb");
			fEb();
		} else {
			unexpectedToken("const string, const char, const int, const float, const bool, '!', '-', '(', id");
		}
	}

	public void fEb() {
		if (checkCategory(false, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.constBool,
				TokenCategory.opNot, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Eb", "Tb Ebr");
			fTb();
			fEbr();
		} else {
			unexpectedToken("const int, const float, const bool, '!', '-', '(', id");
		}
	}

	public void fEbr() {
		if (checkCategory(true, TokenCategory.opOr)) {
			printProduction("Ebr", "'opOr' Tb Ebr");
			fTb();
			fEbr();
		} else {
			printProduction("Ebr", "ε");
		}
	}

	public void fTb() {
		if (checkCategory(false, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.constBool,
				TokenCategory.opNot, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Tb", "Fb Tbr");
			fFb();
			fTbr();
		} else {
			unexpectedToken("const int, const float, const bool, '!', '-', '(', id");
		}
	}

	public void fTbr() {
		if (checkCategory(true, TokenCategory.opAnd)) {
			printProduction("Tbr", "'opAnd' Fb Tbr");
			fFb();
			fTbr();
		} else {
			printProduction("Tbr", "ε");
		}
	}

	public void fFb() {
		if (checkCategory(true, TokenCategory.opNot)) {
			printProduction("Fb", "'opNot' Fb");
			fFb();
		} else if (checkCategory(true, TokenCategory.constBool)) {
			printProduction("Fb", "'cteBool'");
		} else if (checkCategory(false, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Fb", "Ra Fbr");
			fRa();
			fFbr();
		} else {
			unexpectedToken("const int, const float, const bool, '!', '-', '(', id");
		}
	}

	public void fFbr() {
		if (checkCategory(true, TokenCategory.opGreater)) {
			printProduction("Fbr", "'opGreater' Ra Fbr");
			fRa();
			fFbr();
		} else if (checkCategory(true, TokenCategory.opLesser)) {
			printProduction("Fbr", "'opLesser' Ra Fbr");
			fRa();
			fFbr();
		} else if (checkCategory(true, TokenCategory.opGreq)) {
			printProduction("Fbr", "'opGreq' Ra Fbr");
			fRa();
			fFbr();
		} else if (checkCategory(true, TokenCategory.opLeq)) {
			printProduction("Fbr", "'opLeq' Ra Fbr");
			fRa();
			fFbr();
		} else {
			printProduction("Fbr", "ε");
		}
	}

	public void fRa() {
		if (checkCategory(false, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Ra", "Ea Rar");
			fEa();
			fRar();
		} else {
			unexpectedToken("const int, const float, '-', '(', id");
		}
	}

	public void fRar() {
		if (checkCategory(true, TokenCategory.opEquals)) {
			printProduction("Rar", "'opEquals' Ea Rar");
			fEa();
			fRar();
		} else if (checkCategory(true, TokenCategory.opNotEqual)) {
			printProduction("Rar", "'opNotEqual' Ea Rar");
			fEa();
			fRar();
		} else {
			printProduction("Rar", "ε");
		}
	}

	public void fEa() {
		if (checkCategory(false, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Ea", "Ta Ear");
			fTa();
			fEar();
		} else {
			unexpectedToken("const int, const float, '-', '(', id");
		}
	}

	public void fEar() {
		if (checkCategory(true, TokenCategory.opAdd)) {
			printProduction("Ear", "'opAdd' Ta Ear");
			fTa();
			fEar();
		} else if (checkCategory(true, TokenCategory.opSub)) {
			printProduction("Ear", "'opSub' Ta Ear");
			fTa();
			fEar();
		} else {
			printProduction("Ear", "ε");
		}
	}

	public void fTa() {
		if (checkCategory(false, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Ta", "Pa Tar");
			fPa();
			fTar();
		} else {
			unexpectedToken("const int, const float, '-', '(', id");
		}
	}

	public void fTar() {
		if (checkCategory(true, TokenCategory.opMult)) {
			printProduction("Tar", "'opMult' Pa Tar");
			fPa();
			fTar();
		} else if (checkCategory(true, TokenCategory.opDiv)) {
			printProduction("Tar", "'opDiv' Pa Tar");
			fPa();
			fTar();
		} else {
			printProduction("Tar", "ε");
		}
	}

	public void fPa() {
		if (checkCategory(false, TokenCategory.constInt, TokenCategory.constFloat, TokenCategory.opSub, TokenCategory.paramBeg, TokenCategory.id)) {
			printProduction("Pa", "Fa Par");
			fFa();
			fPar();
		} else {
			unexpectedToken("const int, const float, '-','(', id");
		}
	}

	public void fPar() {
		if (checkCategory(true, TokenCategory.opPow)) {
			printProduction("Par", "'opPow' Fa Par");
			fFa();
			fPar();
		} else {
			printProduction("Par", "ε");
		}
	}

	public void fFa() {
		if (checkCategory(true, TokenCategory.paramBeg)) {
			printProduction("Fa", "'(' Ec ')'");
			fEc();
			checkCategory(true, TokenCategory.paramEnd);
		} else if (checkCategory(true, TokenCategory.opSub)) {
			printProduction("Fa", "'opSub' Fc");
			fFc();
		} else if (checkCategory(true, TokenCategory.id)) {
			printProduction("Fa", "Id");
			fId();
		} else if (checkCategory(true, TokenCategory.constInt)) {
			printProduction("Fa", "'constInt'");
		} else if (checkCategory(true, TokenCategory.constFloat)) {
			printProduction("Fa", "'constFloat'");
		} else {
			unexpectedToken("const int, const float, '-', '(', id");
		}
	}
}
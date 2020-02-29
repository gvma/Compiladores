package syntactic;

import java.io.IOException;

import lexical.Lexical;
import tokens.Token;
import tokens.TokenCategory;

public class Syntactic {
	private Lexical lexicalAnalyzer;
	private Token currentToken;
	private int scopeCounter = 0;
	private String epsilon = "ε";
	
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
			currentToken = lexicalAnalyzer.nextToken();
		} else if (isRequired) {
			if (scopeCounter != 1 || currentToken.getTokenCategory() != TokenCategory.endScope) {	
				System.out.println(scopeCounter);
				sendError("Unexpected EOF");
			}
		}
	}

	public boolean checkCategory(boolean isTerminal, TokenCategory... categories) {
		for (TokenCategory category: categories) {
			if (currentToken.getTokenCategory() == category) {
				if (isTerminal) {
					System.out.println(currentToken);
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
		if (checkCategory(false, TokenCategory.constVar, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr)) {
			printProduction("S", "DeclId S");
			fDeclId();
			fS();
		} else if (checkCategory(false, TokenCategory.funDef)) {
			printProduction("S", "FunDecl S");
			fFunDecl();
			fS();
		} else if (checkCategory(false, TokenCategory.procDef)) {
			printProduction("S", "ProcDef S");
			fProcDecl();
			fS();
		} else {
			printProduction("S", epsilon);
		}
	}

	public void fDeclId() {
		if (checkCategory(false, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr)) {
			printProduction("DeclId", "Type LId ';'");
			fType();
			fLId();
			if (!checkCategory(true, TokenCategory.semicolon)){
				unexpectedToken(";");
			}
		} else if (checkCategory(true, TokenCategory.constVar)) {
			printProduction("DeclId", "'const' Type LId ';'");
			fType();
			fLId();
			checkCategory(true, TokenCategory.semicolon);
		} else {
			unexpectedToken("int, float, bool, char string or const");
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
	
	
	public void fLId() {
		if (checkCategory(false, TokenCategory.id)) {
			printProduction("LId", "Id AttrOpt LIdr");
			fId();
			fAttrOpt();
			fLIdr();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fLIdr() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("LIdr", "',' Id AttrOpt LIdr");
			fId();
			fAttrOpt();
			fLIdr();
		} else {
			printProduction("LIdr", epsilon);
		}
	}
	
	public void fId() {
		if (checkCategory(true, TokenCategory.id)) {
			printProduction("Id", "'id' ArrayOpt");
			fArrayOpt();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fAttrOpt() {
		if (checkCategory(true, TokenCategory.opAttrib)) {
			printProduction("AttrOpt", "'=' Ec");
			fEc();
		} else {
			printProduction("AttrOpt", epsilon);
		}
	}
	
	public void fFunDecl() {
		if (checkCategory(true, TokenCategory.funDef)) {
			printProduction("FunDecl", "'fun' Type FunName '(' LParamDecl ')' Body");
			fType();
			fFunName();
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fLParamDecl();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					fBody();
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else {
			unexpectedToken("fun");
		}
	}
	
	public void fFunName() {
		if (checkCategory(true, TokenCategory.id)) {
			printProduction("FunName", "'id'");
		} else if (checkCategory(true, TokenCategory.main)) {
			printProduction("FunName", "'main'");
		} else {
			unexpectedToken("id or main");
		}
	}
	
	public void fLParamCall() {
		if (checkCategory(false, TokenCategory.id, TokenCategory.paramBeg, TokenCategory.opSub, TokenCategory.constBool, TokenCategory.constChar, TokenCategory.constFloat, TokenCategory.constInt, TokenCategory.constStr)) {
			printProduction("LParamCall", "Ec LParamCallr");
			fEc();
			fLParamCallr();
		} else {
			printProduction("LParamCall", epsilon);
		}
	}
	
	public void fLParamCallr() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("LParamCallr", "',' Ec LParamCallr");
			fEc();
			fLParamCallr();
		} else {
			printProduction("LParamCallr", epsilon);
		}
	}
	
	public void fLParamDecl() {
		if (checkCategory(false, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeFloat, TokenCategory.typeInt, TokenCategory.typeStr)) {			
			printProduction("LParamDecl", "Type 'id' ArrayOpt LParamDeclr");
			fType();
			if (checkCategory(true, TokenCategory.id)) {
				fArrayOpt();
				fLParamDeclr();
			} else {
				unexpectedToken("id");
			}
		} else {
			printProduction("LParamDecl", epsilon);
		}
	}
	
	public void fLParamDeclr() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("LParamDeclr", "',' Type 'id' ArrayOpt LParamDeclr");
			fType();
			if (checkCategory(true, TokenCategory.id)) {
				fArrayOpt();
				fLParamDeclr();
			} else {
				unexpectedToken("id");
			}
		}
	}
	
	public void fArrayOpt() {
		if (checkCategory(true, TokenCategory.arrBegin)) {
			printProduction("ArrayOpt", "'[' Ea ']'");
			fEa();
			if (!checkCategory(true, TokenCategory.arrEnd)) {
				unexpectedToken("]");
			}
		} else {
			printProduction("ArrayOpt", epsilon);
		}
	}
	
	public void fProcDecl() {
		if (checkCategory(true, TokenCategory.procDef)) {
			printProduction("ProcDecl", "'proc' FunName '(' LParamDecl ')' Body");
			fFunName();
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fLParamDecl();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					fBody();
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else {
			unexpectedToken("proc");
		}
	}
	
	public void fBody() {
		if (checkCategory(true, TokenCategory.beginScope)) {
			++scopeCounter;
			printProduction("Body", "'{' BodyPart '}'");
			fBodyPart();
			if (!checkCategory(true, TokenCategory.endScope)) {
				unexpectedToken("}");
			} else {
				--scopeCounter;
			}
		} else {
			unexpectedToken("{");
		}
	}
	
	public void fBodyPart() {
		if (checkCategory(false, TokenCategory.constVar, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr)) {
			printProduction("BodyPart", "DeclId BodyPart");
			fDeclId();
			fBodyPart();
		} else if (checkCategory(false, TokenCategory.print, TokenCategory.scan, TokenCategory.whileLoop, TokenCategory.forLoop, TokenCategory.condIf)) {
			printProduction("BodyPart", "Command BodyPart");
			fCommand();
			fBodyPart();
		} else if (checkCategory(false, TokenCategory.id)) {
			printProduction("BodyPart", "BodyPartr ';' BodyPart");
			fBodyPartr();
			if (!checkCategory(true, TokenCategory.semicolon)) {
				unexpectedToken(";");
			}
			fBodyPart();
		} else if (checkCategory(true, TokenCategory.funRet)) {
			printProduction("BodyPart", "'return' Return ';'");
			fReturn();
			if (!checkCategory(true, TokenCategory.semicolon)) {
				unexpectedToken(";");
			}
		} else {
			printProduction("BodyPart", epsilon);
		}
	}
	
	public void fBodyPartr() {
		if (checkCategory(true, TokenCategory.id)) {
			printProduction("BodyPartr", "'id' ParamAttr");
			fParamAttr();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fParamAttr() {
		if (checkCategory(true, TokenCategory.paramBeg)) {
			printProduction("ParamAttrib", "'(' LParamCall ')'");
			fLParamCall();
			if (!checkCategory(true, TokenCategory.paramEnd)) {
				unexpectedToken(")");
			}
		} else if (checkCategory(true, TokenCategory.opAttrib)) {
			printProduction("ParamAttrib", "'=' Ec LAttr");
			fEc();
			fLAttr();
		} else if (checkCategory(true, TokenCategory.arrBegin)) {
			printProduction("ParamAttrib", "'[' Ea ']' '=' Ec LAttr");
			fEa();
			if (checkCategory(true, TokenCategory.arrEnd)) {
				if (checkCategory(true, TokenCategory.opAttrib)) {
					fEc();
					fLAttr();
				} else {
					unexpectedToken("=");
				}
			} else {
				unexpectedToken("]");
			}
		} else {
			unexpectedToken("(, = or [");
		}
	}
	
	public void fLAttr() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("LAttr", "',' Id '=' Ec LAttr");
			fId();
			if (checkCategory(true, TokenCategory.opAttrib)) {
				fEc();
				fLAttr();
			} else {
				unexpectedToken("=");
			}
		} else {
			printProduction("LAttr", epsilon);
		}
	}
	
	public void fReturn() {
		if (checkCategory(false, TokenCategory.paramBeg, TokenCategory.opSub, TokenCategory.constInt, TokenCategory.constBool, TokenCategory.constChar, TokenCategory.constFloat, TokenCategory.constStr, TokenCategory.id)) {
			printProduction("Return", "Ec");
			fEc();
		} else {
			printProduction("Return", epsilon);
		}
	}
	
	public void fCommand() {
		if (checkCategory(true, TokenCategory.print)) {
			printProduction("Command", "'print' '(' 'constStr' PrintLParam ')' ';'");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				if (checkCategory(true, TokenCategory.constStr)) {
					fPrintLParam();
					if (checkCategory(true, TokenCategory.paramEnd)) {
						if (!checkCategory(true, TokenCategory.semicolon)) {
							unexpectedToken(";");
						}
					} else {
						unexpectedToken(")");
					}
				} else {
					unexpectedToken("constStr");
				}
			} else {
				unexpectedToken("(");
			}
		} else if (checkCategory(true, TokenCategory.scan)) {
			printProduction("Command", "'scan' '(' ScanLParam ')' ';'");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fScanLParam();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					if (!checkCategory(true, TokenCategory.semicolon)) {
						unexpectedToken(";");
					}
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else if (checkCategory(true, TokenCategory.whileLoop)) {
			printProduction("Command", "'whileLoop' '(' Eb ')' Body");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fEb();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					fBody();
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else if (checkCategory(true, TokenCategory.forLoop)) {
			printProduction("Command", "'forLoop' ForParams");
			fForParams();
		} else if (checkCategory(true, TokenCategory.condIf)) {
			printProduction("Command", "'condIf' '(' Eb ')' Body Ifr");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fEb();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					fBody();
					fIfr();
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else {
			unexpectedToken("print, scan, while, for or if");
		}
	}
	
	public void fPrintLParam() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("PrintLParam", "',' Ec PrintLParam");
			fEc();
			fPrintLParam();
		} else {
			printProduction("PrintLParam", epsilon);
		}
	}
	
	public void fScanLParam() {
		if (checkCategory(true, TokenCategory.id)) {
			printProduction("ScanLParam", "'id' ArrayOpt ScanLParamr");
			fArrayOpt();
			fScanLParamr();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fScanLParamr() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("ScanLParamr", "',' 'id' ArrayOpt ScanLParamr");
			if (checkCategory(true, TokenCategory.id)) {
				fArrayOpt();
				fScanLParamr();
			} else {
				unexpectedToken("id");
			}
		} else {
			printProduction("ScanLParamr", epsilon);
		}
	}
	
	public void fForParams() {
		if (checkCategory(true, TokenCategory.paramBeg)) {
			printProduction("ForParams", "'(' 'typeInt' 'id' ':' '(' Ea ',' Ea ForStep ')' ')' Body");
			if (checkCategory(true, TokenCategory.typeInt)) {
				if (checkCategory(true, TokenCategory.id)) {
					if (checkCategory(true, TokenCategory.colon)) {
						if (checkCategory(true, TokenCategory.paramBeg)) {
							fEa();
							if (checkCategory(true, TokenCategory.commaSep)) {
								fEa();
								fForStep();
								if (checkCategory(true, TokenCategory.paramEnd)) {
									if (checkCategory(true, TokenCategory.paramEnd)) {
										fBody();
									} else {
										unexpectedToken(")");
									}
								}  else {
									unexpectedToken(")");
								}
							}  else {
								unexpectedToken(",");
							}
						}  else {
							unexpectedToken("(");
						}
					}  else {
						unexpectedToken(":");
					}
				}  else {
					unexpectedToken("id");
				}
			}  else {
				unexpectedToken("int");
			}
		}  else {
			unexpectedToken("(");
		}
	}
	
	public void fForStep() {
		if (checkCategory(true, TokenCategory.commaSep)) {
			printProduction("ForStep", "',' Ea");
			fEa();
		} else {
			printProduction("ForStep", epsilon);
		}
	}
	
	public void fIfr() {
		if (checkCategory(true, TokenCategory.condElseIf)) {
			printProduction("Ifr", "'condElseIf' '(' Eb ')' Body Ifr");
			if (checkCategory(true, TokenCategory.paramBeg)) {
				fEb();
				if (checkCategory(true, TokenCategory.paramEnd)) {
					fBody();
					fIfr();
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else if (checkCategory(true, TokenCategory.condElse)) {
			printProduction("Ifr", "'condElse' Body");
			fBody();
		} else {
			printProduction("Ifr", epsilon);
		}
	}
	
	public void fEc() {
		printProduction("Ec", "Fc Ecr");
		fEb();
		fEcr();
	}
	
	public void fEcr() {
		if (checkCategory(true, TokenCategory.opConcat)) {
			printProduction("Ecr", "'opConcat' Fc Ecr");
			fEb();
			fEcr();
		} else {
			printProduction("Ecr", epsilon);
		}
	}
	
	public void fEb() {
		printProduction("Eb", "Tb Ebr");
		fTb();
		fEbr();
	}
	
	public void fEbr() {
		if (checkCategory(true, TokenCategory.opOr)) {
			printProduction("Ebr", "'opOr' Tb Ebr");
			fTb();
			fEbr();
		} else {
			printProduction("Ebr", epsilon);
		}
	}
	
	public void fTb() {
		printProduction("Tb", "Fb Tbr");
		fFb();
		fTbr();
	}
	
	public void fTbr() {
		if (checkCategory(true, TokenCategory.opAnd)) {
			printProduction("Tbr", "'opAnd' Fb Tbr");
			fFb();
			fTbr();
		} else {
			printProduction("Tbr", epsilon);
		}
	}
	
	public void fFb() {
		if (checkCategory(true, TokenCategory.opNot)) {
			printProduction("Fb", "'opNot' Fb");
			fFb();
		} else if (checkCategory(false, TokenCategory.paramBeg, TokenCategory.opSub, TokenCategory.constInt, TokenCategory.constBool, TokenCategory.constChar, TokenCategory.constFloat, TokenCategory.constStr, TokenCategory.id)) {
			printProduction("Fb", "Ra Fbr");
			fRa();
			fFbr();
		} else {
			unexpectedToken("!, (, -, constInt, constBool, constChar, constFloat, constStr or id");
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
			printProduction("Fbr", epsilon);
		}
	}
	
	public void fRa() {
		printProduction("Ra", "Ea Rar");
		fEa();
		fRar();
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
			printProduction("Rar", epsilon);
		}
	}
	
	public void fEa() {
		printProduction("Ea", "Ta Ear");
		fTa();
		fEar();
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
			printProduction("Ear", epsilon);
		}
	}
	
	public void fTa() {
		printProduction("Ta", "Pa Tar");
		fPa();
		fTar();
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
			printProduction("Tar", epsilon);
		}
	}
	
	public void fPa() {
		printProduction("Pa", "Fa Par");
		fFa();
		fPar();
	}
	
	public void fPar() {
		if (checkCategory(true, TokenCategory.opPow)) {
			printProduction("Par", "'opPow' Fa Par");
			fFa();
			fPar();
		} else {
			printProduction("Par", epsilon);
		}
	}
	
	public void fFa() {
		if (checkCategory(true, TokenCategory.paramBeg)) {
			printProduction("Fa", "'(' Ec ')'");
			fEc();
			if (!checkCategory(true, TokenCategory.paramEnd)) {
				unexpectedToken(")");
			}
		} else if (checkCategory(true, TokenCategory.opSub)) {
			printProduction("Fa", "'opSub' Fa");
			fFa();
		} else if (checkCategory(false, TokenCategory.id)) {
			fIdOrFunCall();
		} else if (checkCategory(true, TokenCategory.constBool)) {
			printProduction("Fa", "'constBool'");
		} else if (checkCategory(true, TokenCategory.constChar)) {
			printProduction("Fa", "'constChar'");
		} else if (checkCategory(true, TokenCategory.constFloat)) {
			printProduction("Fa", "'constFloat'");
		} else if (checkCategory(true, TokenCategory.constInt)) {
			printProduction("Fa", "'constInt'");
		} else if (checkCategory(true, TokenCategory.constStr)) {
			printProduction("Fa", "'constStr'");
		} else {
			unexpectedToken("constBool, constChar, constFloat, constInt, constStr");
		}
	}
	
	public void fIdOrFunCall() {
		if (checkCategory(true, TokenCategory.id)) {
			printProduction("IdOrFunCall", "'id' IdOrFunCallr");
			fIdOrFunCallr();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fIdOrFunCallr() {
		if (checkCategory(true, TokenCategory.paramBeg)) {
			printProduction("IdOrFunCallr", "'(' LParamCall ')'");
			fLParamCall();
			if (!checkCategory(true, TokenCategory.paramEnd)) {
				unexpectedToken(")");
			}
		} else if (checkCategory(true, TokenCategory.arrBegin)) {
			printProduction("IdOrFunCallr", "'[' Ea ']'");
			fEa();
			if (!checkCategory(true, TokenCategory.arrEnd)) {
				unexpectedToken("]");
			}
		} else {
			printProduction("IdOrFunCallr", epsilon);
		}
	}
}
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

	public boolean checkCategory(TokenCategory... categories) {
		for (TokenCategory category: categories) {
			if (currentToken.getTokenCategory() == category) {
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
		System.exit(1);
	}

	public void fS() {
		if (checkCategory(TokenCategory.constVar, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr)) {
			printProduction("S", "DeclId S");
			fDeclId();
			fS();
		} else if (checkCategory(TokenCategory.funDef)) {
			printProduction("S", "FunDecl S");
			fFunDecl();
			fS();
		} else if (checkCategory(TokenCategory.procDef)) {
			printProduction("S", "ProcDecl S");
			fProcDecl();
			fS();
		} else {
			printProduction("S", epsilon);
		}
	}

	public void fDeclId() {
		if (checkCategory(TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr)) {
			printProduction("DeclId", "Type LId ';'");
			fType();
			fLId();
			if (!checkCategory(TokenCategory.semicolon)){
				unexpectedToken(";");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
			}
		} else if (checkCategory(TokenCategory.constVar)) {
			printProduction("DeclId", "'const' Type LId ';'");
			System.out.println(currentToken);
			setNextToken(true);
			fType();
			fLId();
			if (checkCategory(TokenCategory.semicolon)) {
				System.out.println(currentToken);
				setNextToken(true);
			}
		} else {
			unexpectedToken("int, float, bool, char string or const");
		}
	}
	
	public void fType() {
		if (checkCategory(TokenCategory.typeInt)) {
			printProduction("Type", "'int'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.typeFloat)) {
			printProduction("Type", "'float'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.typeBool)) {
			printProduction("Type", "'bool'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.typeChar)) {
			printProduction("Type", "'char'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.typeStr)) {
			printProduction("Type", "'string'");
			System.out.println(currentToken);
			setNextToken(true);
		} else {
			unexpectedToken("int, float, bool, char or string");
		}
	}
	
	
	public void fLId() {
		if (checkCategory(TokenCategory.id)) {
			printProduction("LId", "Id AttrOpt LIdr");
			fId();
			fAttrOpt();
			fLIdr();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fLIdr() {
		if (checkCategory(TokenCategory.commaSep)) {
			printProduction("LIdr", "',' Id AttrOpt LIdr");
			System.out.println(currentToken);
			setNextToken(true);
			fId();
			fAttrOpt();
			fLIdr();
		} else {
			printProduction("LIdr", epsilon);
		}
	}
	
	public void fId() {
		if (checkCategory(TokenCategory.id)) {
			printProduction("Id", "'id' ArrayOpt");
			System.out.println(currentToken);
			setNextToken(true);
			fArrayOpt();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fAttrOpt() {
		if (checkCategory(TokenCategory.opAttrib)) {
			printProduction("AttrOpt", "'=' Ec");
			System.out.println(currentToken);
			setNextToken(true);
			fEc();
		} else {
			printProduction("AttrOpt", epsilon);
		}
	}
	
	public void fFunDecl() {
		if (checkCategory(TokenCategory.funDef)) {
			printProduction("FunDecl", "'fun' Type FunName '(' LParamDecl ')' Body");
			System.out.println(currentToken);
			setNextToken(true);
			fType();
			fFunName();
			if (checkCategory(TokenCategory.paramBeg)) {
				fLParamDecl();
				System.out.println(currentToken);
				setNextToken(true);
				if (checkCategory(TokenCategory.paramEnd)) {
					System.out.println(currentToken);
					setNextToken(true);
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
		if (checkCategory(TokenCategory.id)) {
			printProduction("FunName", "'id'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.main)) {
			printProduction("FunName", "'main'");
			System.out.println(currentToken);
			setNextToken(true);
		} else {
			unexpectedToken("id or main");
		}
	}
	
	public void fLParamCall() {
		if (checkCategory(TokenCategory.id, TokenCategory.paramBeg, TokenCategory.opSub, TokenCategory.constBool, TokenCategory.constChar, TokenCategory.constFloat, TokenCategory.constInt, TokenCategory.constStr)) {
			printProduction("LParamCall", "Ec LParamCallr");
			fEc();
			fLParamCallr();
		} else {
			printProduction("LParamCall", epsilon);
		}
	}
	
	public void fLParamCallr() {
		if (checkCategory(TokenCategory.commaSep)) {
			printProduction("LParamCallr", "',' Ec LParamCallr");
			System.out.println(currentToken);
			setNextToken(true);
			fEc();
			fLParamCallr();
		} else {
			printProduction("LParamCallr", epsilon);
		}
	}
	
	public void fLParamDecl() {
		if (checkCategory(TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeFloat, TokenCategory.typeInt, TokenCategory.typeStr)) {			
			printProduction("LParamDecl", "Type 'id' ArrayOpt LParamDeclr");
			fType();
			if (checkCategory(TokenCategory.id)) {
				System.out.println(currentToken);
				setNextToken(true);
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
		if (checkCategory(TokenCategory.commaSep)) {
			printProduction("LParamDeclr", "',' Type 'id' ArrayOpt LParamDeclr");
			System.out.println(currentToken);
			setNextToken(true);
			fType();
			if (checkCategory(TokenCategory.id)) {
				System.out.println(currentToken);
				setNextToken(true);
				fArrayOpt();
				fLParamDeclr();
			} else {
				unexpectedToken("id");
			}
		}
	}
	
	public void fArrayOpt() {
		if (checkCategory(TokenCategory.arrBegin)) {
			printProduction("ArrayOpt", "'[' Ea ']'");
			System.out.println(currentToken);
			setNextToken(true);
			fEa();
			if (!checkCategory(TokenCategory.arrEnd)) {
				unexpectedToken("]");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
			}
		} else {
			printProduction("ArrayOpt", epsilon);
		}
	}
	
	public void fProcDecl() {
		if (checkCategory(TokenCategory.procDef)) {
			printProduction("ProcDecl", "'proc' FunName '(' LParamDecl ')' Body");
			System.out.println(currentToken);
			setNextToken(true);
			fFunName();
			if (checkCategory(TokenCategory.paramBeg)) {
				System.out.println(currentToken);
				setNextToken(true);
				fLParamDecl();
				if (checkCategory(TokenCategory.paramEnd)) {
					System.out.println(currentToken);
					setNextToken(true);
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
		if (checkCategory(TokenCategory.beginScope)) {
			++scopeCounter;
			printProduction("Body", "'{' BodyPart '}'");
			System.out.println(currentToken);
			setNextToken(true);
			fBodyPart();
			if (!checkCategory(TokenCategory.endScope)) {
				unexpectedToken("}");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
				--scopeCounter;
			}
		} else {
			unexpectedToken("{");
		}
	}
	
	public void fBodyPart() {
		if (checkCategory(TokenCategory.constVar, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr)) {
			printProduction("BodyPart", "DeclId BodyPart");
			fDeclId();
			fBodyPart();
		} else if (checkCategory(TokenCategory.print, TokenCategory.scan, TokenCategory.whileLoop, TokenCategory.forLoop, TokenCategory.condIf)) {
			printProduction("BodyPart", "Command BodyPart");
			fCommand();
			fBodyPart();
		} else if (checkCategory(TokenCategory.id)) {
			printProduction("BodyPart", "BodyPartr ';' BodyPart");
			fBodyPartr();
			if (!checkCategory(TokenCategory.semicolon)) {
				unexpectedToken(";");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
			}
			fBodyPart();
		} else if (checkCategory(TokenCategory.funRet)) {
			printProduction("BodyPart", "'return' Return ';'");
			System.out.println(currentToken);
			setNextToken(true);
			fReturn();
			if (!checkCategory(TokenCategory.semicolon)) {
				unexpectedToken(";");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
			}
		} else {
			printProduction("BodyPart", epsilon);
		}
	}
	
	public void fBodyPartr() {
		if (checkCategory(TokenCategory.id)) {
			printProduction("BodyPartr", "'id' ParamAttr");
			System.out.println(currentToken);
			setNextToken(true);
			fParamAttr();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fParamAttr() {
		if (checkCategory(TokenCategory.paramBeg)) {
			printProduction("ParamAttrib", "'(' LParamCall ')'");
			System.out.println(currentToken);
			setNextToken(true);
			fLParamCall();
			if (!checkCategory(TokenCategory.paramEnd)) {
				unexpectedToken(")");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
			}
		} else if (checkCategory(TokenCategory.opAttrib)) {
			printProduction("ParamAttrib", "'=' Ec LAttr");
			System.out.println(currentToken);
			setNextToken(true);
			fEc();
			fLAttr();
		} else if (checkCategory(TokenCategory.arrBegin)) {
			printProduction("ParamAttrib", "'[' Ea ']' '=' Ec LAttr");
			System.out.println(currentToken);
			setNextToken(true);
			fEa();
			if (checkCategory(TokenCategory.arrEnd)) {
				System.out.println(currentToken);
				setNextToken(true);
				if (checkCategory(TokenCategory.opAttrib)) {
					System.out.println(currentToken);
					setNextToken(true);
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
		if (checkCategory(TokenCategory.commaSep)) {
			printProduction("LAttr", "',' Id '=' Ec LAttr");
			System.out.println(currentToken);
			setNextToken(true);
			fId();
			if (checkCategory(TokenCategory.opAttrib)) {
				System.out.println(currentToken);
				setNextToken(true);
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
		if (checkCategory(TokenCategory.paramBeg, TokenCategory.opSub, TokenCategory.constInt, TokenCategory.constBool, TokenCategory.constChar, TokenCategory.constFloat, TokenCategory.constStr, TokenCategory.id)) {
			printProduction("Return", "Ec");
			fEc();
		} else {
			printProduction("Return", epsilon);
		}
	}
	
	public void fCommand() {
		if (checkCategory(TokenCategory.print)) {
			printProduction("Command", "'print' '(' 'constStr' PrintLParam ')' ';'");
			System.out.println(currentToken);
			setNextToken(true);
			if (checkCategory(TokenCategory.paramBeg)) {
				System.out.println(currentToken);
				setNextToken(true);
				if (checkCategory(TokenCategory.constStr)) {
					System.out.println(currentToken);
					setNextToken(true);
					fPrintLParam();
					if (checkCategory(TokenCategory.paramEnd)) {
						System.out.println(currentToken);
						setNextToken(true);
						if (!checkCategory(TokenCategory.semicolon)) {
							unexpectedToken(";");
						} else {
							System.out.println(currentToken);
							setNextToken(true);
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
		} else if (checkCategory(TokenCategory.scan)) {
			printProduction("Command", "'scan' '(' ScanLParam ')' ';'");
			System.out.println(currentToken);
			setNextToken(true);
			if (checkCategory(TokenCategory.paramBeg)) {
				System.out.println(currentToken);
				setNextToken(true);
				fScanLParam();
				if (checkCategory(TokenCategory.paramEnd)) {
					System.out.println(currentToken);
					setNextToken(true);
					if (!checkCategory(TokenCategory.semicolon)) {
						unexpectedToken(";");
					} else {
						System.out.println(currentToken);
						setNextToken(true);
					}
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else if (checkCategory(TokenCategory.whileLoop)) {
			printProduction("Command", "'whileLoop' '(' Eb ')' Body");
			System.out.println(currentToken);
			setNextToken(true);
			if (checkCategory(TokenCategory.paramBeg)) {
				System.out.println(currentToken);
				setNextToken(true);
				fEb();
				if (checkCategory(TokenCategory.paramEnd)) {
					System.out.println(currentToken);
					setNextToken(true);
					fBody();
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else if (checkCategory(TokenCategory.forLoop)) {
			printProduction("Command", "'forLoop' ForParams");
			System.out.println(currentToken);
			setNextToken(true);
			fForParams();
		} else if (checkCategory(TokenCategory.condIf)) {
			printProduction("Command", "'condIf' '(' Eb ')' Body Ifr");
			System.out.println(currentToken);
			setNextToken(true);
			if (checkCategory(TokenCategory.paramBeg)) {
				System.out.println(currentToken);
				setNextToken(true);
				fEb();
				if (checkCategory(TokenCategory.paramEnd)) {
					System.out.println(currentToken);
					setNextToken(true);
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
		if (checkCategory(TokenCategory.commaSep)) {
			printProduction("PrintLParam", "',' Ec PrintLParam");
			System.out.println(currentToken);
			setNextToken(true);
			fEc();
			fPrintLParam();
		} else {
			printProduction("PrintLParam", epsilon);
		}
	}
	
	public void fScanLParam() {
		if (checkCategory(TokenCategory.id)) {
			printProduction("ScanLParam", "'id' ArrayOpt ScanLParamr");
			System.out.println(currentToken);
			setNextToken(true);
			fArrayOpt();
			fScanLParamr();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fScanLParamr() {
		if (checkCategory(TokenCategory.commaSep)) {
			printProduction("ScanLParamr", "',' 'id' ArrayOpt ScanLParamr");
			System.out.println(currentToken);
			setNextToken(true);
			if (checkCategory(TokenCategory.id)) {
				System.out.println(currentToken);
				setNextToken(true);
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
		if (checkCategory(TokenCategory.paramBeg)) {
			printProduction("ForParams", "'(' 'typeInt' 'id' ':' '(' Ea ',' Ea ForStep ')' ')' Body");
			System.out.println(currentToken);
			setNextToken(true);
			if (checkCategory(TokenCategory.typeInt)) {
				System.out.println(currentToken);
				setNextToken(true);
				if (checkCategory(TokenCategory.id)) {
					System.out.println(currentToken);
					setNextToken(true);
					if (checkCategory(TokenCategory.colon)) {
						System.out.println(currentToken);
						setNextToken(true);
						if (checkCategory(TokenCategory.paramBeg)) {
							System.out.println(currentToken);
							setNextToken(true);
							fEa();
							if (checkCategory(TokenCategory.commaSep)) {
								System.out.println(currentToken);
								setNextToken(true);
								fEa();
								fForStep();
								if (checkCategory(TokenCategory.paramEnd)) {
									System.out.println(currentToken);
									setNextToken(true);
									if (checkCategory(TokenCategory.paramEnd)) {
										System.out.println(currentToken);
										setNextToken(true);
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
		if (checkCategory(TokenCategory.commaSep)) {
			printProduction("ForStep", "',' Ea");
			System.out.println(currentToken);
			setNextToken(true);
			fEa();
		} else {
			printProduction("ForStep", epsilon);
		}
	}
	
	public void fIfr() {
		if (checkCategory(TokenCategory.condElseIf)) {
			printProduction("Ifr", "'condElseIf' '(' Eb ')' Body Ifr");
			System.out.println(currentToken);
			setNextToken(true);
			if (checkCategory(TokenCategory.paramBeg)) {
				System.out.println(currentToken);
				setNextToken(true);
				fEb();
				if (checkCategory(TokenCategory.paramEnd)) {
					System.out.println(currentToken);
					setNextToken(true);
					fBody();
					fIfr();
				} else {
					unexpectedToken(")");
				}
			} else {
				unexpectedToken("(");
			}
		} else if (checkCategory(TokenCategory.condElse)) {
			printProduction("Ifr", "'condElse' Body");
			System.out.println(currentToken);
			setNextToken(true);
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
		if (checkCategory(TokenCategory.opConcat)) {
			printProduction("Ecr", "'opConcat' Fc Ecr");
			System.out.println(currentToken);
			setNextToken(true);
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
		if (checkCategory(TokenCategory.opOr)) {
			printProduction("Ebr", "'opOr' Tb Ebr");
			System.out.println(currentToken);
			setNextToken(true);
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
		if (checkCategory(TokenCategory.opAnd)) {
			printProduction("Tbr", "'opAnd' Fb Tbr");
			System.out.println(currentToken);
			setNextToken(true);
			fFb();
			fTbr();
		} else {
			printProduction("Tbr", epsilon);
		}
	}
	
	public void fFb() {
		if (checkCategory(TokenCategory.opNot)) {
			printProduction("Fb", "'opNot' Fb");
			System.out.println(currentToken);
			setNextToken(true);
			fFb();
		} else if (checkCategory(TokenCategory.paramBeg, TokenCategory.opSub, TokenCategory.constInt, TokenCategory.constBool, TokenCategory.constChar, TokenCategory.constFloat, TokenCategory.constStr, TokenCategory.id)) {
			printProduction("Fb", "Ra Fbr");
			fRa();
			fFbr();
		} else {
			unexpectedToken("!, (, -, constInt, constBool, constChar, constFloat, constStr or id");
		}
	}
	
	public void fFbr() {
		if (checkCategory(TokenCategory.opGreater)) {
			printProduction("Fbr", "'opGreater' Ra Fbr");
			System.out.println(currentToken);
			setNextToken(true);
			fRa();
			fFbr();
		} else if (checkCategory(TokenCategory.opLesser)) {
			printProduction("Fbr", "'opLesser' Ra Fbr");
			System.out.println(currentToken);
			setNextToken(true);
			fRa();
			fFbr();
		} else if (checkCategory(TokenCategory.opGreq)) {
			printProduction("Fbr", "'opGreq' Ra Fbr");
			System.out.println(currentToken);
			setNextToken(true);
			fRa();
			fFbr();
		} else if (checkCategory(TokenCategory.opLeq)) {
			printProduction("Fbr", "'opLeq' Ra Fbr");
			System.out.println(currentToken);
			setNextToken(true);
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
		if (checkCategory(TokenCategory.opEquals)) {
			printProduction("Rar", "'opEquals' Ea Rar");
			System.out.println(currentToken);
			setNextToken(true);
			fEa();
			fRar();
		} else if (checkCategory(TokenCategory.opNotEqual)) {
			printProduction("Rar", "'opNotEqual' Ea Rar");
			System.out.println(currentToken);
			setNextToken(true);
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
		if (checkCategory(TokenCategory.opAdd)) {
			printProduction("Ear", "'opAdd' Ta Ear");
			System.out.println(currentToken);
			setNextToken(true);
			fTa();
			fEar();
		} else if (checkCategory(TokenCategory.opSub)) {
			printProduction("Ear", "'opSub' Ta Ear");
			System.out.println(currentToken);
			setNextToken(true);
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
		if (checkCategory(TokenCategory.opMult)) {
			printProduction("Tar", "'opMult' Pa Tar");
			System.out.println(currentToken);
			setNextToken(true);
			fPa();
			fTar();
		} else if (checkCategory(TokenCategory.opDiv)) {
			printProduction("Tar", "'opDiv' Pa Tar");
			System.out.println(currentToken);
			setNextToken(true);
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
		if (checkCategory(TokenCategory.opPow)) {
			printProduction("Par", "'opPow' Fa Par");
			System.out.println(currentToken);
			setNextToken(true);
			fFa();
			fPar();
		} else {
			printProduction("Par", epsilon);
		}
	}
	
	public void fFa() {
		if (checkCategory(TokenCategory.paramBeg)) {
			printProduction("Fa", "'(' Ec ')'");
			System.out.println(currentToken);
			setNextToken(true);
			fEc();
			if (!checkCategory(TokenCategory.paramEnd)) {
				unexpectedToken(")");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
			}
		} else if (checkCategory(TokenCategory.opSub)) {
			printProduction("Fa", "'opSub' Fa");
			System.out.println(currentToken);
			setNextToken(true);
			fFa();
		} else if (checkCategory(TokenCategory.id)) {
			fIdOrFunCall();
		} else if (checkCategory(TokenCategory.constBool)) {
			printProduction("Fa", "'constBool'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.constChar)) {
			printProduction("Fa", "'constChar'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.constFloat)) {
			printProduction("Fa", "'constFloat'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.constInt)) {
			printProduction("Fa", "'constInt'");
			System.out.println(currentToken);
			setNextToken(true);
		} else if (checkCategory(TokenCategory.constStr)) {
			printProduction("Fa", "'constStr'");
			System.out.println(currentToken);
			setNextToken(true);
		} else {
			unexpectedToken("constBool, constChar, constFloat, constInt, constStr");
		}
	}
	
	public void fIdOrFunCall() {
		if (checkCategory(TokenCategory.id)) {
			printProduction("IdOrFunCall", "'id' IdOrFunCallr");
			System.out.println(currentToken);
			setNextToken(true);
			fIdOrFunCallr();
		} else {
			unexpectedToken("id");
		}
	}
	
	public void fIdOrFunCallr() {
		if (checkCategory(TokenCategory.paramBeg)) {
			printProduction("IdOrFunCallr", "'(' LParamCall ')'");
			System.out.println(currentToken);
			setNextToken(true);
			fLParamCall();
			if (!checkCategory(TokenCategory.paramEnd)) {
				unexpectedToken(")");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
			}
		} else if (checkCategory(TokenCategory.arrBegin)) {
			printProduction("IdOrFunCallr", "'[' Ea ']'");
			System.out.println(currentToken);
			setNextToken(true);
			fEa();
			if (!checkCategory(TokenCategory.arrEnd)) {
				unexpectedToken("]");
			} else {
				System.out.println(currentToken);
				setNextToken(true);
			}
		} else {
			printProduction("IdOrFunCallr", epsilon);
		}
	}
}
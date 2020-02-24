package syntactic;

import java.io.IOException;

import lexical.Lexical;
import tokens.Token;
import tokens.TokenCategory;

public class Syntactic {
	
	private Lexical lexicalAnalyzer;
	private Token token;
	String epsilon = "epsilon";
	boolean EOF = false;
	
	public void printProduction(String left, String right) {
		String format = "%10s%s = %s";
		System.out.println(String.format(format, "", left, right));
	}
	
	public void printError(String message) {
		System.err.println("Error: "+ message + " In line " + (token.getLine() - 1) + " and column " + token.getColumn());
		System.exit(0);
	}
	
	public void setNextToken() {
		if (lexicalAnalyzer.hasNextToken()) {
			token = lexicalAnalyzer.nextToken();
		} else {
			printError("Unexpected EOF");
		}
	}
	
	public void fS() {
		if (token.getTokenCategory() == TokenCategory.constVar 
				|| token.getTokenCategory() == TokenCategory.typeInt
				|| token.getTokenCategory() == TokenCategory.typeStr
				|| token.getTokenCategory() == TokenCategory.typeBool
				|| token.getTokenCategory() == TokenCategory.typeChar
				|| token.getTokenCategory() == TokenCategory.typeFloat) {
			printProduction("S", "DeclId S");
			fDeclId();
			fS();
		} else if (token.getTokenCategory() == TokenCategory.procDef) {
			printProduction("S", "ProcDecl S");
			fProcDecl();
			fS();
		} else if (token.getTokenCategory() == TokenCategory.funDef) {
			printProduction("S", "FunDecl S");
			fFunDecl();
			fS();
		} else if (EOF){
			printProduction("S", epsilon);
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'const', 'int', 'string', 'bool', 'char', 'float', 'proc', 'fun' or EOF");
		}
	}

	public void fProcDecl() {
		if (token.getTokenCategory() == TokenCategory.procDef) {
			printProduction("ProdDecl", "'procDef' 'id' Param Body");
			setNextToken();
			if (token.getTokenCategory() == TokenCategory.id) {
				setNextToken();
				fParam();
				fBody();
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected 'id'.");
			}
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'procDef'.");
		}
	}
	
	public void fDeclId() {
		if (token.getTokenCategory() == TokenCategory.constVar) {
			setNextToken();
			fType();
			fLId();
		} else if (token.getTokenCategory() == TokenCategory.typeInt
				|| token.getTokenCategory() == TokenCategory.typeStr
				|| token.getTokenCategory() == TokenCategory.typeBool
				|| token.getTokenCategory() == TokenCategory.typeChar
				|| token.getTokenCategory() == TokenCategory.typeFloat) {
			printProduction("DeclId", "Type LId");
			fType();
			fLId();
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'constVar', 'typeInt', 'typeStr', 'typeBool' or 'typeFloat'.");
		}
	}
	
	public void fType() {
		if (token.getTokenCategory() == TokenCategory.typeInt) {
			printProduction("Type", "'int'");
			setNextToken();
		} else if (token.getTokenCategory() == TokenCategory.typeBool) {
			printProduction("Type", "'bool'");
			setNextToken();
		} else if (token.getTokenCategory() == TokenCategory.typeChar) {
			printProduction("Type", "'char'");
			setNextToken();
		} else if (token.getTokenCategory() == TokenCategory.typeFloat) {
			printProduction("Type", "'float'");
			setNextToken();
		} else if (token.getTokenCategory() == TokenCategory.typeStr) {
			printProduction("Type", "'string'");
			setNextToken();
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'typeInt', 'typeStr', 'typeBool' or 'typeFloat'.");
		}
	}
	
	public void fLId() {
		if (token.getTokenCategory() == TokenCategory.id) {			
			printProduction("LId", "'id' IdAttr LIdr ';'");
			setNextToken();
			fIdAttr();
			fLIdr();
			if (token.getTokenCategory() == TokenCategory.semicolon) {
				setNextToken();
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected ';'");
			}
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'id'");
		}
	}
	
	public void fIdAttr() {
		if (token.getTokenCategory() == TokenCategory.paramBeg) {
			printProduction("IdAttr", "FunCall");
			fFunCall();
		} else if (token.getTokenCategory() == TokenCategory.arrBegin 
				|| token.getTokenCategory() == TokenCategory.opAttrib
				|| token.getTokenCategory() == TokenCategory.commaSep
				|| token.getTokenCategory() == TokenCategory.semicolon) {
			printProduction("IdAttr", "ArrayOpt AttrOpt");
			fArrayOpt();
			fAttrOpt();
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'paramBeg' or 'arrBegin' or 'opAttrib'.");
		}
	}
	
	public void fLIdr() {
		if (token.getTokenCategory() == TokenCategory.commaSep) {
			printProduction("LIdr", "',' 'id' ArrayOpt AttrOpt LIdr");
			setNextToken();
			if (token.getTokenCategory() == TokenCategory.id) {
				setNextToken();
				fArrayOpt();
				fAttrOpt();
				fLIdr();
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected 'id'.");
			}
		} else {
			printProduction("LIdr", epsilon);
		}
	}
	
	public void fArrayOpt() {
		if (token.getTokenCategory() == TokenCategory.arrBegin) {
			printProduction("ArrayOpt", "'[' Ea ']'");
			setNextToken();
			fEa();
			if (token.getTokenCategory() == TokenCategory.arrEnd) {
				setNextToken();
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected ']'");
			}
		} else {
			printProduction("ArrayOpt", epsilon);
		}
	}
	
	public void fAttrOpt() {
		if (token.getTokenCategory() == TokenCategory.opAttrib) {
			printProduction("AttrOpt", "'='");
			setNextToken();
			fEc();
		} else {
			printProduction("AttrOpt", epsilon);
		}
	}
	
	public void fFunDecl() {
		if (token.getTokenCategory() == TokenCategory.funDef) {
			printProduction("FunDecl", "'funDef' Type FunName Param Body");
			setNextToken();
			fType();
			fFunName();
			fParam();
			fBody();
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'funDef'.");
		}
	}
	
	public void fFunName() {
		if (token.getTokenCategory() == TokenCategory.id) {
			printProduction("FunName", "'id'");
			setNextToken();
		} else if (token.getTokenCategory() == TokenCategory.main) {
			printProduction("FunName", "'main'");
			setNextToken();
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'id' or 'main'");
		}
	}
	
	public void fParam() {
		if (token.getTokenCategory() == TokenCategory.paramBeg) {
			printProduction("Param", "'(' LParam ')'");
			setNextToken();
			fLParam();
			if (token.getTokenCategory() == TokenCategory.paramEnd) {
				setNextToken();
			}
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
		}
	}

	public void fLParam() {
		if (token.getTokenCategory() == TokenCategory.typeInt
				|| token.getTokenCategory() == TokenCategory.typeStr
				|| token.getTokenCategory() == TokenCategory.typeBool
				|| token.getTokenCategory() == TokenCategory.typeChar
				|| token.getTokenCategory() == TokenCategory.typeFloat) {			
			printProduction("LParam", "Type 'id' ArrayOpt LParamr");
			fType();
			if (token.getTokenCategory() == TokenCategory.id) {
				setNextToken();
				fArrayOpt();
				fLParamr();
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected 'id'.");
			}
		} else {
			printProduction("LParam", epsilon);
		}
	}
	
	public void fLParamr() {
		if (token.getTokenCategory() == TokenCategory.commaSep) {
			printProduction("LParamr", "',' Type 'id' ArrayOpt LParamr");
			setNextToken();
			fType();
			if (token.getTokenCategory() == TokenCategory.id) {
				setNextToken();
				fArrayOpt();
				fLParamr();
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected 'id'");
			}
		} else {
			printProduction("LParam", epsilon);
		}
	}
	
	public void fBody() {
		if (token.getTokenCategory() == TokenCategory.beginScope) {
			printProduction("Body", "'{' BodyPart '}'");
			setNextToken();
			fBodyPart();
			if (token.getTokenCategory() == TokenCategory.endScope) {
				if (lexicalAnalyzer.hasNextToken()) {
					token = lexicalAnalyzer.nextToken();					
				} else {
					EOF = true;
				}
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected 'endScope'.");
			}
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected '{' at line " + lexicalAnalyzer.lineCounter + " and " + lexicalAnalyzer.column + ".");
		}
	}
	
	public void fBodyPart() {
		if (token.getTokenCategory() == TokenCategory.typeInt
				|| token.getTokenCategory() == TokenCategory.typeStr
				|| token.getTokenCategory() == TokenCategory.typeBool
				|| token.getTokenCategory() == TokenCategory.typeChar
				|| token.getTokenCategory() == TokenCategory.typeFloat
				|| token.getTokenCategory() == TokenCategory.constVar) {
			printProduction("BodyPart", "DeclId BodyPart");
			fDeclId();
			fBodyPart();
		} else if (token.getTokenCategory() == TokenCategory.id) {
			printProduction("BodyPart", "LId BodyPart");
			fLId();
			fBodyPart();
		} else if (token.getTokenCategory() == TokenCategory.print
				|| token.getTokenCategory() == TokenCategory.scan
				|| token.getTokenCategory() == TokenCategory.whileLoop
				|| token.getTokenCategory() == TokenCategory.forLoop
				|| token.getTokenCategory() == TokenCategory.condIf) {
			printProduction("BodyPart", "Command BodyPart");
			fCommand();
			fBodyPart();
		} else if(token.getTokenCategory() == TokenCategory.funRet) {
			printProduction("BodyPart", "Return ';'");
			fReturn();
			if (token.getTokenCategory() == TokenCategory.semicolon) {
				setNextToken();
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected ';'.");
			}
		} else {
			printProduction("BodyPart", epsilon);
		}
	}
	
	public void fReturn() {
		if (token.getTokenCategory() == TokenCategory.funRet) {
			printProduction("Return", "'funRet' Ec");
			setNextToken();
			fEc();
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'funRet'.");
		}
	}
	
	public void fCommand() {
		if (token.getTokenCategory() == TokenCategory.print) {
			printProduction("Command", "'print' '(' Ec LParamCall ')' ';'");
			setNextToken();
			if (token.getTokenCategory() == TokenCategory.paramBeg) {
				setNextToken();
				fEc();
				fPrintParam();
				if (token.getTokenCategory() == TokenCategory.paramEnd) {
					setNextToken();
					if (token.getTokenCategory() == TokenCategory.semicolon) {
						setNextToken();
					} else {
						printError("Unexpected token " + token.getLexeme() + ". Expected ';'.");
					}
				} else {
					printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
				}
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected '('");
			}
		} else if (token.getTokenCategory() == TokenCategory.scan) {
			printProduction("Command", "'scan' '(' LParamCall ')' ';'");
			setNextToken();
			if (token.getTokenCategory() == TokenCategory.paramBeg) {
				setNextToken();
				fLParamCall();
				if (token.getTokenCategory() == TokenCategory.paramEnd) {
					setNextToken();
					if (token.getTokenCategory() == TokenCategory.semicolon) {
						setNextToken();
					} else {
						printError("Unexpected token " + token.getLexeme() + ". Expected ';'.");
					}
				} else {
					printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
				}
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected '('");
			}
		} else if (token.getTokenCategory() == TokenCategory.condIf) {
			printProduction("Command", "'condIf' '(' Eb ')' Body Ifr");
			setNextToken();
			if (token.getTokenCategory() == TokenCategory.paramBeg) {
				setNextToken();
				fEb();
				if (token.getTokenCategory() == TokenCategory.paramEnd) {
					setNextToken();
					fBody();
					fIfr();
				} else {
					printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
				}
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected '('");
			}
		} else if (token.getTokenCategory() == TokenCategory.whileLoop) {
			printProduction("Command", "'whileLoop' '(' Eb ')' Body ");
			setNextToken();
			if (token.getTokenCategory() == TokenCategory.paramBeg) {
				setNextToken();
				fEb();
				if (token.getTokenCategory() == TokenCategory.paramEnd) {
					setNextToken();
					fBody();
				} else {
					printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
				}
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected '('");
			}
		} else if (token.getTokenCategory() == TokenCategory.forLoop) {
			printProduction("Command", "'forLoop' '(' 'typeInt' 'id' ':' '(' Ec ',' Ec ',' Ec ')' ')' Body");
			setNextToken();
			if (token.getTokenCategory() == TokenCategory.paramBeg) {
				setNextToken();
				if (token.getTokenCategory() == TokenCategory.typeInt) {
					setNextToken();
					if (token.getTokenCategory() == TokenCategory.id) {
						setNextToken();
						if (token.getTokenCategory() == TokenCategory.colon) {
							setNextToken();
							if (token.getTokenCategory() == TokenCategory.paramBeg) {
								setNextToken();
								fEc();
								if (token.getTokenCategory() == TokenCategory.commaSep) {
									setNextToken();
									fEc();
									if (token.getTokenCategory() == TokenCategory.commaSep) {
										setNextToken();
										fEc();
										if (token.getTokenCategory() == TokenCategory.paramEnd) {
											setNextToken();
											if (token.getTokenCategory() == TokenCategory.paramEnd) {
												setNextToken();
												fBody();
											} else {
												printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
											}
										}  else {
											printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
										}
									}  else {
										printError("Unexpected token " + token.getLexeme() + ". Expected ','");
									}
								}  else {
									printError("Unexpected token " + token.getLexeme() + ". Expected ','");
								}
							}  else {
								printError("Unexpected token " + token.getLexeme() + ". Expected '('");
							}
						} else {
							printError("Unexpected token " + token.getLexeme() + ". Expected ':'");
						}
					} else {
						printError("Unexpected token " + token.getLexeme() + ". Expected 'id'");
					}
				}  else {
					printError("Unexpected token " + token.getLexeme() + ". Expected 'typeInt'");
				}
			}  else {
				printError("Unexpected token " + token.getLexeme() + ". Expected '('");
			}
		}  else {
			printError("Unexpected token " + token.getLexeme() + ". Expected 'forLoop'");
		}
	}
	
	public void fIfr() {
		if (token.getTokenCategory() == TokenCategory.condElseIf) {
			printProduction("Ifr", "'condElseIf' '(' Eb ')' Body Ifr");
			setNextToken();
			if (token.getTokenCategory() == TokenCategory.paramBeg) {
				setNextToken();
				fEb();
				if (token.getTokenCategory() == TokenCategory.paramEnd) {
					setNextToken();
					fBody();
					fIfr();
				} else {
					printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
				}
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected '('");
			}
		} else if (token.getTokenCategory() == TokenCategory.condElse) {
			printProduction("Ifr", "'condElse' Body");
			setNextToken();
			fBody();
		} else {
			printProduction("Ifr", epsilon);
		}
	}
	
	public void fPrintParam() {
		if (token.getTokenCategory() == TokenCategory.commaSep) {
			printProduction("PrintParam", "',' LParamCall");
			setNextToken();
			fLParamCall();
		} else {
			printProduction("PrintParam", epsilon);
		}
	}
	
	public void fLParamCall() {
		printProduction("ParamCall", "Ec LParamCallr");
		fEc();
		fLParamCallr();
	}
	
	public void fLParamCallr() {
		if (token.getTokenCategory() == TokenCategory.commaSep) {
			printProduction("LParamCallr", "',' Ec LParamCallr");
			setNextToken();
			fEc();
			fLParamCallr();
		} else {
			printProduction("LParamCallr", epsilon);
		}
	}
	
	public void fEc() {
		printProduction("Ec", "Fc Ecr");
		fFc();
		fEcr();
	}
	
	public void fFc() {
		if (token.getTokenCategory() == TokenCategory.constStr) {
			printProduction("Fc", "'constStr'");
			setNextToken();
		} else if (token.getTokenCategory() == TokenCategory.constChar) {
			printProduction("Fc", "'constChar'");
			setNextToken();
		} else {
			printProduction("Fc", "Eb");
			fEb();
		}
	}
	
	public void fEcr() {
		if (token.getTokenCategory() == TokenCategory.opConcat) {
			printProduction("Ecr", "'opConcat' Fc Ecr");
			setNextToken();
			fFc();
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
	
	public void fTb() {
		printProduction("Tb", "Fb Tbr");
		fFb();
		fTbr();
	}
	
	public void fEbr() {
		if (token.getTokenCategory() == TokenCategory.opOr) {
			printProduction("Ebr", "'opOr' Tb Ebr");
			setNextToken();
			fTb();
			fEbr();
		} else {
			printProduction("Ebr", epsilon);
		}
	}
	
	public void fFb() {
		if (token.getTokenCategory() == TokenCategory.opNot) {
			printProduction("Fb", "'opNot' Fb");
			setNextToken();
		} else if (token.getTokenCategory() == TokenCategory.constBool) {
			printProduction("Fb", "'cteBool'");
			setNextToken();
		} else {
			printProduction("Fb", "Ra Fbr");
			fRa();
			fFbr();
		}
	}
	
	public void fTbr() {
		if (token.getTokenCategory() == TokenCategory.opAnd) {
			printProduction("Tbr", "'opAnd' Fb Tbr");
			setNextToken();
			fFb();
			fTbr();
		} else {
			printProduction("Tbr", epsilon);
		}
	}
	
	public void fRa() {
		printProduction("Ra", "Ea Rar");
		fEa();
		fRar();
	}
	
	public void fFbr() {
		if (token.getTokenCategory() == TokenCategory.opGreater) {
			printProduction("Fbr", "'opGreater' Ra Fbr");
			setNextToken();
			fRa();
			fFbr();
		} else if (token.getTokenCategory() == TokenCategory.opLesser) {
			printProduction("Fbr", "'opLesser' Ra Fbr");
			setNextToken();
			fRa();
			fFbr();
		} else if (token.getTokenCategory() == TokenCategory.opGreq) {
			printProduction("Fbr", "'opGreq' Ra Fbr");
			setNextToken();
			fRa();
			fFbr();
		} else if (token.getTokenCategory() == TokenCategory.opLeq) {
			printProduction("Fbr", "'opLeq' Ra Fbr");
			setNextToken();
			fRa();
			fFbr();
		} else {
			printProduction("Fbr", epsilon);
		}
	}
	
	public void fEa() {
		printProduction("Ea", "Ta Ear");
		fTa();
		fEar();
	}
	
	public void fRar() {
		if (token.getTokenCategory() == TokenCategory.opEquals) {
			printProduction("Rar", "'opEquals' Ea Rar");
			setNextToken();
			fEa();
			fRar();
		} else if (token.getTokenCategory() == TokenCategory.opNotEqual) {
			printProduction("Rar", "'opNotEqual' Ea Rar");
			setNextToken();
			fEa();
			fRar();
		} else {
			printProduction("Rar", epsilon);
		}
	}
	
	public void fTa() {
		printProduction("Ta", "Pa Tar");
		fPa();
		fTar();
	}
	
	public void fEar() {
		if (token.getTokenCategory() == TokenCategory.opAdd) {
			printProduction("Ear", "'opAdd' Ta Ear");
			setNextToken();
			fTa();
			fEar();
		} else if (token.getTokenCategory() == TokenCategory.opSub) {
			printProduction("Ear", "'opSub' Ta Ear");
			setNextToken();
			fTa();
			fEar();
		} else {
			printProduction("Ear", epsilon);
		}
	}
	
	public void fPa() {
		printProduction("Pa", "Fa Par");
		fFa();
		fPar();
	}
	
	public void fTar() {
		if (token.getTokenCategory() == TokenCategory.opMult) {
			printProduction("Tar", "'opMult' Pa Tar");
			setNextToken();
			fPa();
			fTar();
		} else if (token.getTokenCategory() == TokenCategory.opDiv) {
			printProduction("Tar", "'opDiv' Pa Tar");
			setNextToken();
			fPa();
			fTar();
		} else {
			printProduction("Tar", epsilon);
		}
	}
	
	public void fFa() {
		if (token.getTokenCategory() == TokenCategory.paramBeg) {
			printProduction("Fa", "'(' Ec ')'");
			setNextToken();
			fEc();
			if (token.getTokenCategory() == TokenCategory.paramEnd) {
				setNextToken();
			}
		} else if (token.getTokenCategory() == TokenCategory.opSub) {
			printProduction("Fa", "'opSub' Ec");
			setNextToken();
			fEc();
		} else if (token.getTokenCategory() == TokenCategory.id) {
			printProduction("Fa", "Id");
			fId();
		} else if (token.getTokenCategory() == TokenCategory.constInt) {
			printProduction("Fa", "'cteInt'");
			setNextToken();
		} else if (token.getTokenCategory() == TokenCategory.constFloat) {
			printProduction("Fa", "'cteFloat'");
			setNextToken();
		}
	}
	
	public void fPar() {
		if (token.getTokenCategory() == TokenCategory.opPow) {
			printProduction("Par", "'opPow' Fa Par");
			setNextToken();
			fFa();
			fPar();
		} else {
			printProduction("Par", epsilon);
		}
	}
	
	public void fId() {
		if (token.getTokenCategory() == TokenCategory.id) {
			printProduction("Id", "'id' IdOpt");
			setNextToken();
			fIdOpt();
		}
	}
	
	public void fIdOpt() {	
		if (token.getTokenCategory() == TokenCategory.paramBeg) {
			printProduction("IdOpt", "ArrayOpt");
			fFunCall();
		} else {
			printProduction("IdOpt", "ArrayOpt");
			fArrayOpt();
		}
	}
	
	public void fFunCall() {
		if (token.getTokenCategory() == TokenCategory.paramBeg) {
			printProduction("FunCall", "'(' LParamCall ')'");
			setNextToken();
			fLParamCall();
			if (token.getTokenCategory() == TokenCategory.paramEnd) {
				setNextToken();
			} else {
				printError("Unexpected token " + token.getLexeme() + ". Expected ')'");
			}
		} else {
			printError("Unexpected token " + token.getLexeme() + ". Expected '('");
		}
	}
	
	public Syntactic(String[] args) {
		for (String s : args) {
			try {
				lexicalAnalyzer = new Lexical(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (lexicalAnalyzer.hasNextToken()) {
				setNextToken();				
				fS();
			}
		}
	}
}
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
				//TODO: fS();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			assert lexicalAnalyzer != null;
//			while (lexicalAnalyzer.hasNextToken()) {
//				System.out.println(lexicalAnalyzer.nextToken().toString());
//			}
		}
	}

	public void setNextToken(boolean isRequired) {
		if (lexicalAnalyzer.hasNextToken()) {
			currentToken = lexicalAnalyzer.nextToken();
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
		if (checkCategory(false, TokenCategory.typeInt, TokenCategory.typeFloat, TokenCategory.typeBool, TokenCategory.typeChar, TokenCategory.typeStr)) {
			printProduction("S", "DeclId S");
			// fDeclId();
			fS();
		} else if (checkCategory(false, TokenCategory.funDef)) {
			printProduction("S", "FunDecl S");
			// fFunDecl();
			fS();
		} else if (checkCategory(false, TokenCategory.procDef)) {
			printProduction("S", "ProcDecl S");
			// fProcDecl();
			fS();
		} else {
			printProduction("S", "ε");
		}
	}

}
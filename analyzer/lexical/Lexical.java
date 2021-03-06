package lexical;

import java.io.*;
import tokens.Token;
import tokens.TokenCategory;

@SuppressWarnings("unused")
public class Lexical {

	public Token previousToken;
	public Token currentToken;
	public int lineCounter = 1, column = 0, lastColumn = 0;
	private BufferedReader bufferedReader;
	public String codeLine = "";

	/**
	 * @param filePath the file path
	 * @throws IOException if filePath is invalid
	 */
	public Lexical(String filePath) throws IOException {
		this.bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
	}

	private String nextCharacter() {
		if (column < codeLine.length()) {
			return Character.toString(codeLine.charAt(column));
		}
		return "";
	}

	/**
	 * @return Token the next token if there is one or null otherwise
	 */
	public Token nextToken() {
		TokenCategory category = TokenCategory.unknown;
		while (codeLine.charAt(column) == ' ' || codeLine.charAt(column) == '\t') {
			++column;
		}
		String lexeme = Character.toString(codeLine.charAt(column));
		boolean endOfFile = true;
		if (lexeme.equals(".")) {
			endOfFile = false;
			++column;
			while (column < codeLine.length() && !LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
				lexeme += nextCharacter();
				++column;
			}
			--column;
		} else if (lexeme.equals(">") || lexeme.equals("<") || lexeme.equals("!") || lexeme.equals("=")) {
			endOfFile = false;
			if (column + 1 < codeLine.length() && codeLine.charAt(column + 1) == '=') {
				++column;
				lexeme += nextCharacter();
			}
			category = LexemeTable.tokenMapping.get(lexeme);
		} else if (lexeme.equals(":") || lexeme.equals("&") || lexeme.equals("|")) {
			endOfFile = false;
			char next = codeLine.charAt(column + 1);
			if (next == lexeme.charAt(0)) {
				lexeme += nextCharacter();
				category = LexemeTable.tokenMapping.get(lexeme);
				++column;
			} else {
				if (LexemeTable.tokenMapping.get(lexeme) != null) {
					category = LexemeTable.tokenMapping.get(lexeme);
				} else {
					category = TokenCategory.unknown;
				}
			}
		} else if (lexeme.equals("\"") || lexeme.equals("\'")) {
			endOfFile = false;
			char str = lexeme.charAt(0);
			int initialColumn = column;
			++column;
			lexeme += nextCharacter();
			while (column < codeLine.length()) {
				if (codeLine.charAt(column) == str) {
					if (codeLine.charAt(column - 1) != '\\' && codeLine.charAt(column) == str) {
						if (str == '\"') {
							category = TokenCategory.constStr;
						} else if (str == '\'') {
							category = TokenCategory.constChar;
							if (lexeme.length() > 4 || lexeme.length() > 3 && !lexeme.contains("\\")) {
								category = TokenCategory.unknown;
							}
						}
						break;
					}
				}
				++column;
				if (column < codeLine.length()) {
					lexeme += nextCharacter();
				}
			}
		} else if (lexeme.matches("\\d")) {
			endOfFile = false;
			category = TokenCategory.constInt;
			while (++column < codeLine.length() && Character.toString(codeLine.charAt(column)).matches("\\d")) {  // Adiciona a lexeme enquanto for um digito
				lexeme += nextCharacter();
			}
			if (column < codeLine.length() && codeLine.charAt(column) == '.') {  // Verificando se EH um float
				lexeme += nextCharacter();
				++column;
				while (column < codeLine.length() && Character.toString(codeLine.charAt(column)).matches("\\d")) { // Vai pegando o valor depois do .
					category = TokenCategory.constFloat;
					lexeme += nextCharacter();
					++column;
				}
				if (category != TokenCategory.constFloat) {  // Se for por exemplo 123.
					category = TokenCategory.unknown;
				}
			} else if (column < codeLine.length() && Character.toString(codeLine.charAt(column)).matches("\\d")) { // Caso nÃ£o seja float ele tem que ir pra o prox char
				++column;
			}
			while (column < codeLine.length() && !LexemeTable.tokenEndings.contains(codeLine.charAt(column))) { // Adicionar o
				lexeme += nextCharacter();
				++column;
				category = TokenCategory.unknown;
			}
			if (column < codeLine.length() && LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
				--column;
			}
		} else if (lexeme.equals("(") || lexeme.equals(")") || lexeme.equals("[") || lexeme.equals("]")) {
			endOfFile = false;
			category = LexemeTable.tokenMapping.get(lexeme);
		} else if (lexeme.matches(".")) {
			endOfFile = false;
			if (lexeme.matches("\\p{ASCII}")) {
				if (lexeme.equals(";")) {
					category = TokenCategory.semicolon;
				} else if (lexeme.equals("-")) {
					category = TokenCategory.opSub;
				} else if (lexeme.equals("+")) {
					category = TokenCategory.opAdd;
				} else if (lexeme.equals("/")) {
					category = TokenCategory.opDiv;
				} else if (lexeme.equals("*")) {
					category = TokenCategory.opMult;
				} else if (lexeme.equals("^")) {
					category = TokenCategory.opPow;
				} else if (lexeme.equals(",")) {
					category = TokenCategory.commaSep;
				} else if (lexeme.equals("{")) {
					category = TokenCategory.beginScope;
				} else if (lexeme.equals("}")) {
					category = TokenCategory.endScope;
				} else if (lexeme.equals("(")) {
					category = TokenCategory.paramBeg;
				} else if (lexeme.equals(")")) {
					category = TokenCategory.paramEnd;
				} else if (lexeme.equals("=")) {
					category = TokenCategory.opAttrib;
				} else if (lexeme.equals("!")) {
					category = TokenCategory.opNot;
				} else {
					while (column < codeLine.length()) {
						boolean nextChar = false;
						++column;
						if (LexemeTable.tokenMapping.containsKey(lexeme)) {
							nextChar = true;
							if (column < codeLine.length() && LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
								--column;
								break;
							}
							lexeme += nextCharacter();
						} else if (column < codeLine.length() && LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
							--column;
							break;
						}
						if (!nextChar) {
							lexeme += nextCharacter();
						}
					}
					if ((category = LexemeTable.tokenMapping.get(lexeme)) == null) {
						category = TokenCategory.unknown;
					}
					if (category == TokenCategory.unknown && lexeme.matches("[_a-zA-Z][0-9_a-zA-Z]*")) {
						category = TokenCategory.id;
					}
				}
			}
		}
		if (endOfFile) {
			category = TokenCategory.EOF;
		}
		if (column == codeLine.length()) {
			if (hasNextLine()) {
				column = 0;
				printCodeLine(codeLine);
			}
		} else {
			++column;
		}
		currentToken = new Token(category, lineCounter - 1, column - lexeme.length() + 1, lexeme);
		previousToken = currentToken;
		return currentToken;
	}

	public void printCodeLine(String content) {
		String format = "%4d  %s";
		System.out.println(String.format(format, lineCounter - 1, content));
	}

	public boolean hasNextLine() {
		String s = new String();
		try {
			s = bufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (s != null) {
			codeLine = s;
			++lineCounter;
			return true;
		}
		return false;
	}

	public boolean hasNextToken() {
		if (lineCounter == 1 && column == 0) {
			hasNextLine();
			if (codeLine == null) {
				printCodeLine("");
				return false;
			} else {
				printCodeLine(codeLine);
			}
		}
		if (codeLine.substring(column).matches("\\s*")) {
			while (hasNextLine()) {
				column = 0;
				printCodeLine(codeLine);
				if (!codeLine.matches("\\s*")) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
}
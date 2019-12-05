package lexical;

import java.io.*;
import tokens.Token;
import tokens.TokenCategory;

@SuppressWarnings("unused")
public class Lexical {

	public Token previousToken;
	public int lineCounter = 1, column = 0;
	public BufferedReader bufferedReader;
	public String codeLine = "";
	
	/**
	 * @param filePath the file path
	 * @throws IOException if filePath is invalid
	 */
	public Lexical(String filePath) throws IOException {
		File file = new File(filePath);
		this.bufferedReader = new BufferedReader(new FileReader(file));
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
		String lexeme = Character.toString(codeLine.charAt(column));
		if (lexeme.equals(".")) {
			++column;
			while (column < codeLine.length() && !LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
				lexeme += nextCharacter();
				++column;
			}
			--column;
		}else if (lexeme.equals(">") || lexeme.equals("<") || lexeme.equals("!") || lexeme.equals("=")) {
			if (column + 1 < codeLine.length() && codeLine.charAt(column + 1) == '=') {
				++column;
				lexeme += nextCharacter();
			}
			category = LexemeTable.tokenMapping.get(lexeme);
		} else if (lexeme.equals(":") || lexeme.equals("&") || lexeme.equals("|")) {
			if (column < codeLine.length()) {
				++column;
				char next = codeLine.charAt(column);
				if (next == lexeme.charAt(0)) {
					lexeme += nextCharacter();
					category = LexemeTable.tokenMapping.get(lexeme);
				} else {
					if (LexemeTable.tokenMapping.get(lexeme) != null) {
						category = LexemeTable.tokenMapping.get(lexeme);
					} else {
						category = TokenCategory.unknown;
					}
				}
			}
		} else if (lexeme.equals("\"") || lexeme.equals("\'")) {
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
			} else if (column < codeLine.length() && Character.toString(codeLine.charAt(column)).matches("\\d")) { // Caso não seja float ele tem que ir pra o prox char
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
			category = LexemeTable.tokenMapping.get(lexeme);
		} else if (lexeme.matches(".")) {
			if (lexeme.matches("\\p{ASCII}")) {
				if (lexeme.equals(";")) {
					category = TokenCategory.EOL;
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
					if (category == TokenCategory.unknown && lexeme.matches("[_a-zA-Z]?[0-9_a-zA-Z]*")) {
						category = TokenCategory.id;
					}
				}
			}
		}
		Token tk = new Token(category, lineCounter, column - lexeme.length() + 2, lexeme);
		previousToken = tk;
		return tk;
	}
	
}
package lexical;

import java.io.*;
import tokens.Token;
import tokens.TokenCategory;

@SuppressWarnings("unused")
public class Lexical {

	private Token previousToken;
	private int lineCounter = 1, column = 0;
	private BufferedReader bufferedReader;
	private String codeLine = "";
	
	/**
	 * @param filePath the file path
	 * @throws IOException if filePath is invalid
	 */
	public Lexical(String filePath) throws IOException {
		File file = new File(filePath);
		this.bufferedReader = new BufferedReader(new FileReader(file));
	}

	public String nextCharacter() {
		if (column < codeLine.length()) {			
			if (codeLine.charAt(column) != ' ' && codeLine.charAt(column) != '\t' && codeLine.charAt(column) != '\n') {
				return Character.toString(codeLine.charAt(column));
			}
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
			while (!LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
				lexeme += nextCharacter();
				++column;
			}
			--column;
		} else if (lexeme.matches("\\d")) {
			++column;
			category = TokenCategory.constInt;
			lexeme += nextCharacter();
			while (Character.toString(codeLine.charAt(++column)).matches("\\d")) {  // Adiciona a lexeme enquanto for um digito
				lexeme += nextCharacter();
			}
			if (codeLine.charAt(column) == '.') {  // Verificando se EH um float
				lexeme += nextCharacter();
				++column;
				while (column < codeLine.length() && Character.toString(codeLine.charAt(column)).matches("\\d")) { // Vai pegando o valor depois do .
					category = TokenCategory.constFloat;
					lexeme += nextCharacter();
					++column;
				}
				if (category != TokenCategory.constFloat) {  // Se for por exemplo 123.
					category = TokenCategory.unknown;
					--column;
				}
			}
		} else if (lexeme.matches(".")) {
			if (lexeme.matches("\\p{ASCII}")) {
				while (column < codeLine.length()) {		// TODO: Otimizar >> Tentar fazer lendo ate um ending antes de fazer a verificacao no tokenMapping
					boolean nextChar = false;
					if (LexemeTable.tokenMapping.containsKey(lexeme)) {
						nextChar = true;
						column++;
						if (column < codeLine.length() && LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
							--column;
							break;
						}
						lexeme += nextCharacter();
					} else if (LexemeTable.tokenEndings.contains(codeLine.charAt(++column))) {
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
			}
		}

		Token tk = new Token(category, lineCounter, column - lexeme.length() + 2, lexeme);
		previousToken = tk;
		System.out.println(tk.toString());
		return tk;
	}
	
	/**
	 * @return boolean true if there is another token or false otherwise
	 */
	public boolean hasNextToken() {
		return true;
	}
	
	public void readFile() {
		try {
			while ((codeLine = bufferedReader.readLine()) != null) {
				// chamar a funcao de analisar
				String cleanString = "";
				for (column = 0; column < codeLine.length(); ++column) {
					if (codeLine.charAt(column) != ' ' && codeLine.charAt(column) != '\n' && codeLine.charAt(column) != '\t') {
						previousToken = nextToken();
					}
				}
				column = 0;
				++lineCounter;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
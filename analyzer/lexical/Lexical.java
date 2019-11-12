package lexical;

import java.io.*;
import tokens.Token;
import tokens.TokenCategory;

@SuppressWarnings("unused")
public class Lexical {

	private Token previousToken;
	private int lineCounter = 1, column = 1;
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
	
	private char nextCharacter() {
		return ++column < codeLine.length() ? codeLine.charAt(column) : '\n';  
	}

	/**
	 * @return Token the next token if there is one or null otherwise
	 */
	public Token nextToken() {
		// constFloat e constInt
		TokenCategory category = TokenCategory.unknown;
		String lexeme = Character.toString(codeLine.charAt(column));
		if (codeLine.charAt(column) == '.') {
			++column;
			while (!LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
				lexeme += Character.toString(codeLine.charAt(column));
				++column;
			}
		} else if (lexeme.matches("\\d")) {
			category = TokenCategory.constInt;
			char ch;
			for (ch = nextCharacter(); Character.toString(ch).matches("\\d"); ch = nextCharacter()) {
				lexeme += ch;
			}
			if (ch == '.') {
				lexeme += ch;
				for (ch = nextCharacter(); Character.toString(ch).matches("\\d"); ch = nextCharacter()) {
					category = TokenCategory.constFloat;
					lexeme += ch;
					// int[] a = [1, 2, 3]; //modificar os tokens arrBegin
				}
				if (category.equals(TokenCategory.constInt)) {
					category = TokenCategory.unknown;
				}
			}
			if (column < codeLine.length()) {				
				if (!LexemeTable.tokenEndings.contains(ch)) {
					lexeme += ch;
					++column;
					category = TokenCategory.unknown;
				}
			}	
		} else if (lexeme.matches(".")) {
			if (Character.toString(codeLine.charAt(column)).equals("i")) {
				lexeme += Character.toString(nextCharacter());
				if (lexeme.equals("if")) {
					category = TokenCategory.condIf;
				} else {
					lexeme += Character.toString(nextCharacter());
					if (lexeme.equals("int")) {			
						lexeme += nextCharacter();
						if (LexemeTable.tokenEndings.contains(codeLine.charAt(column))) {
							category = TokenCategory.typeInt;							
						} else {
							category = TokenCategory.id;
						}
					}
				}
//				System.out.println("LEXEME DENTRO = " + lexeme);
			} else if (Character.toString(codeLine.charAt(column)).equals("f")) {
				lexeme += Character.toString(nextCharacter());
				
			}
		}
//		System.out.println("Lexeme length = "+ lexeme.length() + " column = " + column);
		Token tk = new Token(category, lineCounter, column - lexeme.length() + 1, lexeme);
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
				System.out.println(codeLine);
				String cleanString = "";
				for (column = 0; column < codeLine.length(); ++column) {
					if (codeLine.charAt(column) != ' ' && codeLine.charAt(column) != '\t' && codeLine.charAt(column) != '\n') {
						previousToken = nextToken(); // return true aqui
					}
				}
				++lineCounter;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
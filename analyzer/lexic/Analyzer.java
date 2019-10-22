package lexic;

import java.io.*;

import token.*;

@SuppressWarnings("unused")
public class Analyzer {

	private int line, column;
	private BufferedReader bufferedReader;
	
	/**
	 * @param filePath the file path
	 * @throws IOException if filePath is invalid
	 */
	public Analyzer(String filePath) throws IOException {
		File file = new File(filePath);
		this.bufferedReader = new BufferedReader(new FileReader(file));
	}
	
	/**
	 * @return Token the next token if there is one or null otherwise
	 */
	public Token nextToken() {
		return new Token(TokenCategory.arrBool, 0, 0, "");
	}
	
	/**
	 * @return boolean true if there is another token or false otherwise
	 */
	public boolean hasNextToken() {
		return true;
	}
	
}
package tokens;

public class Token { 

	private TokenCategory tokenCategory;
	private int line, column;
	private String id;
	
	/**
	 * @param tokenCategory a token category
	 * @param line the line the token has been found
	 * @param column the column the token has been found
	 */
	public Token(TokenCategory tokenCategory, int line, int column, String id) {
		this.tokenCategory = tokenCategory; 
		this.line = line;
		this.column = column;
		this.id = id;
	}

	public TokenCategory getTokenCategory() {
		return tokenCategory;
	}

	public void setTokenCategory(TokenCategory tokenCategory) {
		this.tokenCategory = tokenCategory;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public String toString() { 
		String format = "              [%04d, %04d] (%04d, %20s) {%s}";
		return String.format(format, line, column, tokenCategory.ordinal(), tokenCategory.toString(), id);
	}
}
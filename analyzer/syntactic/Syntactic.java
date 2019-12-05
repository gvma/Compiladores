package syntactic;

import java.io.FileNotFoundException;
import java.io.IOException;

import lexical.Lexical;
import tokens.Token;
import tokens.TokenCategory;

public class Syntactic {
	private Lexical lexicalAnalyzer;
	
	public Syntactic(String[] args) {
		for (String s : args) {
			try {
				lexicalAnalyzer = new Lexical(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (lexicalAnalyzer.hasNextLine()) {
				for (lexicalAnalyzer.column = 0; lexicalAnalyzer.column < lexicalAnalyzer.codeLine.length(); ++lexicalAnalyzer.column) {
					if (lexicalAnalyzer.codeLine.charAt(lexicalAnalyzer.column) != ' ' && 
							lexicalAnalyzer.codeLine.charAt(lexicalAnalyzer.column) != '\n' &&
							lexicalAnalyzer.codeLine.charAt(lexicalAnalyzer.column) != '\t') {
						lexicalAnalyzer.previousToken = lexicalAnalyzer.nextToken();
						System.out.println(lexicalAnalyzer.currentToken.toString());
					}
				}
				lexicalAnalyzer.column = 0;
				++lexicalAnalyzer.lineCounter;
			}
			lexicalAnalyzer.codeLine = "";
		}
	}
}
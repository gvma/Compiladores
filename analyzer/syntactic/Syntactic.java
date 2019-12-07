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
			while (lexicalAnalyzer.hasNextToken()) {
				System.out.println(lexicalAnalyzer.nextToken().toString());
			}
		}
	}
}
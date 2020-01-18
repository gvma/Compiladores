package syntactic;

import java.io.IOException;

import lexical.Lexical;

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
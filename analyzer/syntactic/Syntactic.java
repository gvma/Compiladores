package syntactic;

import java.io.FileNotFoundException;
import java.io.IOException;

import lexical.Lexical;

public class Syntactic {
	private Lexical lexicalAnalyzer;
	
	public Syntactic(String[] args) {
		int count = 0;
		for (String s : args) {
			try {
				System.out.println("\nArquivo de numero " + (count++ + 1) + "\n");
				lexicalAnalyzer = new Lexical(s);
				readFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void readFile() {
		try {
			while ((lexicalAnalyzer.codeLine = lexicalAnalyzer.bufferedReader.readLine()) != null) {
				String format = "%4d  %s\n";
				System.out.printf(format, lexicalAnalyzer.lineCounter, lexicalAnalyzer.codeLine);
				for (lexicalAnalyzer.column = 0; lexicalAnalyzer.column < lexicalAnalyzer.codeLine.length(); ++lexicalAnalyzer.column) {
					if (lexicalAnalyzer.codeLine.charAt(lexicalAnalyzer.column) != ' ' && 
							lexicalAnalyzer.codeLine.charAt(lexicalAnalyzer.column) != '\n' &&
							lexicalAnalyzer.codeLine.charAt(lexicalAnalyzer.column) != '\t') {
						lexicalAnalyzer.previousToken = lexicalAnalyzer.nextToken();
						System.out.println(lexicalAnalyzer.previousToken.toString());
					}
				}
				lexicalAnalyzer.column = 0;
				++lexicalAnalyzer.lineCounter;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
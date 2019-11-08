package main;

import java.io.*;

import lexical.Lexical;
import tokens.Token;

public class Main {
	
	/**
	 * 
	 * @param args the filePath
	 */
	public static void main(String[] args) {
		String filePath = args[0];
		try {
			Lexical analyzer = new Lexical("C:/Users/guiga/Maratona/hello_world.sft");
			analyzer.readFile();
//			while (analyzer.hasNextToken()) {
//				Token token = analyzer.nextToken();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

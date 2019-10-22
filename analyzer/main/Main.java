package main;

import java.io.*;
import lexic.Analyzer;
import token.Token;

public class Main {
	
	/**
	 * 
	 * @param args the filePath
	 */
	public static void main(String[] args) {
		String filePath = args[0];
		Analyzer analyzer;
		try {
			analyzer = new Analyzer(filePath);
			while (analyzer.hasNextToken()) {
				Token token = analyzer.nextToken();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

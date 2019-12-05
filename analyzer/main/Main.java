package main;

import java.io.*;

import lexical.Lexical;
import syntactic.Syntactic;
import tokens.Token;

public class Main {
	
	/**
	 * 
	 * @param args the filePath
	 */
	public static void main(String[] args) {
		Syntactic syntacticAnalyzer = new Syntactic(args);
	}
	
}

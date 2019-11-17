package lexical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tokens.TokenCategory;

public class LexemeTable {

	public static Map<String, TokenCategory> tokenMapping = new HashMap<String, TokenCategory>();
	public static List<Character> tokenEndings = new ArrayList<Character>();
	
	static {
		tokenMapping.put("true", TokenCategory.constBool); //ok
		tokenMapping.put("false", TokenCategory.constBool); //ok
		tokenMapping.put("fun",TokenCategory.funDef); //ok
		tokenMapping.put("return",TokenCategory.funRet); //ok
		tokenMapping.put("main",TokenCategory.main); //ok
		tokenMapping.put("(",TokenCategory.paramBeg); //ok
		tokenMapping.put(")",TokenCategory.paramEnd); //ok
		tokenMapping.put(",",TokenCategory.commaSep); //not ok
		tokenMapping.put("int",TokenCategory.typeInt); // ok
		tokenMapping.put("float",TokenCategory.typeFloat); // ok
		tokenMapping.put("bool",TokenCategory.typeBool); // ok
		tokenMapping.put("char",TokenCategory.typeChar); //ok
		tokenMapping.put("void",TokenCategory.typeVoid); //ok
		tokenMapping.put("string",TokenCategory.typeStr); //ok
		tokenMapping.put("int[]",TokenCategory.arrInt); //ok
		tokenMapping.put("float[]",TokenCategory.arrFloat); //ok
		tokenMapping.put("bool[]",TokenCategory.arrBool); //ok
		tokenMapping.put("char[]",TokenCategory.arrChar); //ok
		tokenMapping.put("string[]",TokenCategory.arrString); //ok
		tokenMapping.put("{",TokenCategory.beginScope); //ok
		tokenMapping.put("}",TokenCategory.endScope); //ok
		tokenMapping.put("+",TokenCategory.opAdd); //ok
		tokenMapping.put("-",TokenCategory.opSub); //opNegative instead of opSub
		tokenMapping.put("*",TokenCategory.opMult); //ok
		tokenMapping.put("/",TokenCategory.opDiv); //ok
		tokenMapping.put("^",TokenCategory.opPow); //ok
		tokenMapping.put("%",TokenCategory.opMod); //ok
		tokenMapping.put(">",TokenCategory.opGreater); //ok
		tokenMapping.put("<",TokenCategory.opLesser); //ok
		tokenMapping.put(">=",TokenCategory.opGreq); //opGreater and opAttrib instead of opGreq
		tokenMapping.put("<=",TokenCategory.opLeq); //same
		tokenMapping.put("==",TokenCategory.opEquals); //two attribs
		tokenMapping.put("!=",TokenCategory.opNotEqual); //not and attrib
		tokenMapping.put("&&",TokenCategory.opAnd); //nope
		tokenMapping.put("||",TokenCategory.opOr); //nope
		tokenMapping.put("-",TokenCategory.opNegative); //yep but nope
		tokenMapping.put("!",TokenCategory.opNot); //ok
		tokenMapping.put("::",TokenCategory.opConcat); //nope
		tokenMapping.put("=",TokenCategory.opAttrib); //ok
		tokenMapping.put(";",TokenCategory.EOL); //ok
		tokenMapping.put("for",TokenCategory.forLoop); //ok
		tokenMapping.put("while",TokenCategory.whileLoop); //ok
		tokenMapping.put("if",TokenCategory.condIf); //ok
		tokenMapping.put("ceif",TokenCategory.condElseIf); //ta ceif poarr
		tokenMapping.put("else",TokenCategory.condElse); //ok
		tokenMapping.put("read",TokenCategory.scan); //ok
		tokenMapping.put("print",TokenCategory.print); //ok
		tokenEndings.add(';');
		tokenEndings.add(' ');
		tokenEndings.add('(');
		tokenEndings.add(')');
		tokenEndings.add('\n');
		tokenEndings.add('\t');
		tokenEndings.add('{');
		tokenEndings.add('}');
		tokenEndings.add('\'');
		tokenEndings.add('"');
		tokenEndings.add('+');
		tokenEndings.add('-');
		tokenEndings.add('/');
		tokenEndings.add('*');
		tokenEndings.add('^');
		tokenEndings.add('%');
		tokenEndings.add(':');
		tokenEndings.add('=');
		tokenEndings.add('<');
		tokenEndings.add('>');
		tokenEndings.add('|');
		tokenEndings.add('&');
		tokenEndings.add('!');
		tokenEndings.add('(');
		tokenEndings.add(')');
		tokenEndings.add(',');
	}
	
}

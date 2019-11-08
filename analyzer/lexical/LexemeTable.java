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
		tokenMapping.put("fun",TokenCategory.funDef);
		tokenMapping.put("return",TokenCategory.funRet);
		tokenMapping.put("main",TokenCategory.main);
		tokenMapping.put("(",TokenCategory.paramBeg);
		tokenMapping.put(")",TokenCategory.paramEnd);
		tokenMapping.put(",",TokenCategory.commaSep);
		tokenMapping.put("int",TokenCategory.typeInt);
		tokenMapping.put("float",TokenCategory.typeFloat);
		tokenMapping.put("bool",TokenCategory.typeBool);
		tokenMapping.put("char",TokenCategory.typeChar);
		tokenMapping.put("void",TokenCategory.typeVoid);
		tokenMapping.put("string",TokenCategory.typeStr);
		tokenMapping.put("int[]",TokenCategory.arrInt);
		tokenMapping.put("float[]",TokenCategory.arrFloat);
		tokenMapping.put("bool[]",TokenCategory.arrBool);
		tokenMapping.put("char[]",TokenCategory.arrChar);
		tokenMapping.put("string[]",TokenCategory.arrString);
		tokenMapping.put("{",TokenCategory.beginScope);
		tokenMapping.put("}",TokenCategory.endScope);
		tokenMapping.put("+",TokenCategory.opAdd);
		tokenMapping.put("-",TokenCategory.opSub);
		tokenMapping.put("*",TokenCategory.opMult);
		tokenMapping.put("/",TokenCategory.opDiv);
		tokenMapping.put("[",TokenCategory.arrBegin);
		tokenMapping.put("]",TokenCategory.arrEnd);
		tokenMapping.put("^",TokenCategory.opPow);
		tokenMapping.put("%",TokenCategory.opMod);
		tokenMapping.put(">",TokenCategory.opGreater);
		tokenMapping.put("<",TokenCategory.opLesser);
		tokenMapping.put(">=",TokenCategory.opGreq);
		tokenMapping.put("<=",TokenCategory.opLeq);
		tokenMapping.put("==",TokenCategory.opEquals);
		tokenMapping.put("!=",TokenCategory.opNotEqual);
		tokenMapping.put("&&",TokenCategory.opAnd);
		tokenMapping.put("||",TokenCategory.opOr);
		tokenMapping.put("-",TokenCategory.opNegative);
		tokenMapping.put("!",TokenCategory.opNot);
		tokenMapping.put("::",TokenCategory.opConcat);
		tokenMapping.put("=",TokenCategory.opAttrib);
		tokenMapping.put(";",TokenCategory.EOL);
		tokenMapping.put("for",TokenCategory.forLoop);
		tokenMapping.put("while",TokenCategory.whileLoop);
		tokenMapping.put("if",TokenCategory.condIf);
		tokenMapping.put("ceif",TokenCategory.condElseIf);
		tokenMapping.put("else",TokenCategory.condElse);
		tokenMapping.put("read",TokenCategory.scan);
		tokenMapping.put("print",TokenCategory.print);
//		tokenMapping.put("",TokenCategory.id);
//		tokenMapping.put("",TokenCategory.constInt);
//		tokenMapping.put("",TokenCategory.constFloat);
//		tokenMapping.put("",TokenCategory.constBool);
//		tokenMapping.put("",TokenCategory.constChar);
//		tokenMapping.put("",TokenCategory.constStr);
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
	}
	
}

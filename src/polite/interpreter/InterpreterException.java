package polite.interpreter;

import polite.interpreter.basicTypes.MyType;
import polite.parser.Token;

public class InterpreterException extends Exception {

	private static final long serialVersionUID = 5077719695677356596L;

	public static final String PRINT_FORMAT = "Line %d, %d: '%s' %s\n";

	public static InterpreterException newException(Token t, String msg) {
		if (t == null)
			return new InterpreterException("Module:" + msg);
		return new InterpreterException(String.format(PRINT_FORMAT,
				t.beginLine, t.beginColumn, t.image, msg));
	}

	public static InterpreterException parameterTypeError(int i, MyType type, String... types){
		StringBuilder sb = new StringBuilder();
		sb.append("parameter ");sb.append(i);sb.append(" type mismatch (");
		sb.append(type.toString());sb.append("): expected ");
		boolean start=true;
		for(String s:types){
			if(!start) sb.append(", ");
			else start=false;
			sb.append(s);
		}
		return new InterpreterException(sb.toString());
	}

	public InterpreterException() {
		super();
	}

	public InterpreterException(String msg) {
		super(msg);
	}

	public InterpreterException(Throwable e) {
		super(e);
	}

}

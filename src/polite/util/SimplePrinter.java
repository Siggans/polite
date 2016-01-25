package polite.util;

import java.io.PrintStream;
import java.util.Stack;

public class SimplePrinter {
	private final PrintStream out;
	private String indent;
	private Stack<String> stack = new Stack<String>();

	public SimplePrinter(PrintStream ps) {
		out = ps;
		indent = "   ";
		stack.push("");
	}

	public SimplePrinter() {
		this(System.out);
	}

	public void setIndentSpace(String s) {
		indent = s;
	}

	public void toNextIndent() {
		stack.push(stack.peek() + indent);
	}

	public void toNextIndent(String s) {
		if (s == null || s.length() != indent.length())
			throw new RuntimeException(
					"Expecting same length custom indent input");
		stack.push(stack.peek() + s);
	}

	public void toPrevIndent() {
		if (stack.peek().equals(""))
			return;
		stack.pop();
	}

	public void println(String s) {
		out.print(stack.peek() + s + "\n");
	}

}

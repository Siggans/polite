package polite.abstractLanguage;

import java.io.PrintStream;
import java.util.List;

import polite.interpreter.basicTypes.BaseType;
import polite.parser.ParseException;
import polite.parser.Token;
import polite.util.Logger;
import polite.util.SimplePrinter;


public class Node {
	protected static SimplePrinter sp;
	protected static final String DEFAULT_INDENT = "    ";
	static {
		setOutput(System.out);
	}

	public static void setOutput(PrintStream ps) {
		sp = new SimplePrinter(ps);
		sp.setIndentSpace(DEFAULT_INDENT);
	}


	// //////////////////////////////////////////
	// Node Helper classes
	public static class NodeList {
		private static Logger logger = Logger.getClassLogger(NodeList.class);

		public static int countList(Node n) {
			int i = 0;
			while (n != null) {
				i++;
				n = n.getSibling();
			}
			return i;
		}

		public static int countList(NodeList node) {
			if (node == null)
				return 0;
			return countList(node.getHead());
		}

		Node head;
		Node tail;

		public NodeList() {
		}

		public Node getHead() {
			return head;
		}

		public void addNode(Node node) {
			Logger ml = logger.getMethodLogger("addNode");
			ml.enter();
			if (tail == null) {
				ml.log("first node: " + node);
				head = node;
				tail = node;
			} else {
				ml.log("appending " + node);
				tail.setSibling(node);
				tail = node;
			}
			while (tail.getSibling() != null) {
				ml.log("moving pass sibling: " + tail);
				tail = tail.getSibling();
			}

			ml.log("tail is: " + tail);
			ml.leave();
		}

		public String toString() {
			String res = "";
			tail = head;
			while (tail != head) {
				if (tail != head)
					res += ", ";
				res += tail;
			}
			return res;
		}

		public int size() {
			return NodeList.countList(this);
		}
	}

	public static class BlockNode extends Node {
		protected final String childName1;
		protected String childName2;
		protected final String childName3;
		private Node child2;
		private Node currentChild2;
		private Node child3;
		private Node currentChild3;
		protected SymbolTable st;

		protected BlockNode(Token t, NodeType n, String name1, String name3) {
			super(t, n);
			childName1 = name1;
			childName3 = name3;
			childName2 = "Statements";
			st = null;
		}

		protected void setChild2Name(String name) {
			childName2 = name;
		}

		public String toString() {
			return "BlockNode should not be called";
		}

		public void addStatement(Node statment) throws ParseException {
			logger.getMethodLogger("addStatement").log("redirect getChild2");
			addChild2(statment);
		}

		public Node getStatementsStart() {
			return getChild2();
		}

		public static String indentation = "  | ";

		public void print() {
			printSelf();
			if (st != null) {
				printChildName("Symbol Table");
				List<SymbolTable.Pair> l = st.getTable();
				sp.toNextIndent();
				for (SymbolTable.Pair p : l)
					sp.println(p.type + ": " + p.name);
				sp.toPrevIndent();
			}
			if (childName1 != null) {
				printChildName(childName1);
				printIndented(getChild(), indentation);
			}
			printChildName(childName2);
			printIndented(getStatementsStart(), indentation);
			if (childName3 != null) {
				printChildName(childName3);
				printIndented(getChild3(), indentation);
			}
			printNext();
		}

		protected void printChildName(String name) {
			sp.println(" +" + name + ":");
		}

		protected void addChild2(Node child) {

			if (child == null) {
				logger.getMethodLogger("addChild").log(
						"null detected!\n" + "\tAdding to " + getToken().image
								+ "on (" + getToken().beginLine + ", "
								+ getToken().beginColumn + ")");
				return;
			}
			if (currentChild2 == null) {
				logger.getMethodLogger("addChild2").log("new child " + child);
				this.child2 = child;
				currentChild2 = this.child2;
			} else {
				logger.getMethodLogger("addChild2").log("next child " + child);
				currentChild2.setSibling(child);
				currentChild2 = currentChild2.getSibling();
			}
			currentChild2.setParent(this);
			while (currentChild2.getSibling() != null) {
				currentChild2 = currentChild2.getSibling();
				currentChild2.setParent(this);
			}
		}

		protected Node getChild2() {
			return child2;
		}

		protected void addChild3(Node child) {
			if (child == null) {
				logger.getMethodLogger("addChild3").log(
						"null detected!\n" + "\tAdding to " + getToken().image
								+ "on (" + getToken().beginLine + ", "
								+ getToken().beginColumn + ")");
				return;
			}
			if (currentChild3 == null) {
				logger.getMethodLogger("addChild3").log("new child " + child);
				this.child3 = child;
				currentChild3 = this.child3;
			} else {
				logger.getMethodLogger("addChild3").log("next child " + child);
				currentChild3.setSibling(child);
				currentChild3 = currentChild3.getSibling();
			}
			currentChild3.setParent(this);
			while (currentChild3.getSibling() != null) {
				logger.getMethodLogger("addChild3").log(
						"next child on list " + child);
				currentChild3 = currentChild3.getSibling();
				currentChild3.setParent(this);
			}
		}

		protected Node getChild3() {
			return child3;
		}

		public boolean isValid(Node parent) {
			Logger ml = logger.getMethodLogger("isValid");
			ml.enter();
			if (!super.isValid(parent)) {
				ml.log("failed parent check");
				return false;
			}
			Node v = this.getChild2();
			while (v != null) {
				if (!v.isValid(this))
					return false;
				v = v.getSibling();
			}
			v = this.getChild3();
			while (v != null) {
				if (!v.isValid(this))
					return false;
				v = v.getSibling();
			}
			ml.leave();
			return true;
		}

		public SymbolTable initializeSymbolTable(SymbolTable st) {
			Logger ml = logger.getMethodLogger("initializeSymbolTable");
			if (st == null) {
				ml.log("global symbol table created");
				this.st = new SymbolTable();
			} else {
				ml.log("local symbol table created");
				this.st = new SymbolTable(st);
			}
			return this.st;
		}

		public SymbolTable getSymbolTable() {
			return this.st;
		}

	}

	// ////////////////////////////////////////////
	// atom type nodes here, i.e. we aren't making block out of these

	public static class Identifier extends Node {
		private final String name;

		public Identifier(Token t, String name) {
			super(t, NodeType.IDENTIFIER);
			this.name = name;
		}

		public Identifier(Token t, Operation op) {
			this(t, op.toString());
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return this.type + ": " + this.name;
		}
	}

	public static class Literal extends Node {
		final BaseType baseType;
		final String value;

		public Literal(Token t, BaseType bt) {
			super(t, NodeType.LITERAL);
			baseType = bt;
			String value = t.image;
			if (bt == BaseType.STRING) {
				if(value.length()>=6){
					if(value.startsWith("\"\"\"")||value.startsWith("'''")){
						value=value.replace(value.substring(0,3),"");
					} else if(value.startsWith("\"")|| value.startsWith("'"))
						value=value.replace(value.substring(0, 1), "");
				} else if(value.startsWith("\"")|| value.startsWith("'"))
					value=value.replace(value.substring(0, 1), "");
			}
			this.value=value;
		}

		protected Literal(Token t, String string) {
			super(t, NodeType.LITERAL);
			baseType = BaseType.STRING;
			value = string;
		}

		public String toString() {
			return this.getType() + ": " + this.baseType + "(" + value + ")";
		}

		public BaseType getLiteralType() {
			return baseType;
		}
		public String getValue(){
			return value;
		}
	}

	public static class NOOP extends Node {
		public NOOP(Token t) {
			super(t, NodeType.NOOP);
		}

		public String toString() {
			return this.getType().toString();
		}

		public boolean isValid(Node parent) {
			Logger ml = logger.getMethodLogger("isValid");
			ml.enter();
			if (getParent() != parent) {
				ml.log("failed parent check");
				return false;
			}
			if (getChild() != null) {
				ml.log("failed child check");
				return false;
			}
			ml.leave();
			return true;
		}
	}

	public static class Assign extends Node {
		private final String identName;

		public Assign(Token t, String identName, Node value) {
			super(t, NodeType.ASSIGN);
			this.identName = identName;
			this.addChild(value);
		}

		public String toString() {
			return this.getType() + ": " + getIdentifierName();
		}

		public String getIdentifierName() {
			return identName;
		}

		public Node getValue() {
			return getChild();
		}

	}

	public static class Global extends Node {
		public Global(Token t, NodeList prints) {
			super(t, NodeType.GLOBAL);
			addChild(prints.getHead());
		}

		public Node getList() {
			return getChild();
		}

		public String toString() {
			return this.type + ": ";
		}
	}

	public static class Print extends Node {
		boolean println;

		public Print(Token t, NodeList prints, Token comma) {
			super(t, NodeType.PRINT);
			if (prints != null)
				addChild(prints.getHead());
			println = comma == null;
		}

		public Node getList() {
			return getChild();
		}

		public boolean isPrintEOL() {
			return println;
		}

		public String toString() {
			return this.type + ": " + (println ? " (\\n)" : " (cont)");
		}
	}

	public static class Return extends Node {
		public Return(Token t, Node expr) {
			super(t, NodeType.RETURN);
			addChild(expr);
		}

		public Node getExpr() {
			return getChild();
		}

		public String toString() {
			return this.type + ": "
					+ (getChild() == null ? "None" : getChild());
		}
	}

	public static class Yield extends Node {
		public Yield(Token t, Node expr) {
			super(t, NodeType.YIELD);
			addChild(expr);
		}

		public Node getExpr() {
			return getChild();
		}

		public String toString() {
			return this.type + ": " + getChild();
		}
	}

	// ////////////////////////////
	// Fun starts here

	public static class Module extends BlockNode {
		protected String name;

		protected Module(NodeType type) {
			super(null, type, null, null);
		}

		public Module(String moduleName) {
			this(NodeType.MODULE);
			this.name = moduleName;
		}

		public String getModuleName() {
			return name;
		}

		public String toString() {
			return this.getType() + ": " + name;
		}
	}

	public static class FunDef extends BlockNode {
		private String name;

		public FunDef(Token t, NodeList params, SymbolTable st)
				throws ParseException {
			super(t, NodeType.LAMBDA, "paramList", null);
			setName("@lambda");
			if (params != null)
				this.addChild(params.getHead());
			initializeSymbolTable(st);
			addParamToSymbolTable();
		}

		public FunDef(Token t, String name, NodeList params, SymbolTable st)
				throws ParseException {
			super(t, NodeType.FUNDEF, "paramList", null);
			setName(name); // this is the name we are saving to symbol table
			if (params != null)
				this.addChild(params.getHead());
			initializeSymbolTable(st);
			addParamToSymbolTable();
		}

		private void addParamToSymbolTable() throws ParseException {
			Node n = getParamsList();
			while (n != null) {
				if (st.hasVariable(n, false))
					throw SymbolTable.getScopeError(n.getToken(), "already declared");
				if (!(st.addVariable(n)) || !st.testAssign(n)) {
					throw SymbolTable.getScopeError(n.getToken(),
							"cannot add variable to symbol table");
				}
				n = n.getSibling();
			}
		}

		private void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public Node getParamsList() {
			return getChild();
		}

		private void setType(NodeType type) {
			this.type = type;
		}

		public void changeToGenerator() throws ParseException {
			setType(NodeType.GENERATOR);
			if (getParamsList() != null)
				throw new ParseException(String.format("Line %d, %d: '%s'",
						getToken().beginLine, getToken().beginColumn,
						getToken().image)
						+ " generator cannot contain arguments");
			for (Node n = getStatementsStart(); n != null; n = n.getSibling()) {
				if (n instanceof FunDef)
					throw new ParseException(
							String.format("Line %d, %d: '%s'",
									n.getToken().beginLine,
									n.getToken().beginColumn,
									n.getToken().image)
									+ " generator cannot contain new function declaration");
			}
		}

		public void addStatement(Node statement) throws ParseException {
			if (this.getType() == NodeType.GENERATOR) {
				for (Node n = statement; n != null; n = n.getSibling())
					if (n instanceof FunDef)
						throw new ParseException(
								String.format("Line %d, %d: '%s'",
										n.getToken().beginLine,
										n.getToken().beginColumn,
										n.getToken().image)
										+ " generator cannot contain new function declaration");
			}
			super.addStatement(statement);
		}

		public String toString() {
			return this.getType() + ": " + this.name;
		}
	}

	public static class IfElse extends BlockNode {
		protected Node Condition;

		public IfElse(Token t, Node cond) {
			super(t, NodeType.IF, "condition", "false");
			this.setChild2Name("true");
			this.addChild(cond);
		}

		public Node getTrue() {
			return getStatementsStart();
		}

		public Node getFalse() {
			return getChild3();
		}

		public BlockNode getIfBlockHelper() {
			return this;
		}

		public BlockNode getElseBlockHelper() {
			return new ElseBlockHelper();
		}

		private class ElseBlockHelper extends BlockNode {
			protected ElseBlockHelper() {
				super(null, null, null, null);
			}

			public void addStatement(Node statement) {
				IfElse.this.addChild3(statement);
			}

			public Node getStatementsStart() {
				return IfElse.this.getChild3();
			}

			public Node getParent() {
				return IfElse.this.getParent();
			}
		}

		public String toString() {
			return this.type + ":";
		}
	}

	public static class While extends BlockNode {
		public While(Token t, Node cond) {
			super(t, NodeType.WHILE, "condition", null);
			this.addChild(cond);
		}

		public Node getCondition() {
			return getChild();
		}

		public String toString() {
			return this.type + ":";
		}

	}

	public static class For extends BlockNode {
		public For(Token t, Node generator) {
			super(t, NodeType.FOR, "codition", null);
			this.addChild(generator);
		}

		public Node getGeneratorToValue() {
			return getChild();
		}

		public String toString() {
			return this.type + ":";
		}

	}

	public static class InGenerator extends BlockNode {
		public InGenerator(Token t, Node varName, Node generator) {
			super(t, NodeType.INGENERATOR, "variable", null);
			this.setChild2Name("generator");
			this.addChild(varName);
			this.addChild2(generator);
		}

		public Node getVariable() {
			return getChild();
		}

		public Node getGenerator() {
			return getStatementsStart();
		}

		public String toString() {
			return this.type + ":";
		}

	}

	public static class Call extends BlockNode {
		public static Call setAttr(Token t, Node obj, Identifier id, Node expr) {
			Literal str = new Literal(id.getToken(), BaseType.STRING);
			NodeList nl = new NodeList();
			nl.addNode(str);
			nl.addNode(expr);
			return new Call(t, Operation.SETATTR, obj, nl.getHead());
		}

		public Call(Node functionNode, NodeList paramList) {
			super(functionNode.getToken(), NodeType.CALL, "functionObj", null);
			this.setChild2Name("paramList");
			this.addChild(functionNode);
			if (paramList != null)
				this.addChild2(paramList.getHead());
		}

		public Call(Token t, Operation op, Node node1, Node node2) {
			super(t, NodeType.CALL, "functionObj", null);
			this.setChild2Name("paramList");
			if (op == Operation.GETATTR) {
				NodeList nl = new NodeList();
				nl.addNode(node1);
				nl.addNode(node2);
				addChild(new Identifier(t, "send"));
				addChild2(nl.getHead());
			} else {
				node1 = new Call(t, Operation.GETATTR, node1, new Literal(t, op
						.toString()));
				this.addChild(node1);
				this.addChild2(node2);
			}
		}

		public Node getFunctionNode() {
			return getChild();
		}

		public Node getParamList() {
			return getStatementsStart();
		}

		public String toString() {
			return this.type + ":";
		}
	}

	// Instance data
	protected final Logger logger = Logger.getClassLogger(this.getClass());
	private Node parent;
	private Node sibling;
	private Node child;
	private Node currentChild;
	private final Token token;

	protected NodeType type;

	protected Node(Token t, NodeType type) {
		logger.getMethodLogger("Node").log("Creating " + type);
		this.type = type;
		this.token = t;
		parent = sibling = child = null;
	}

	protected void setParent(Node parent) {
		logger.getMethodLogger("setParent").log("new parent " + parent);
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}

	protected void setSibling(Node sibling) {
		logger.getMethodLogger("setSibling").log("new sibling " + sibling);
		this.sibling = sibling;
	}

	public Node getSibling() {
		return sibling;
	}

	protected void addChild(Node child) {
		if (child == null) {
			logger.getMethodLogger("addChild").log(
					"null detected!\n" + "\tAdding to " + getToken().image
							+ " on (" + getToken().beginLine + ", "
							+ getToken().beginColumn + ")");
			return;
		}
		if (currentChild == null) {
			logger.getMethodLogger("addChild").log("new child " + child);
			this.child = child;
			currentChild = this.child;
		} else {
			logger.getMethodLogger("addChild").log("next child " + child);
			currentChild.setSibling(child);
			currentChild = currentChild.getSibling();
		}
		currentChild.setParent(this);
		while (currentChild.getSibling() != null) {
			currentChild = currentChild.getSibling();
			currentChild.setParent(this);
		}
	}

	public Node getChild() {
		return child;
	}

	public void print() {
		printSelf();
		printIndented(getChild());
		printNext();

	}

	protected void printSelf() {
		sp.println(this.toString());
	}

	protected void printIndented(Node node) {
		printIndented(node, DEFAULT_INDENT);
	}

	protected void printIndented(Node node, String s) {
		if (node != null) {
			sp.toNextIndent(s);
			node.print();
			sp.toPrevIndent();
		}
	}

	protected void printNext() {
		if (getSibling() != null)
			getSibling().print();
	}

	public Token getToken() {
		return token;
	}

	public NodeType getType() {
		return type;
	}

	public boolean isValid(Node node) {
		Logger ml = logger.getMethodLogger("isValid");
		ml.enter();
		if (this.getParent() != node) {
			ml.log("failed parent check");
			ml.leave();
			return false;
		}
		Node v = this.getChild();
		while (v != null) {
			if (!v.isValid(this))
				return false;
			v = v.getSibling();
		}
		ml.leave();
		return true;
	}

}

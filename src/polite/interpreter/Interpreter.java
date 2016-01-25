package polite.interpreter;

import java.util.LinkedList;
import java.util.List;

import polite.abstractLanguage.AbstractSyntaxTree;
import polite.abstractLanguage.Node;
import polite.abstractLanguage.SymbolTable;
import polite.abstractLanguage.Node.Assign;
import polite.abstractLanguage.Node.Call;
import polite.abstractLanguage.Node.FunDef;
import polite.abstractLanguage.Node.Identifier;
import polite.abstractLanguage.Node.IfElse;
import polite.abstractLanguage.Node.Literal;
import polite.abstractLanguage.Node.Module;
import polite.abstractLanguage.Node.NodeList;
import polite.abstractLanguage.Node.Print;
import polite.abstractLanguage.Node.Return;
import polite.abstractLanguage.Node.While;
import polite.interpreter.basicTypes.BuiltInFunctions;
import polite.interpreter.basicTypes.MyConstant;
import polite.interpreter.basicTypes.MyFunction;
import polite.interpreter.basicTypes.MyInteger;
import polite.interpreter.basicTypes.MyObj;
import polite.interpreter.basicTypes.MyReal;
import polite.interpreter.basicTypes.MyString;
import polite.interpreter.basicTypes.BuiltInFunctions.BuiltInFunction;
import polite.parser.Token;
import polite.util.Logger;


public class Interpreter {
	private static final Logger cl = Logger.getClassLogger(Interpreter.class);

	private Interpreter() {
	};

	public static final MyObj run(AbstractSyntaxTree ast) {
		try {
			return runModule(ast.getMainModule());
		} catch (InterpreterException e) {
			e.printStackTrace();
			return MyConstant.None;
		}
	}

	public static final MyObj runModule(Module mf) throws InterpreterException {
		MyFunction m = new MyFunction(mf);
		return executeModule(m);
	}

	protected static final MyObj executeModule(MyFunction m)
			throws InterpreterException {
		return executeFunction(m, null, new LinkedList<MyObj>(), m
				.getSymbolTable(),false);
	}

	protected static final MyObj runIdentifier(Identifier id, SymbolTable st)
			throws InterpreterException {
		try {
			return st.query(id.getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw InterpreterException.newException(id.getToken(), e
					.getMessage());
		}
	}

	private static List<MyObj> generateParamList(int functionsize, Call c,
			SymbolTable st) throws InterpreterException {
		LinkedList<MyObj> ll = new LinkedList<MyObj>();
		int nodesize = NodeList.countList(c.getParamList());
		if (nodesize != functionsize)
			try {
				if (nodesize + 1 == functionsize
						&& st.query(SymbolTable.INSTANCE_NAME) != null) {
					ll.add(st.query(SymbolTable.INSTANCE_NAME));
				} else if (functionsize == -1) {
					// take every param
				} else {
					throw InterpreterException.newException(c.getToken(),
							"bad parameter size");
				}
			} catch (Exception e) {
				if (e instanceof InterpreterException)
					throw (InterpreterException) e;
				e.printStackTrace();
				throw new InterpreterException(
						"unrecoverable error while processing memory");
			}
		Node pl = c.getParamList();
		while (pl != null) {
			ll.add(runExpression(pl, st));
			pl = pl.getSibling();
		}
		return ll;
	}
	
	public static MyObj executeFunction(MyFunction mf, Token token,
			List<MyObj> ll, SymbolTable st) throws InterpreterException {
		return executeFunction(mf,token,ll,st,true);
	}

	protected static final MyObj executeFunction(MyFunction mf, Token t,
			List<MyObj> ll, SymbolTable st, boolean saveInstance) throws InterpreterException {
		MyObj result = MyConstant.None;
		MyObj instance = null;
		try {
			if(saveInstance)
				st.assign(SymbolTable.INSTANCE_NAME, null);
			if(mf instanceof BuiltInFunction){
				BuiltInFunction bf = (BuiltInFunction) mf;
				result = bf.invoke(t, ll, st);
				if (bf == st.query("send")) {
					instance = ll.get(0);
				}
			} else {
				mf.getSymbolTable().activateNewRecord(t, st.getCurrentRecord());
				mf.setParameters(ll);
				result = runStatements(mf.getStatements(), mf.getSymbolTable());
				mf.getSymbolTable().removeRecord();
			}
			if(saveInstance)
				st.assign(SymbolTable.INSTANCE_NAME, instance);
		} catch (Exception e) {
			if (e instanceof InterpreterException)
				throw (InterpreterException) e;
			e.printStackTrace();
			throw InterpreterException.newException(t, "internal error");
		}
		return result;

	}

	protected static final MyObj runCall(Node n, SymbolTable st)
			throws InterpreterException {
		Call c = (Call) n;
		MyObj obj = runExpression(c.getFunctionNode(), st);
		if (obj instanceof MyFunction) {
			MyFunction mf = (MyFunction) obj;
			List<MyObj> ll = generateParamList(mf.getParamSize(), c, st);
			return executeFunction(mf, c.getToken(), ll, st);
		} else {
			throw InterpreterException.newException(c.getToken(), obj.getType()+" is not a function object");
		}
	}

	
	protected static final MyObj runExpression(Node n, SymbolTable st)
			throws InterpreterException {
		Logger ml = cl.getMethodLogger("runExpression");
		ml.enter();
		ml.log("Processing node: " + n);
		switch (n.getType()) {
		case CALL:
			return runCall(n, st);
		case LITERAL:
			Literal l = (Literal) n;
			switch (l.getLiteralType()) {
			case INTEGER:
				return new MyInteger(l.getValue());
			case STRING:
				return new MyString(l.getValue());
			case FLOAT:
				return new MyReal(l.getValue());
			default:
				throw InterpreterException.newException(l.getToken(),
						"has an unsupport literal type " + l.getLiteralType());
			}
		case IDENTIFIER:
			return runIdentifier((Identifier) n, st);
		case LAMBDA:
			FunDef fd = (FunDef) n;
			return new MyFunction(fd, st);
		default:
			throw InterpreterException.newException(n.getToken(),
					"has unsupported runtime node type " + n.getType());
		}
	}

	protected static final boolean runStatement(Node n, SymbolTable st)
			throws InterpreterException {
		Logger ml = cl.getMethodLogger("runStatement");
		ml.enter();
		ml.log("Processing node: " + n);
		switch (n.getType()) {
		case NOOP:case GLOBAL:break;
		case ASSIGN:
			Assign ass = (Assign) n;
			MyObj value = runExpression(ass.getValue(), st);
			try {
				st.assign(ass.getIdentifierName(), value);
			} catch (Exception e) {
				e.printStackTrace();
				throw InterpreterException.newException(ass.getToken(), e
						.getMessage());
			}
			break;
		case PRINT:
			Print pr = (Print) n;
			Node c = pr.getList();
			while (c != null) {
				System.out.print(runExpression(c, st));
				System.out.print(" ");
				c = c.getSibling();
			}
			if (pr.isPrintEOL())
				System.out.print("\n");
			break;
		case RETURN:
			Return rt = (Return) n;
			if (rt.getExpr() == null)
				return true;
			MyObj expr = runExpression(rt.getExpr(), st);
			try {
				st.assign(SymbolTable.RETURN_NAME, expr);
			} catch (Exception e) {
				e.printStackTrace();
				throw InterpreterException.newException(rt.getToken(),
						"failure to save return value");
			}
			return true;

		case CALL:
			runCall(n, st);
			break;
		case FUNDEF:
			FunDef fd = (FunDef) n;
			MyFunction myf = new MyFunction(fd, st);
			try {
				st.assign(fd.getName(), myf);
				myf.saveReferences(st);
			} catch (Exception e) {
				e.printStackTrace();
				throw InterpreterException.newException(fd.getToken(),
						"failure to save " + fd.getName());
			}
			break;
		case IF:
			IfElse f = (IfElse) n;
			MyObj condif = runExpression(f.getChild(), st);
			LinkedList<MyObj> llif = new LinkedList<MyObj>();
			llif.add(condif);
			if (BuiltInFunctions.bool(f.getToken(), llif, st).__notzero()) {
				return runSubStatements(f.getTrue(), st);
			} else {
				return runSubStatements(f.getFalse(), st);
			}
		case WHILE:
			While w = (While) n;
			MyObj condw = runExpression(w.getCondition(), st);
			LinkedList<MyObj> llw = new LinkedList<MyObj>();
			llw.add(condw);

			while (BuiltInFunctions.bool(w.getToken(), llw, st).__notzero()) {
				if (runSubStatements(w.getStatementsStart(), st))
					return true;
				condw = runExpression(w.getCondition(), st);
				llw.clear();
				llw.add(condw);
			}
			break;
		case YIELD: // disabled
		default:
			throw InterpreterException.newException(n.getToken(),
					"has unsupported runtime node type " + n.getType());
		}
		return false;
	}

	final protected static boolean runSubStatements(Node n, SymbolTable st)
			throws InterpreterException {
		while (n != null) {
			if (runStatement(n, st))
				return true;
			n = n.getSibling();
		}
		return false;

	}

	final protected static MyObj runStatements(Node n, SymbolTable st)
			throws InterpreterException {
		runSubStatements(n, st);
		try {
			return st.query(SymbolTable.RETURN_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InterpreterException(
					"unrecoverable error while processing memory");
		}
	}
}

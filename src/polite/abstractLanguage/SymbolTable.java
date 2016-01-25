package polite.abstractLanguage;

import java.util.ArrayList;
import java.util.Map;

import polite.abstractLanguage.Node.Identifier;
import polite.interpreter.RecordManager;
import polite.interpreter.RecordManager.ActivationRecord;
import polite.interpreter.basicTypes.BuiltInFunctions;
import polite.interpreter.basicTypes.MyObj;
import polite.parser.ParseException;
import polite.parser.Token;
import polite.util.Logger;


public class SymbolTable {
	public static final String RETURN_NAME="@RETURN";
	public static final String INSTANCE_NAME= "@INSTANCE";
	public static Map<String, MyObj> Functions;

	public static enum SymType {
		GLOBAL, // a global variable that can be assigned
		NORMAL, // Normal stance
		READONLY, // cannot be written to
		REFERENCEONLY, // a global variable that can only be read
		NOTASSIGNED; // not initialized value
	}

	public static ParseException getScopeError(Token t, String msg) {
		return new ParseException("Line " + t.beginLine + ", " + t.beginColumn
				+ ": identifier '" + t.image + "' " + msg);
	}

	protected static final Pair[] DEFAULT_GLOBAL = {
			new Pair(SymType.READONLY, "None"),
			new Pair(SymType.READONLY, "True"),
			new Pair(SymType.READONLY, "False"),
			new Pair(SymType.READONLY, "send"),
			new Pair(SymType.READONLY, "int"),
			new Pair(SymType.READONLY, "float"),
			new Pair(SymType.READONLY, "str"),
			new Pair(SymType.READONLY, "list"),
			new Pair(SymType.READONLY, "tuple"),
			new Pair(SymType.READONLY, "bool"),
			new Pair(SymType.READONLY, "str"),
			new Pair(SymType.READONLY, "len"),
			new Pair(SymType.READONLY, "super"),
			new Pair(SymType.READONLY, "type"),
			new Pair(SymType.READONLY, "dir"),
			new Pair(SymType.READONLY, "object"),};

	protected static void addDefaultGlobalValues(SymbolTable st) {
		for (Pair p : DEFAULT_GLOBAL) {
			st.tb.add(new Pair(p));
		}
	}

	public static class Pair {
		public SymType type;
		public String name;

		public Pair(SymType type, String n) {
			this.type = type;
			this.name = n;
			if (n == null)
				throw new RuntimeException("String value cannot be null");
		}

		public Pair(Pair p) {
			this(p.type, p.name);
		}
	}

	protected final Logger logger = Logger.getClassLogger(this.getClass());
	private final SymbolTable parent;
	private ArrayList<Pair> tb = new ArrayList<Pair>();
	private RecordManager rm = null;
	private boolean isGenerator;

	public SymbolTable(SymbolTable parent) {
		this.parent = parent;
		if (parent == null)
			throw new RuntimeException(
					"Cannot create two copies of base symbol table");
	}

	public SymbolTable() {
		this.parent = null;
		addDefaultGlobalValues(this);
		RecordManager.setGlobalST(this);
	}

	public void startMemoryManagement(boolean isGenerator) {
		if (rm == null) {
			rm = new RecordManager(this, isGenerator);
			this.isGenerator = isGenerator;
		}
	}

	public ActivationRecord activateNewRecord(Token t,
			ActivationRecord currentRecord) throws Exception {
		if (isGenerator)
			return rm.activateAsGenerator(t, currentRecord);
		if(this.getParent()==null){
			rm.activateRecord(t, currentRecord);
			BuiltInFunctions.addDefaultGlobalFunctions(this);
			return rm.getCurrentRecord();
		}
		return rm.activateRecord(t, currentRecord);
	}

	public void assign(String name, MyObj newmo) throws Exception {
		if (isGenerator)
			throw new Exception("Generator memory is managed by interperter");
		rm.assign(name, newmo);
	}

	public MyObj query(String name) throws Exception {
		Logger ml = logger.getMethodLogger("hasVariable");
		ml.log("looking for value of "+name);
		if (isGenerator)
			throw new Exception("Generator memory is managed by interperter");
		if(rm!=null&&getCurrentRecord()!=null&&getCurrentRecord().hasVariable(name)){
			MyObj query=rm.query(name);
			if(query==null && !name.equals(SymbolTable.INSTANCE_NAME)){ 
				if(getParent()==null)
					return query;
				query=getParent().query(name);
			} else return query;
			if(query==null) throw new Exception("null value exception on"+ name);
			getCurrentRecord().set(name, query);
			return query;
		}
		if(getParent()==null)
			throw new Exception("Variable '"+name+"' not found");
		return getParent().query(name);
	}

	private Pair getPair(String s) {
		for (int i = 0; i < tb.size(); i++) {
			if (tb.get(i).name.equals(s)) {
				return tb.get(i);
			}
		}
		return null;
	}

	public boolean hasVariable(String s, boolean searchParent) {
		Logger ml = logger.getMethodLogger("hasVariable");
		ml.log("looking for (" + s + ")");
		if (getPair(s) != null) {
			ml.log("found");
			return true;
		}
		ml.log("not found");
		if (searchParent && getParent() != null) {
			ml.log("searching parent");
			boolean found = getParent().hasVariable(s, searchParent);
			if (found) {
				ml.log("adding (" + s
						+ ") to local symbol table as REFERENCEONLY");
				return addVariable(SymType.REFERENCEONLY, s);

			}
		}
		return false;
	}

	public boolean hasVariable(Node id, boolean searchParent) {
		return hasVariable(((Identifier) id).getName(), searchParent);
	}

	public boolean addVariable(SymType st, String s) {
		Logger ml = logger.getMethodLogger("addVariable");
		if (hasVariable(s, false))
			return false;
		ml.log("adding (" + s + ") as " + st);
		Pair p = new Pair(st, s);
		tb.add(p);
		return true;
	}

	public boolean addVariable(String s) {
		return addVariable(SymType.NOTASSIGNED, s);
	}

	public boolean addVariable(SymType st, Node n) {
		if (n instanceof Identifier)
			return addVariable(st, ((Identifier) n).getName());
		return false;
	}

	public boolean addVariable(Node n) {
		return addVariable(SymType.NOTASSIGNED, n);
	}

	public void addGlobal(Node values) throws ParseException {
		Node n = values;
		if (getParent() != null) {
			SymbolTable global_st = getParent();
			while (global_st.getParent() != null) {
				global_st = global_st.getParent();
			}
			while (n != null) {
				if (!(n instanceof Identifier))
					throw getScopeError(n.getToken(), "wrong node type: " + n);
				if (hasVariable(n, false))
					throw getScopeError(n.getToken(),
							"a declared local variable");
				if (!global_st.hasVariable(n, false))
					throw getScopeError(n.getToken(),
							"global variable reference not available");
				if (global_st.getSymType(n) == SymType.READONLY)
					throw getScopeError(n.getToken(),
							"read only variable, cannot assign value");

				addVariable(SymType.GLOBAL, n);
				n = n.getSibling();
			}
		} else {
			while (n != null) {
				if (!(n instanceof Identifier))
					throw getScopeError(n.getToken(), "wrong node type: " + n);
				if (!hasVariable(n, false))
					throw getScopeError(n.getToken(),
							"variable not yet declared");
				n = n.getSibling();
			}
		}
	}

	public SymbolTable getParent() {
		return parent;
	}

	protected SymType getSymType(String s) {
		for (Pair p : tb) {
			if (p.name.equals(s))
				return p.type;
		}
		return null;
	}

	public SymType getSymType(Node s) {
		if (s instanceof Identifier)
			return getSymType(((Identifier) s).getName());
		return null;
	}

	public boolean testAssign(Node s) {
		SymType st;
		if ((st = getSymType(s)) != null) {
			if (st == SymType.READONLY || st == SymType.REFERENCEONLY)
				return false;
			else if (st == SymType.GLOBAL){
				SymbolTable global= this;
				while(global.getParent()!=null)
					global=global.getParent();
				return global.testAssign(s);
			}
			else if (st == SymType.NOTASSIGNED)
				updateSymType(SymType.NORMAL, s);
			return true;
		}
		return false;
	}

	private void updateSymType(SymType st, String s) {
		getPair(s).type = st;
	}

	private void updateSymType(SymType st, Node s) {
		updateSymType(st, ((Identifier) s).getName());
	}

	public boolean testQuery(Node s) {
		SymType st;
		if ((st = getSymType(s)) != null) {
			if (st == SymType.NOTASSIGNED)
				return false;
			return true;
		} else {
			if (getParent() != null && getParent().testQuery(s)) {
				try {
					addVariable(SymType.REFERENCEONLY, s);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Should not reach here");
				}
				return true;
			}
		}
		return false;
	}

	public ArrayList<Pair> getTable() {
		ArrayList<Pair> res = new ArrayList<Pair>();
		for (Pair p : tb) {
			res.add(new Pair(p));
		}
		return res;
	}

	public RecordManager getMemoryManager(){
		return rm;
	}
	public ActivationRecord getCurrentRecord(){
		return rm.getCurrentRecord();
	}

	public void removeRecord() throws Exception{
		rm.removeRecord();
		
	}
}

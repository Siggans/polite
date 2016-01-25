package polite.interpreter;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import polite.abstractLanguage.SymbolTable;
import polite.abstractLanguage.SymbolTable.Pair;
import polite.interpreter.basicTypes.MyConstant;
import polite.interpreter.basicTypes.MyObj;
import polite.parser.Token;
import polite.util.Logger;


public class RecordManager {
	private static SymbolTable GLOBAL_ST;
	
	private static final Logger logger = Logger
			.getClassLogger(RecordManager.class);

	public static void setGlobalST(SymbolTable st) {
		GLOBAL_ST = st;
		if (st.getParent() != null)
			throw new RuntimeException("Symbol Table being set is not global");
	}

	public class ActivationRecord {
		private Logger logger = Logger.getClassLogger(ActivationRecord.class);
		private Map<String, MyObj> memMap = new TreeMap<String, MyObj>();
		private final ActivationRecord prevRecord;
		public Token activator;

		public ActivationRecord(Token t, ActivationRecord prevRecord) throws Exception{
			this.activator = t;
			this.prevRecord = prevRecord;
			Logger ml = logger.getMethodLogger("ActivationRecord");
			List<Pair> l = RecordManager.this.self_st.getTable();
			for (Pair p : l) {
				ml.log("entering new variable " + p.type + ": " + p.name);
				switch (p.type) {
				case NOTASSIGNED:
					throw new Exception(
							"Should not have not assigned here");
				case GLOBAL:
					ml.log("GLOBAL, not entering");
					break;
				case REFERENCEONLY:
				default:
					ml.log(p.type + ", default action");
					memMap.put(p.name, null);
				}
			}
			
			memMap.put(SymbolTable.RETURN_NAME, MyConstant.None);
			memMap.put(SymbolTable.INSTANCE_NAME, MyConstant.None);
		}

		public Token getActivatior(){
			return activator;
		}
		public void renewActivatorToken(Token t) {
			this.activator = t;
		}

		public boolean hasVariable(String name) {
			return memMap.containsKey(name);
		}

		public MyObj get(String name) {
			return memMap.get(name);
		}

		public void set(String name, MyObj newObj) {
			memMap.put(name, newObj);
		}

		public ActivationRecord getPreviousRecord() {
			return prevRecord;
		}
		public void printContent(){
			System.out.println(String.format("Line %d, %d: '%s' ... record content",
					activator.beginLine,activator.beginColumn,activator.image));
			for(Entry<String,MyObj> e:memMap.entrySet())
				System.out.println("\t"+e.getKey()+"= "+e.getValue());
		}
		public void printStackTrace(){
			String format="Line %d, %d: '%s'\n";
			ActivationRecord ar= this;
			System.out.print("Current stack: ");
			while(ar!=null){
				System.out.print(String.format(format, ar.activator.beginLine,
						ar.activator.beginColumn,ar.activator.image));
				System.out.print("\t");
				ar=ar.getPreviousRecord();
			}
			System.out.println();
		}
	}

	private Stack<ActivationRecord> localmem;
	private final SymbolTable self_st;

	public RecordManager(SymbolTable self, boolean isGenerator) {
		self_st = self;
		if (!isGenerator) {
			localmem = new Stack<ActivationRecord>();
		} else {
			localmem = null;
		}
	}

	public ActivationRecord activateRecord(Token t, ActivationRecord currentRecord) throws Exception{
		Logger ml = logger.getMethodLogger("activateRecord");
		ml.log("function type: "+(localmem==null?"generator":"normal"));
		if(t==null)
			ml.log("start management of module");
		else
			ml.log(String.format("start management of '%s' at (%d,%d",t.image,t.beginLine,t.beginColumn));
		localmem.push(new ActivationRecord(t, currentRecord));
		return localmem.peek();
	}

	public ActivationRecord activateAsGenerator(Token t,
			ActivationRecord currentRecord)  throws Exception {
		Logger ml = logger.getMethodLogger("activateAsGenerator");
		ml.log("function type: "+(localmem==null?"generator":"normal"));
		ml.log(String.format("create record of '%s' at (%d,%d",t.image,t.beginLine,t.beginColumn));
		return new ActivationRecord(t, currentRecord);
	}

	public void removeRecord() throws Exception{
		Logger ml = logger.getMethodLogger("removeRecord");
		ml.log("function type: "+(localmem==null?"generator":"normal"));
		Token t;
		if(localmem==null || localmem.isEmpty()){
			throw new Exception("Nothing to remove");
		}
		t=getCurrentRecord().getActivatior();
		if(t==null)
			ml.log("remove record of module");
		else
			ml.log(String.format("remove record of '%s' at (%d,%d",t.image,t.beginLine,t.beginColumn));
		localmem.pop();
	}

	public final ActivationRecord getCurrentRecord(){
		try {
			return localmem.peek();
		} catch (EmptyStackException e) {
			return null;
		}
	}
	public MyObj query(String name) throws Exception{
		if(getCurrentRecord().hasVariable(name)){
			return getCurrentRecord().get(name);
		}
		throw new Exception("cannot locate symbol '"+name+"' in record");
	}
	public void assign(String name, MyObj newmo)throws Exception{
		Logger ml = logger.getMethodLogger("assign");
		ml.log("assigning '"+name+"' as "+newmo);
		if(getCurrentRecord().hasVariable(name)){
			getCurrentRecord().set(name,newmo);
			return;
		}
		if(self_st.getParent()!=null){
			GLOBAL_ST.assign(name,newmo);
			return;
		}
		throw new Exception("cannot locate symbol '"+name+"' in record");

	}

	public void printStackTrace()throws Exception{
		ActivationRecord ar= getCurrentRecord();
		if(ar==null)
			System.out.println("Stack not activated");
		ar.printStackTrace();
	}
}

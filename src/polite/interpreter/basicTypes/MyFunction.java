package polite.interpreter.basicTypes;

import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import polite.abstractLanguage.Node;
import polite.abstractLanguage.SymbolTable;
import polite.abstractLanguage.Node.BlockNode;
import polite.abstractLanguage.Node.FunDef;
import polite.abstractLanguage.Node.Identifier;
import polite.abstractLanguage.Node.Module;
import polite.abstractLanguage.Node.NodeList;
import polite.abstractLanguage.SymbolTable.Pair;
import polite.abstractLanguage.SymbolTable.SymType;


public class MyFunction extends MyObj {
	
	// class values
	private BlockNode functionNode;
	private String[] paramNames;
	private TreeMap<String, MyObj> savedParams=new TreeMap<String,MyObj>();
	protected MyFunction(BaseType type){
		super(type);
	}
	 
	public MyFunction(Module m){
		this(BaseType.FUNCTION);
		functionNode=m;
		paramNames=new String[0];
		functionNode.getSymbolTable().startMemoryManagement(false);
	}
	public MyFunction(FunDef node, SymbolTable st){
		this(BaseType.FUNCTION);
		Identifier n = (Identifier)(node.getParamsList());
		functionNode=node;
		paramNames=new String[NodeList.countList(n)];
		for(int i=0; i<paramNames.length; i++){
			paramNames[i]= n.getName();
			n=(Identifier)n.getSibling();
		}
		functionNode.getSymbolTable().startMemoryManagement(false);
	}
	
	public boolean equals(Object o){
		if(this==o) return true;
		if(o==null) return false;
		if(o instanceof MyFunction){
			if(o.getClass().equals(MyFunction.class)){
				MyFunction that=(MyFunction)o;
				if(getStatements()==that.getStatements())
					return true;
			}
		}
		return false;
	}
	public void saveReferences(SymbolTable currentst) {
		SymbolTable localst=functionNode.getSymbolTable();
		for(Pair p:localst.getTable()){
			if(p.type==SymType.REFERENCEONLY){
				try{
					savedParams.put(p.name, currentst.query(p.name));
				} catch(Exception e){
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}

	public int getParamSize(){
		return paramNames.length;
	}
	public String toString(){
		return getType().toString();
	}
	public SymbolTable getSymbolTable(){
		return functionNode.getSymbolTable();
	}
	public Node getStatements() {
		return functionNode.getStatementsStart();
	}
	public void setParameters(List<MyObj> ll) throws Exception{
		SymbolTable st = functionNode.getSymbolTable();
		for(int i=0; i<paramNames.length; i++){
			st.assign(paramNames[i], ll.get(i));
		}
		for(Entry<String, MyObj> e:savedParams.entrySet()){
			st.assign(e.getKey(), e.getValue());
		}
	}
	public boolean __notzero(){
		return false;
	}
	
}

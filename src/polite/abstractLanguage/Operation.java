package polite.abstractLanguage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum Operation {
	// arithmatic
	_DEFINED_OP("",0),
	PLUS ("add",2),
	MINUS("sub",2),
	MULT ("mult",2),
	DIV  ("div",2),
	MOD  ("mod",2),
	
	// logic 
	AND ("and",2),
	OR  ("or",2),
	NOT ("not",1),
	
	// relation
	EQ ("eq",2),
	NE ("ne",2),
	GT ("gt",2),
	GE ("ge",2),
	LT ("lt",2),
	LE ("le",2),
	
	NEG ("neg",1),
	BOOL("nonzero",1),
	
	ITER("iter",1),
	GETATTR("getattr",2),
	SETATTR("setattr",3),
	CALL ("call",-1),
	LEN  ("len", 1),
	STR  ("str", 1)
	

	;
	private static final List<Operation> operators= new ArrayList<Operation>();
	private static final String pre="__";
	private static final String post="__";
	
	static{ // enum is built before static initialization
		for(Operation op: EnumSet.allOf(Operation.class)){
			operators.add(op);
		}
	}

	final int paramNum;
	final String fName;
	Operation(String Function, int num){
		fName=pre+Function+post;
		paramNum=num;
	}
	
	public int getParamNumber(){
		return paramNum;
	}
	public String toString(){
		return fName;
	}
	
	public static Operation checkFunctionName(String methodName) {
		for(Operation op: operators){
			if(op.toString().equals(methodName))
				return op;
		}
		return null;
	}
}

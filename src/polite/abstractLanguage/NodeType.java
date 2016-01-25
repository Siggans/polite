package polite.abstractLanguage;

public enum NodeType {
	MODULE("module"),   // Node.Module
	CALL("call"),       // Node.Call
	ASSIGN("assign"),   // Node.Assign
	
	FUNDEF("fun-define"), // Node.FunDef
	LAMBDA("lambda"),     // Node.FunDef
	GENERATOR("generator"),// Node.FunDef
	INGENERATOR("generator to value"),
	
	IDENTIFIER("reference"), // Node.Identifier
	LITERAL("literal"),  // Node.Literal
	IF("if-else"),       // Node.IfElse
	FOR("foreach"),      // Node.For
	WHILE("while"),      // Node.While
	PRINT("print"),      // Node.Print
	RETURN("return"),    // Node.Return
	YIELD("yield"),      // Node.Yield
	
	GLOBAL("global identifier"), // Node.Global
	NOOP("no_op"),       // Node.NOOP
	
	CLASSDEF("classdef"),// undefined atm
	;
	private String name;
	private NodeType(String s){
		name=s;
	}
	public String verbose(){
		return name;
	}
	
}

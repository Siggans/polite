package polite.interpreter.basicTypes;

import java.util.List;

import polite.abstractLanguage.Operation;
import polite.abstractLanguage.SymbolTable;
import polite.interpreter.InterpreterException;
import polite.parser.Token;


final public class MyBoolean extends MyObj {
	static public final MyBoolean True = new MyBoolean(true);
	static public final MyBoolean False = new MyBoolean(false);

	private Boolean value;

	protected MyBoolean() {
		super(BaseType.BOOLEAN);
		setValue(null);
	}

	public MyBoolean(boolean flag) {
		value = flag;
	}

	protected MyBoolean(String s) {
		if (s.equals("True"))
			value = true;
		if (s.equals("False"))
			value = false;
	}

	public MyBoolean(MyBoolean f) {
		this.value = new Boolean(f.value);
	}

	public String toString() {
		if (value == null) {
			return getType().toString();
		}
		return value ? "True" : "False";
	}

	public boolean getBoolValue() {
		if (value != null)
			return this.value;
		return false;
	}

	public void setBoolValue(boolean flag) {
		this.value = flag;
	}

	public Object getValue() {
		return getBoolValue();
	}
	public boolean equals(Object o){
		if(this==o) return true;
		if(o==null) return false;
		if(o.getClass().equals(MyBoolean.class)){
			MyBoolean mb =(MyBoolean)o;
			if(mb.getBoolValue()==getBoolValue())
				return true;
		}
		return false;
	}

	@Override
	public boolean __notzero() {
		return getBoolValue();
	}

	@ExportAsMethod(type=Operation.EQ, extendedParam=true)
	public static MyObj __eq__(Token t, List<MyObj> ll, SymbolTable st)
			throws InterpreterException {
		MyObj obj2 = ll.remove(1);
		MyObj obj1 = BuiltInFunctions.bool(t, ll, st);
		ll.set(0, obj2);
		obj2 = BuiltInFunctions.bool(t, ll, st);
		return new MyBoolean(obj1.__notzero() == obj2.__notzero());
	}
	
	@ExportAsMethod(type=Operation.NE, extendedParam=true)
	public static MyObj __ne__(Token t, List<MyObj> ll, SymbolTable st)
			throws InterpreterException {
		MyObj obj2 = ll.remove(1);
		MyObj obj1 = BuiltInFunctions.bool(t, ll, st);
		ll.set(0, obj2);
		obj2 = BuiltInFunctions.bool(t, ll, st);
		return new MyBoolean(obj1.__notzero() != obj2.__notzero());
	}

}

package polite.interpreter.basicTypes;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import polite.abstractLanguage.Operation;
import polite.abstractLanguage.SymbolTable;
import polite.interpreter.InterpreterException;
import polite.parser.Token;
import polite.util.Logger;


public class MyObj {
	// class values

	static public final MyObj None = new MyObj(BaseType.NONE);

	// instance values
	final protected Logger iLogger;
	final protected Map<String, MyObj> cmDict;
	final private MyType cmType;
	private Object value;

	protected MyObj(BaseType type) {
		iLogger = Logger.getClassLogger(this.getClass());
		iLogger.log("Creating object of type: " + type);
		cmType = MyType.getType(type);
		cmDict = new TreeMap<String, MyObj>();
	}

	public MyObj() {
		this(BaseType.OBJECT);
		setValue(null);
	}

	public MyType getType() {
		return cmType;
	}

	// ==============================================
	// fields that may need to be overwritten
	public String toString() {
		if (this == None)
			return "None";
		return BaseType.getMyClassString(BaseType.OBJECT);
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}

	final public MyObj select(String memberName) {

		Logger ml = iLogger.getMethodLogger("select");
		ml.enter();
		ml.log("selecting " + memberName);

		if (cmDict.containsKey(memberName)) {
			ml.log(memberName + " found");
			return cmDict.get(memberName);
		} else {
			ml.leave();
			return selectSuper(memberName);
		}
	}

	final public MyObj selectSuper(String memberName) {
		Logger ml = iLogger.getMethodLogger("selectSuper");
		ml.enter();
		MyObj member;
		MyType typeObj = this.getType();
		do {
			ml.log("currently verifing class obj " + typeObj);
			member = typeObj.getMember(memberName);
			if (member != None)
				break;
			typeObj = typeObj.getParent();
		} while (typeObj != null);

		if (typeObj == null) {
			ml.log("cannot find member name " + memberName);
			return MyConstant.None;
		}

		return member;
	}

	public Map<String, MyObj> getDict() {
		return cmDict;
	}

	@ExportAsMethod(type = Operation.BOOL)
	public static MyBoolean __notzero__(List<MyObj> ll) {
		return new MyBoolean(ll.get(0).__notzero());
	}

	public boolean __notzero() {
		return this != MyObj.None;
	}

	@ExportAsMethod(type = Operation.STR)
	final public static MyString __str__(List<MyObj> ll) {
		return new MyString(ll.get(0).toString());
	}

	@ExportAsMethod(type = Operation.GETATTR)
	final public static MyObj __getattr__(List<MyObj> ll) throws InterpreterException {
		MyObj o1 = ll.get(0);
		MyObj o2 = ll.get(1);
		if (!(o2 instanceof MyString))
			throw InterpreterException.parameterTypeError(2, o2.getType(), "string");
		MyString str = (MyString) o2;
		if (str.getValue() == null || str.getValue() == "")
			throw new InterpreterException("attribute name is an empty string");
		return o1.select(str.getStringValue());
	}

	@ExportAsMethod(type = Operation.SETATTR)
	final public static MyObj __setattr__(List<MyObj> ll) throws InterpreterException {
		MyObj ins = ll.get(0);
		MyObj obj = ll.get(1);
		MyObj newo = ll.get(2);
		if (!(obj instanceof MyString))
			throw InterpreterException.parameterTypeError(2, obj.getType(), "string");
		MyString str = (MyString) obj;
		if (str.getValue() == null || str.getValue() == "")
			throw new InterpreterException("attribue name is an empty string");
		MyObj oldObj = ins.select(str.getStringValue());
		ins.getDict().put(str.getStringValue(), newo);
		return oldObj;
	}

	@ExportAsMethod(type = Operation.AND, extendedParam = true)
	public static MyObj __and__(Token t, List<MyObj> ll, SymbolTable st)
			throws InterpreterException {
		MyObj obj2 = ll.remove(1);
		MyObj obj1 = BuiltInFunctions.bool(t, ll, st);
		ll.set(0, obj2);
		obj2 = BuiltInFunctions.bool(t, ll, st);
		return new MyBoolean(obj1.__notzero() && obj2.__notzero());
	}

	@ExportAsMethod(type = Operation.OR, extendedParam = true)
	public static MyObj __or__(Token t, List<MyObj> ll, SymbolTable st)
			throws InterpreterException {
		MyObj obj2 = ll.remove(1);
		MyObj obj1 = BuiltInFunctions.bool(t, ll, st);
		ll.set(0, obj2);
		obj2 = BuiltInFunctions.bool(t, ll, st);
		return new MyBoolean(obj1.__notzero() || obj2.__notzero());
	}

	@ExportAsMethod(type = Operation.NOT, extendedParam = true)
	public static MyObj __not__(Token t, List<MyObj> ll, SymbolTable st)
			throws InterpreterException {
		MyObj obj1 = BuiltInFunctions.bool(t, ll, st);
		return new MyBoolean(!obj1.__notzero());
	}

	public boolean equals(Object o){
		if(this==o)return true;
		return false;
	}
	
	@ExportAsMethod(type = Operation.EQ)
	final public static MyObj __eq__(List<MyObj> ll) throws InterpreterException {
		MyObj obj1 = ll.get(0);
		MyObj obj2 = ll.get(1);
		return new MyBoolean(obj1.equals(obj2));
	}

	@ExportAsMethod(type = Operation.NE)
	final public static MyObj __ne__(List<MyObj> ll) throws InterpreterException {
		MyObj obj1 = ll.get(0);
		MyObj obj2 = ll.get(1);
		return new MyBoolean(!obj1.equals(obj2));
	}

}

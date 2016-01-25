package polite.interpreter.basicTypes;

import static polite.interpreter.basicTypes.BaseType.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import polite.util.Logger;


final public class MyType {
	// ***********************************
	// static interface
	static private Map<String, MyType> definedClassType = new HashMap<String, MyType>();
	static private Logger clogger = Logger.getClassLogger(MyType.class);

	static {
		clogger.log("Creating Class Objects");

		final BaseType[] defaultTypes = { OBJECT, NONE, STRING, FLOAT,
				INTEGER, BOOLEAN, FUNCTION, BUILT_IN_FUNCTION, TUPLE,LIST };
		for (int i = 0; i < defaultTypes.length; i++) {
			addType(defaultTypes[i]);
		}
		for(int i=0; i<defaultTypes.length; i++){
			getType(defaultTypes[i]).setDefinedMembers(BuiltInFunctions.addBuiltInFunctions(defaultTypes[i].implementationClass));
		}

	}

	static private MyType addType(BaseType type) {
		return addType(type, getType(type.getParentType()));
	}

	static public MyType addType(BaseType tp, MyType parent) {
		String name = BaseType.getMyClassString(tp);
		clogger.log("Adding new type: " + name);
		if (definedClassType.containsKey(name)) {
			clogger.log(name + " is being replaced");
		}
		MyType type = new MyType(name, parent);
		definedClassType.put(name, type);
		return type;
	}

	static public MyType getType(BaseType type) {
		if (type == null)
			return null;
		return getType(type.toString());
	}

	static public MyType getType(String name) {
		if (definedClassType.containsKey(name))
			return definedClassType.get(name);
		if (definedClassType.containsKey(BaseType.getMyClassString(name)))
			return definedClassType.get(BaseType.getMyClassString(name));
		return null;
	}

	// **********************************
	// instance interface
	final protected String typeName;
	final protected BaseType basicType;
	final protected MyType parentType;
	protected Map<String, MyObj> builtInDefinitions;
	protected Map<String, MyObj> overRiddingDefinitions;

	private MyType(BaseType type, MyType parent) {
		typeName = null;
		parentType = parent;
		basicType = type;
		overRiddingDefinitions = null;
	}

	private MyType(String name, MyType parent) {
		typeName = name;
		parentType = parent;
		basicType = CLASS;
		overRiddingDefinitions = null;
	}
	protected void setDefinedMembers(Map<String, MyObj> definedMembers){
		builtInDefinitions = new HashMap<String, MyObj>();
		builtInDefinitions.putAll(definedMembers);
		
	}

	public BaseType getType() {
		return basicType;
	}

	public MyType getParent() {
		return parentType;
	}

	public MyObj getDefaultMember(String memberName){
		Logger ml = clogger.getMethodLogger("getDefaultMember");
		ml.enter();
		if (builtInDefinitions.containsKey(memberName)) {
			ml.log(memberName + " is builtin");
			ml.leave();
			return builtInDefinitions.get(memberName);
		}
		return MyConstant.None;
	}
	public Set<String> getDefaultMemeberNames(){
		return this.builtInDefinitions.keySet();
	}
	public MyObj getMember(String memberName) {
		Logger ml = clogger.getMethodLogger("getMember");
		ml.enter();
		if (overRiddingDefinitions != null
				&& overRiddingDefinitions.containsKey(memberName)) {
			ml.log(memberName + " a was added member");
			ml.leave();
			return overRiddingDefinitions.get(memberName);
		}
		ml.leave();
		return getDefaultMember(memberName);
		
	}

	public MyObj setMember(String memberName, MyObj newMemberDef) {
		Logger ml = clogger.getMethodLogger("getMember");
		ml.enter();
		if (overRiddingDefinitions == null) {
			ml.log("creating overridding mechanism");
			overRiddingDefinitions = new HashMap<String, MyObj>();
		}
		ml.log("saving "+memberName + "("+newMemberDef+")");
		MyObj original;
		original = overRiddingDefinitions.put(memberName, newMemberDef);
		return original == null ? MyConstant.None : original;
	}

	public String toString() {
		if (typeName == null)
			return BaseType.getMyClassString(basicType);
		return typeName;
	}
}

package polite.interpreter.basicTypes;

import polite.interpreter.basicTypes.BuiltInFunctions.BuiltInFunction;

public enum BaseType {

	OBJECT("object", null, MyObj.class.getName()), NONE("None", OBJECT, null),

	CLASS("class", OBJECT, null),

	STRING("string", OBJECT, MyString.class.getName()),

	FLOAT("float", OBJECT, MyReal.class.getName()), INTEGER("integer", FLOAT,
			MyInteger.class.getName()),

	BOOLEAN("boolean", OBJECT, MyBoolean.class.getName()),

	TUPLE("tuple", OBJECT, MyTuple.class.getName()), LIST("list", TUPLE,
			MyList.class.getName()),

	FUNCTION("function", OBJECT, MyFunction.class.getName()), LAMBDA("lambda",
			FUNCTION, null), METHOD("method", FUNCTION, null),

	GENERATOR("generator", OBJECT, null),

	// method that should not be overwritten
	BUILT_IN_FUNCTION("built_in_function", FUNCTION, BuiltInFunction.class
			.getName());

	public static String getMyClassString(BaseType type) {
		return type.cmName;
	}

	public static String getMyClassString(String pyclassName) {
		return CLASS + ":" + pyclassName;
	}

	private final String cmName;
	private final BaseType parentType;
	public final String implementationClass;

	BaseType(String type, BaseType parent, String s) {
		cmName = type;
		parentType = parent;
		implementationClass = s;
	}

	public BaseType getParentType() {
		return parentType;
	}

	public String toString() {
		return this.cmName;
	}

}

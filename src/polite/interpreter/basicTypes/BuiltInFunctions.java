package polite.interpreter.basicTypes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import polite.abstractLanguage.Operation;
import polite.abstractLanguage.SymbolTable;
import polite.interpreter.Interpreter;
import polite.interpreter.InterpreterException;
import polite.parser.Token;
import polite.util.Logger;


public class BuiltInFunctions {
	// Only static calls
	final public static class BuiltInFunction extends MyFunction {
		private final Class<?> c;
		private final String methodName;
		private final int paramSize;
		private final boolean extendedParams;

		public BuiltInFunction(Class<?> c, String javaMethodName, int paramSize,
				boolean extended) {
			super(BaseType.BUILT_IN_FUNCTION);
			Logger ml = iLogger.getMethodLogger("BuiltInFunction");
			ml.log(c + "." + javaMethodName + " param: " + paramSize);
			this.c = c;
			this.methodName = javaMethodName;
			this.paramSize = paramSize;
			this.extendedParams = extended;
		}

		public MyObj invoke(Token t, List<MyObj> ll, SymbolTable st)
				throws InterpreterException {
			Logger ml = iLogger.getMethodLogger("invoke");
			ml.enter();
			try {
				if (extendedParams) {
					ml.log("invoking " + c.getCanonicalName() + "."
							+ methodName + " as extended type");
					Method m = c.getMethod(methodName, Token.class, List.class,
							SymbolTable.class);
					return (MyObj) m.invoke(null, t, ll, st);
				} else {
					ml.log("invoking " + c.getCanonicalName() + "."
							+ methodName + " as normal type");
					Method m = c.getMethod(methodName, List.class);
					return (MyObj) m.invoke(null, ll);
				}
			} catch (Exception e) {
				if (e instanceof InterpreterException)
					throw (InterpreterException) e;
				e.printStackTrace();
				throw InterpreterException.newException(t, "internal error");
			}
		}

		public void testExistance() throws Throwable {
			try {
				Method m;
				if (extendedParams)
					m = c.getMethod(methodName, Token.class, List.class,
							SymbolTable.class);
				else
					m = c.getMethod(methodName, List.class);
				if (!Modifier.isStatic(m.getModifiers())
						|| !Modifier.isPublic(m.getModifiers()))
					throw new Exception();
			} catch (Throwable e) {
				String params = extendedParams ? "(Token, List, SymbolTable)"
						: "(List)";
				throw new Exception("public static " + c.getName() + "."
						+ methodName + params + " does not exist");
			}
		}

		public String toString() {
			return getType().toString();
		}

		public int getParamSize() {
			return paramSize;
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null)
				return false;
			if (o instanceof BuiltInFunction) {
				if (o.getClass().equals(BuiltInFunction.class)) {
					BuiltInFunction bif = (BuiltInFunction) o;
					if (getParamSize() == bif.getParamSize() && c.equals(bif.c)
							&& methodName.equals(bif.methodName)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	static Logger cl = Logger.getClassLogger(BuiltInFunction.class);

	public static void addDefaultGlobalFunctions(SymbolTable st)
			throws InterpreterException {
		Logger ml = cl.getMethodLogger("addDefaultGlobalFunctions");
		ml.enter();
		Class<BuiltInFunctions> c = BuiltInFunctions.class;
		Method[] ms = c.getDeclaredMethods();
		ExportAsFunction eaf;
		for (Method m : ms) {
			if (m.isAnnotationPresent(ExportAsFunction.class)) {

				eaf = (ExportAsFunction) m
						.getAnnotation(ExportAsFunction.class);
				ml.log("adding " + c.getName() + "." + m.getName() + "() as '"
						+ eaf.name() + "'");
				BuiltInFunction bif = new BuiltInFunction(c, m.getName(), eaf
						.paramSize(), eaf.extendedParam());
				try {
					bif.testExistance();
					st.assign(eaf.name(), bif);
				} catch (Throwable e) {
					e.printStackTrace();
					throw new InterpreterException("Fail to create '"
							+ eaf.name() + "' object");
				}
			}
		}
		try {
			st.assign("None", MyConstant.None);
			st.assign("True", MyConstant.True);
			st.assign("False", MyConstant.False);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InterpreterException("Fail to create constants");
		}

		ml.leave();
	}

	public static Map<String, MyObj> addBuiltInFunctions(String c) {
		Logger ml = cl.getMethodLogger("addBuiltInFunctions");
		TreeMap<String, MyObj> dict = new TreeMap<String, MyObj>();
		ml.enter();
		if (c == null)
			return dict;
		ml.log("Creating method for " + c);
		try {
			Class<?> cls = Class.forName(c);
			ExportAsMethod eam;
			for (Method m : cls.getMethods()) {
				if (m.isAnnotationPresent(ExportAsMethod.class)) {
					eam = (ExportAsMethod) (m
							.getAnnotation(ExportAsMethod.class));
					boolean skip = false;
					for (Class<?> excluded : eam.excludesClasses())
						if (excluded.equals(cls))
							skip = true;
					if (skip)
						continue;
					String name;
					int paramSize;
					if (eam.type() == Operation._DEFINED_OP) {
						name = eam.name();
						paramSize = eam.paramSize();
						if (paramSize == 0)
							throw new Exception(
									"parameter size=0 from ExportAsMethod at "
											+ c + "." + m.getName());
					} else {
						name = eam.type().toString();
						paramSize = eam.type().getParamNumber();
					}
					ml.log("adding " + c + "." + m.getName() + "() as '" + name
							+ "'");
					BuiltInFunction bif = new BuiltInFunction(cls, m.getName(),
							paramSize, eam.extendedParam());
					bif.testExistance();
					dict.put(name, bif);
				}
			}
			ml.leave();
			return dict;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static MyObj memberOverloadCaller(MyObj instance, Operation op,
			Token t, List<MyObj> params, SymbolTable st)
			throws InterpreterException {
		MyObj overwrite = instance.getDict().get(op.toString());
		if (overwrite instanceof MyFunction) {
			MyFunction newf = (MyFunction) overwrite;
			if (newf.getParamSize() == op.getParamNumber())
				return Interpreter.executeFunction(newf, t, params, st);
			else
				throw InterpreterException.newException(t,
						"bad parameter size on " + op + " expected: "
								+ op.getParamNumber());
		} else {
			throw InterpreterException.newException(t, op
					+ " is not a function type");
		}
	}

	@ExportAsFunction(name = "send", paramSize = 2, extendedParam = true)
	public static MyObj send(Token t, List<MyObj> params, SymbolTable st)
			throws InterpreterException {
		MyObj obj = params.get(0);
		if (obj.getDict().containsKey(Operation.GETATTR.toString()))
			return memberOverloadCaller(obj, Operation.GETATTR, t, params, st);
		return MyObj.__getattr__(params);
	}

	@ExportAsFunction(name = "int", paramSize = 1)
	public static MyObj __int(List<MyObj> params) throws InterpreterException {
		MyObj obj = params.get(0);
		if (obj instanceof MyInteger)
			return new MyInteger((MyInteger) obj);
		if (obj instanceof MyReal)
			return new MyInteger((MyReal) obj);
		if (obj instanceof MyString)
			try {
				return new MyInteger(((MyString) obj).getStringValue().split("\\.")[0]);
			} catch (Exception e) {
				return new MyInteger("0");
			}
		if (obj instanceof MyBoolean)
			return new MyInteger(((MyBoolean) obj).getBoolValue() ? "1" : "0");
		throw InterpreterException.parameterTypeError(1, obj.getType(), "integer","float","string","boolean");
	}

	@ExportAsFunction(name = "float", paramSize = 1)
	public static MyObj __float(List<MyObj> params) throws InterpreterException {
		MyObj obj = params.get(0);
		if (obj instanceof MyInteger)
			return new MyReal((MyInteger) obj);
		if (obj instanceof MyReal)
			return new MyReal((MyReal) obj);
		if (obj instanceof MyString)
			try {
				return new MyReal(((MyString) obj).getStringValue());
			} catch (Exception e) {
				return new MyReal("0f");
			}
		if (obj instanceof MyBoolean)
			return new MyReal(((MyBoolean) obj).getBoolValue() ? "1f" : "0f");
		throw InterpreterException.parameterTypeError(1, obj.getType(), "integer","float","string","boolean");
	}

	@ExportAsFunction(name = "str", paramSize = 1, extendedParam = true)
	public static MyObj str(Token t, List<MyObj> params, SymbolTable st)
			throws InterpreterException {
		MyObj obj = params.get(0);
		if (obj.getDict().containsKey(Operation.STR.toString()))
			return memberOverloadCaller(obj, Operation.STR, t, params, st);
		return str(obj.toString());
	}

	public static MyObj str(String string) {
		return new MyString(string);
	}

	@ExportAsFunction(name = "bool", paramSize = 1, extendedParam = true)
	public static MyObj bool(Token t, List<MyObj> params, SymbolTable st)
			throws InterpreterException {
		MyObj obj = params.get(0);
		if (obj.getDict().containsKey(Operation.BOOL.toString()))
			return memberOverloadCaller(obj, Operation.BOOL, t, params, st);
		return MyObj.__notzero__(params);
	}

	@ExportAsFunction(name = "super", paramSize = 2)
	public static MyObj __super(List<MyObj> params) throws InterpreterException {
		MyObj obj = params.get(0);
		MyObj obj2 = params.get(1);
		if (!(obj2 instanceof MyString))
			throw InterpreterException.parameterTypeError(2, obj2.getType(), "string");
		MyString str = (MyString) obj2;
		return obj.getType().getDefaultMember(str.getStringValue());
	}

	@ExportAsFunction(name = "type", paramSize = 1)
	public static MyString type(List<MyObj> params) throws InterpreterException {
		return new MyString("type:" + params.get(0).getType().toString());
	}

	@ExportAsFunction(name = "len", paramSize = 1, extendedParam = true)
	public static MyObj len(Token t, List<MyObj> params, SymbolTable st)
			throws InterpreterException {
		MyObj obj = params.get(0);
		if (obj.getDict().containsKey(Operation.LEN.toString()))
			return memberOverloadCaller(obj, Operation.LEN, t, params, st);
		if (obj instanceof MyTuple) {
			return MyTuple.__len__(params);
		} else if (obj instanceof MyString) {
			return MyString.__len__(params);
		}
		throw InterpreterException.newException(t,
				"operator __len__ not defined");
	}

	@ExportAsFunction(name = "dir", paramSize = 1)
	public static MyObj dir(List<MyObj> params) throws InterpreterException {
		Set<String> names = new TreeSet<String>();
		MyList ml = new MyList();
		MyObj obj = params.get(0);
		names.addAll(obj.getType().getDefaultMemeberNames());
		names.addAll(obj.getDict().keySet());
		for (String s : names)
			ml.append(new MyString(s));
		return ml;
	}

	@ExportAsFunction(name = "list", paramSize = -1)
	public static MyList list(List<MyObj> params) {
		MyList ml = new MyList();
		for (MyObj obj : params)
			ml.append(obj);
		return ml;
	}

	@ExportAsFunction(name = "tuple", paramSize = -1)
	public static MyTuple tuple(List<MyObj> params) {
		MyTuple ml = new MyTuple();
		for (MyObj obj : params)
			ml.append(obj);
		return ml;
	}
	@ExportAsFunction(name ="object", paramSize=0)
	public static MyObj object(List<MyObj> params){
		return new MyObj();
	}

}

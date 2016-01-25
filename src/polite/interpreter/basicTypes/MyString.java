package polite.interpreter.basicTypes;

import java.util.List;

import polite.abstractLanguage.Operation;
import polite.interpreter.InterpreterException;

final public class MyString extends MyObj {
	private String value;

	protected MyString() {
		super(BaseType.STRING);
		setStringValue(null);
	}

	private void setStringValue(String object) {
		this.value = object;
	}

	public MyString(String s) {
		this();
		setStringValue(s);
	}

	public MyString(MyObj obj) {
		this(obj.toString());
	}

	public Object getValue() {
		return this.getStringValue();
	}

	public String getStringValue() {
		return value;
	}

	public boolean __notzero() {
		return !"".equals(getStringValue());
	}

	public String toString() {
		if (getStringValue() == null)
			return getType().toString();
		return this.getStringValue().toString();
	}

	public String __get(int i) {
		return getStringValue().charAt(i) + "";
	}

	public int __len() {
		return getStringValue().length();
	}

	@ExportAsMethod(type = Operation._DEFINED_OP, name = "get", paramSize = 2)
	public static MyObj get__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyString) {
			if (obj1 instanceof MyInteger) {
				MyString str = (MyString) obj0;
				int i = ((MyInteger) obj1).getIntValue().intValue();
				if (i < 0 || i >= str.__len())
					return MyConstant.None;
				return new MyString(str.__get(i));
			}
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"string");
	}

	@ExportAsMethod(type = Operation._DEFINED_OP, name = "sub", paramSize = 3)
	public static MyObj sub__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		MyObj obj2 = ll.get(2);
		if (!(obj0 instanceof MyString))
			throw InterpreterException.parameterTypeError(1, obj0.getType(),
					"string");
		if (!(obj1 instanceof MyInteger))
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer");
		if (!(obj2 instanceof MyInteger))
			throw InterpreterException.parameterTypeError(3, obj2.getType(),
					"integer");
		MyString str = (MyString) obj0;
		int begin = ((MyInteger) obj1).getIntValue().intValue();
		int end = ((MyInteger) obj2).getIntValue().intValue();
		if (begin < 0)
			begin = 0;
		if (begin > str.__len())
			begin = str.__len();
		if (end < 0)
			end = 0;
		if (end > str.__len())
			end = str.__len();
		if (begin >= end)
			return new MyString("");
		return new MyString(str.getStringValue().substring(begin, end));

	}

	@ExportAsMethod(type = Operation._DEFINED_OP, name = "split", paramSize = 2)
	public static MyObj split__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (!(obj0 instanceof MyString))
			throw InterpreterException.parameterTypeError(1, obj0.getType(),
					"string");
		if (!(obj1 instanceof MyString))
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"string");
		MyList ml = new MyList();
		MyString str = (MyString) obj0;
		String token = ((MyString) obj1).getStringValue();
		String[] tokens = str.getStringValue().split(token);
		for (String s : tokens)
			ml.append(new MyString(s));
		return ml;
	}

	@ExportAsMethod(type = Operation.PLUS)
	public static MyObj concate__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (!(obj0 instanceof MyString))
			throw InterpreterException.parameterTypeError(1, obj0.getType(),
					"string");
		if (!(obj1 instanceof MyString))
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"string");
		String str1 = ((MyString) obj0).getStringValue();
		String str2 = ((MyString) obj1).getStringValue();
		return new MyString(str1 + str2);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (o instanceof MyString) {
			MyString that = (MyString) o;
			return getStringValue().equals(that.getStringValue());
		}
		return false;
	}

	@ExportAsMethod(type = Operation.GT)
	public static MyObj __gt__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (!(obj0 instanceof MyString))
			throw InterpreterException.parameterTypeError(1, obj0.getType(),
					"string");
		if (!(obj1 instanceof MyString))
			throw InterpreterException.parameterTypeError(2, obj0.getType(),
					"string");
		String s1 = ((MyString) obj0).getStringValue();
		String s2 = ((MyString) obj1).getStringValue();
		return new MyBoolean(s1.compareTo(s2) > 0);
	}

	@ExportAsMethod(type = Operation.GE)
	public static MyObj __ge__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (!(obj0 instanceof MyString))
			throw InterpreterException.parameterTypeError(1, obj0.getType(),
					"string");
		if (!(obj1 instanceof MyString))
			throw InterpreterException.parameterTypeError(2, obj0.getType(),
					"string");
		String s1 = ((MyString) obj0).getStringValue();
		String s2 = ((MyString) obj1).getStringValue();
		return new MyBoolean(s1.compareTo(s2) >= 0);
	}

	@ExportAsMethod(type = Operation.LT)
	public static MyObj __lt__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (!(obj0 instanceof MyString))
			throw InterpreterException.parameterTypeError(1, obj0.getType(),
					"string");
		if (!(obj1 instanceof MyString))
			throw InterpreterException.parameterTypeError(2, obj0.getType(),
					"string");
		String s1 = ((MyString) obj0).getStringValue();
		String s2 = ((MyString) obj1).getStringValue();
		return new MyBoolean(s1.compareTo(s2) < 0);
	}

	@ExportAsMethod(type = Operation.LE)
	public static MyObj __le__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (!(obj0 instanceof MyString))
			throw InterpreterException.parameterTypeError(1, obj0.getType(),
					"string");
		if (!(obj1 instanceof MyString))
			throw InterpreterException.parameterTypeError(2, obj0.getType(),
					"string");
		String s1 = ((MyString) obj0).getStringValue();
		String s2 = ((MyString) obj1).getStringValue();
		return new MyBoolean(s1.compareTo(s2) <= 0);
	}

	@ExportAsMethod(type = Operation.LEN)
	public static MyObj __len__(List<MyObj> params) throws InterpreterException {
		MyObj obj = params.get(0);
		if (obj instanceof MyString)
			return new MyInteger(((MyString) obj).__len() + "");
		if (obj instanceof MyTuple)
			return new MyInteger(((MyTuple) obj).__len() + "");
		throw InterpreterException.parameterTypeError(1, obj.getType(), "list",
				"tuple", "string");
	}
}

package polite.interpreter.basicTypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import polite.abstractLanguage.Operation;
import polite.interpreter.InterpreterException;

public class MyReal extends MyObj {

	private BigDecimal value;

	protected MyReal() {
		this(BaseType.FLOAT);
		setRealValue(null);
	}

	protected MyReal(BaseType type) {
		super(type);
	}

	public MyReal(String f) {
		this();
		if (!Character.isDigit(f.charAt(f.length() - 1)))
			f = f.substring(0, f.length() - 1);
		setRealValue(new BigDecimal(f));
	}

	public MyReal(BigDecimal f) {
		this();
		setRealValue(new BigDecimal(f.unscaledValue(), f.scale()));
	}

	public MyReal(MyReal f) {
		this(f.getRealValue());
	}

	public MyReal(MyInteger obj) {
		this();
		setValue(new BigDecimal((BigInteger) (obj.getValue())));
	}

	protected void setRealValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getRealValue() {
		return this.value;
	}

	@Override
	public boolean __notzero() {
		return this.getRealValue().compareTo(new BigDecimal(0.0)) != 0;
	}

	public MyReal __add(MyReal t) {
		if (t instanceof MyInteger)
			return __add((MyInteger) t);
		return new MyReal(getRealValue().add(t.getRealValue()));
	}

	public MyReal __add(MyInteger t) {
		return new MyReal(getRealValue().add(new BigDecimal(t.getIntValue())));
	}

	@ExportAsMethod(type = Operation.PLUS)
	public static MyObj __add__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal)
				return ((MyReal) obj0).__add((MyReal) obj1);
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	public MyReal __sub(MyReal t) {
		if (t instanceof MyInteger)
			return __sub((MyInteger) t);
		return new MyReal(getRealValue().subtract(t.getRealValue()));
	}

	public MyReal __sub(MyInteger t) {
		return new MyReal(getRealValue().subtract(
				new BigDecimal(t.getIntValue())));
	}

	@ExportAsMethod(type = Operation.MINUS)
	public static MyObj __sub__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal)
				return ((MyReal) obj0).__sub((MyReal) obj1);
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	public String toString() {
		if (getRealValue() == null)
			return getType().toString();
		return getRealValue().toString();
	}

	public MyReal __mult(MyReal t) {
		if (t instanceof MyInteger)
			return __mult((MyInteger) t);
		return new MyReal(getRealValue().multiply(t.getRealValue()));
	}

	public MyReal __mult(MyInteger t) {
		return new MyReal(getRealValue().multiply(
				new BigDecimal(t.getIntValue())));
	}

	@ExportAsMethod(type = Operation.MULT)
	public static MyObj __mult__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal) {
				return ((MyReal) obj0).__mult((MyReal) obj1);
			}
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	public MyReal __div(MyReal t) {
		if (t instanceof MyInteger)
			return __div((MyInteger) t);
		return new MyReal(getRealValue().divide(t.getRealValue()));
	}

	public MyReal __div(MyInteger t) {
		return new MyReal(getRealValue()
				.divide(new BigDecimal(t.getIntValue())));
	}

	@ExportAsMethod(type = Operation.DIV)
	public static MyObj __div__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal)
				try {
					return ((MyReal) obj0).__div((MyReal) obj1);
				} catch (ArithmeticException e) {
					throw new InterpreterException("divid by " + obj1);
				}
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	public MyReal __mod(MyReal t) {
		if (t instanceof MyInteger)
			return __mod((MyInteger) t);
		return new MyReal(getRealValue().remainder(t.getRealValue()));
	}

	public MyReal __mod(MyInteger t) {
		return new MyReal(getRealValue().remainder(
				new BigDecimal(t.getIntValue())));
	}

	@ExportAsMethod(type = Operation.MOD)
	public static MyObj __mod__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal)
				try {
					return ((MyReal) obj0).__mod((MyReal) obj1);
				} catch (ArithmeticException e) {
					throw new InterpreterException("mod by " + obj1);
				}
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	public boolean __eq(MyReal t) {
		if (t instanceof MyInteger)
			return __eq((MyInteger) t);
		return getRealValue().compareTo(t.getRealValue()) == 0;
	}

	public boolean __eq(MyInteger t) {
		return getRealValue().compareTo(new BigDecimal(t.getIntValue())) == 0;
	}

	final public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (o instanceof MyReal) {
			return __eq((MyReal) o);
		}
		return false;
	}

	public boolean __gt(MyReal t) {
		if (t instanceof MyInteger)
			return __gt((MyInteger) t);
		return getRealValue().compareTo(t.getRealValue()) > 0;
	}

	public boolean __gt(MyInteger t) {
		return getRealValue().compareTo(new BigDecimal(t.getIntValue())) > 0;
	}

	@ExportAsMethod(type = Operation.GT)
	public static MyObj __gt__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal)
				return new MyBoolean(((MyReal) obj0).__gt((MyReal) obj1));
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	public boolean __ge(MyReal t) {
		if (t instanceof MyInteger)
			return __ge((MyInteger) t);
		return getRealValue().compareTo(t.getRealValue()) >= 0;
	}

	public boolean __ge(MyInteger t) {
		return getRealValue().compareTo(new BigDecimal(t.getIntValue())) >= 0;
	}

	@ExportAsMethod(type = Operation.GE)
	public static MyObj __ge__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal)
				return new MyBoolean(((MyReal) obj0).__ge((MyReal) obj1));

			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	public boolean __lt(MyReal t) {
		if (t instanceof MyInteger)
			return __lt((MyInteger) t);
		return getRealValue().compareTo(t.getRealValue()) < 0;
	}

	public boolean __lt(MyInteger t) {
		return getRealValue().compareTo(new BigDecimal(t.getIntValue())) < 0;
	}

	@ExportAsMethod(type = Operation.LT)
	public static MyObj __lt__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal)
				return new MyBoolean(((MyReal) obj0).__lt((MyReal) obj1));
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	public boolean __le(MyReal t) {
		if (t instanceof MyInteger)
			return __le((MyInteger) t);
		return getRealValue().compareTo(t.getRealValue()) <= 0;
	}

	public boolean __le(MyInteger t) {
		return getRealValue().compareTo(new BigDecimal(t.getIntValue())) <= 0;
	}

	@ExportAsMethod(type = Operation.LE)
	public static MyObj __le__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyReal) {
			if (obj1 instanceof MyReal)
				return new MyBoolean(((MyReal) obj0).__le((MyReal) obj1));
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer", "float");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"integer", "float");
	}

	@ExportAsMethod(type = Operation.NEG)
	public static MyObj __neg__(List<MyObj> ll) throws InterpreterException {
		MyObj obj = ll.get(0);
		if ((obj instanceof MyReal) && !(obj instanceof MyInteger))
			return new MyReal(((MyReal) obj).getRealValue().negate());
		throw InterpreterException
				.parameterTypeError(1, obj.getType(), "float");
	}

}

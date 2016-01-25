package polite.interpreter.basicTypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import polite.abstractLanguage.Operation;
import polite.interpreter.InterpreterException;

public class MyInteger extends MyReal {

	private BigInteger value;
	private static BigInteger INT_ZERO = BigInteger.ZERO;

	protected MyInteger() {
		super(BaseType.INTEGER);
		setIntValue(null);
	}

	public MyInteger(String s) {
		this();
		setValue(new BigInteger(s));
	}

	public MyInteger(MyInteger obj){
		this();
		setIntValue(new BigInteger(obj.getIntValue().toString()));
	}
	public MyInteger(MyReal obj) {
		this();
		setIntValue(obj.getRealValue().toBigInteger());
	}

	public MyInteger(BigInteger obj) {
		this();
		setIntValue(obj);
	}

	private void setIntValue(BigInteger value) {
		this.value = value;
	}

	public void setValue(BigInteger value) {
		setIntValue(value);
	}

	public Object getValue() {
		return this.getIntValue();
	}

	public BigInteger getIntValue() {
		return this.value;
	}

	public String toString() {
		if (getIntValue() == null)
			return getType().toString();
		return getIntValue().toString();
	}

	public boolean __notzero() {
		return INT_ZERO.compareTo(getIntValue()) != 0;
	}

	public MyReal __add(MyReal t) {
		if (t instanceof MyInteger)
			return __add((MyInteger) t);
		return t.__add(this);
	}

	public MyReal __add(MyInteger t) {
		return new MyInteger(getIntValue().add(t.getIntValue()));
	}

	public MyReal __sub(MyReal t) {
		if (t instanceof MyInteger)
			return __sub((MyInteger) t);
		return new MyReal(new BigDecimal(this.getIntValue()).subtract(t
				.getRealValue()));
	}

	public MyReal __sub(MyInteger t) {
		return new MyInteger(this.getIntValue().subtract(t.getIntValue()));
	}

	public MyReal __mult(MyReal t) {
		if (t instanceof MyInteger)
			return __mult((MyInteger) t);
		return t.__mult(this);
	}

	public MyReal __mult(MyInteger t) {
		return new MyInteger(getIntValue().multiply(t.getIntValue()));
	}

	public MyReal __div(MyReal t) {
		if (t instanceof MyInteger)
			return __div((MyInteger) t);
		return new MyReal(new BigDecimal(this.getIntValue()).divide(t
				.getRealValue()));
	}

	public MyReal __div(MyInteger t) {
		return new MyInteger(this.getIntValue().divide(t.getIntValue()));
	}

	public MyReal __mod(MyReal t) {
		if (t instanceof MyInteger)
			return __mod((MyInteger) t);
		return new MyReal(new BigDecimal(this.getIntValue()).remainder(t
				.getRealValue()));
	}

	public MyReal __mod(MyInteger t) {
		return new MyInteger(this.getIntValue().mod(t.getIntValue()));
	}

	public boolean __eq(MyReal t) {
		if (t instanceof MyInteger)
			return __eq((MyInteger) t);
		return t.__eq(this);
	}

	public boolean __eq(MyInteger t) {
		return getIntValue().compareTo(t.getIntValue()) == 0;
	}
	
	public boolean __gt(MyReal t){
		if(t instanceof MyInteger)
			return __gt((MyInteger)t);
		return t.__lt(this);
	}
	public boolean __gt(MyInteger t){
		return getIntValue().compareTo(t.getIntValue())>0;
	}
	public boolean __ge(MyReal t){
		if(t instanceof MyInteger)
			return __ge((MyInteger)t);
		return t.__le(this);
	}
	public boolean __ge(MyInteger t){
		return getIntValue().compareTo(t.getIntValue())>=0;
	}
	public boolean __lt(MyReal t){
		if(t instanceof MyInteger)
			return __lt((MyInteger)t);
		return t.__gt(this);
	}
	public boolean __lt(MyInteger t){
		return getIntValue().compareTo(t.getIntValue())<0;
	}
	public boolean __le(MyReal t){
		if(t instanceof MyInteger)
			return __le((MyInteger)t);
		return t.__ge(this);
	}
	public boolean __le(MyInteger t){
		return getIntValue().compareTo(t.getIntValue())<=0;
	}

	@ExportAsMethod(type= Operation.NEG)
	public static MyObj __neg__(List<MyObj> ll) throws InterpreterException{
		MyObj obj = ll.get(0);
		if( obj instanceof MyInteger)
			return new MyInteger(((MyInteger)obj).getIntValue().negate());
		throw InterpreterException.parameterTypeError(1, obj.getType(), "float");
	}
}

package polite.interpreter.basicTypes;

import java.util.ArrayList;
import java.util.List;

import polite.abstractLanguage.Operation;
import polite.interpreter.InterpreterException;


public class MyTuple extends MyObj {
	private ArrayList<MyObj> value;

	private char startDelim;
	private char endDelim;

	protected MyTuple() {
		super(BaseType.TUPLE);
		setListValue(new ArrayList<MyObj>());
		setDelimiter('(', ')');
	}

	protected MyTuple(BaseType type) {
		super(type);
		setListValue(new ArrayList<MyObj>());
	}

	protected MyTuple(ArrayList<MyObj> that) {
		this();
		setListValue(new ArrayList<MyObj>(that));
	}

	public MyTuple(MyTuple pl) {
		this();
		setListValue(pl.getListValue());
	}

	public MyTuple(MyList pl) {
		this();
		setListValue(pl.getListValue());
	}

	protected void setDelimiter(char start, char end) {
		this.startDelim = start;
		this.endDelim = end;
	}

	protected char getStartDelim() {
		return this.startDelim;
	}

	protected char getEndDelim() {
		return this.endDelim;
	}

	public void setListValue(ArrayList<MyObj> list) {
		value = new ArrayList<MyObj>(list);
	}

	public ArrayList<MyObj> getListValue() {
		return value;
	}

	final public Object getValue() {
		return getListValue();
	}

	final public void setValue(ArrayList<MyObj> ll) {
		setListValue(ll);
	}

	final protected String selfRecursiveString() {
		return getStartDelim() + "..." + getEndDelim();
	}

	final protected StringBuilder getListString(StringBuilder sb,
			ArrayList<MyTuple> ll) {
		boolean start = true;
		sb.append(this.getStartDelim());
		for (MyObj obj : this.getListValue()) {
			sb.append(start ? " " : ", ");
			start = false;
			if (obj instanceof MyTuple) {
				MyTuple tp = (MyTuple) obj;
				if (ll.contains(tp))
					sb.append(tp.selfRecursiveString());
				else {
					ll.add(tp);
					sb = tp.getListString(sb, ll);
				}
			} else {
				sb.append(obj.toString());
			}
		}
		if (!start)
			sb.append(' ');
		sb.append(this.getEndDelim());
		return sb;
	}

	final public String toString() {
		ArrayList<MyTuple> ll = new ArrayList<MyTuple>();
		ll.add(this);
		return getListString(new StringBuilder(), ll).toString();
	}

	final public int __len() {
		return getListValue().size();
	}

	public void append(MyObj obj) {
		getListValue().add(obj);
	}

	public boolean __notzero() {
		return getListValue().size() != 0;
	}

	public MyObj __get(int i) throws InterpreterException {
		int index = i;
		if (i < 0)
			index = index + getListValue().size();
		if (index < 0 || index >= __len())
			return MyConstant.None;
		return getListValue().get(index);

	}

	@ExportAsMethod(type = Operation.LEN)
	public static MyInteger __len__(List<MyObj> params)
			throws InterpreterException {
		MyObj obj = params.get(0);
		if (obj instanceof MyTuple)
			return new MyInteger(((MyTuple) obj).__len() + "");
		if( obj instanceof MyString)
			return new MyInteger(((MyString) obj).__len() +"");
		throw InterpreterException.parameterTypeError(1, obj.getType(), "list","tuple","string");
	}

	@ExportAsMethod(type = Operation._DEFINED_OP, name = "get", paramSize = 2)
	public static MyObj get__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		MyObj obj1 = ll.get(1);
		if (obj0 instanceof MyTuple) {
			if (obj1 instanceof MyInteger) {
				MyTuple tuple = (MyTuple) obj0;
				int i = ((MyInteger) obj1).getIntValue().intValue();
				return tuple.__get(i);
			}
			throw InterpreterException.parameterTypeError(2, obj1.getType(),
					"integer");
		}
		throw InterpreterException.parameterTypeError(1, obj0.getType(),
				"tuple", "list");
	}

	protected MyTuple __new() {
		return new MyTuple();
	}
	
	@ExportAsMethod(type=Operation._DEFINED_OP,name="sub",paramSize=3)
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
		MyTuple tuple = (MyTuple) obj0;
		int begin = ((MyInteger) obj1).getIntValue().intValue();
		int end = ((MyInteger) obj2).getIntValue().intValue();
		MyTuple mt = tuple.__new();
		mt.getListValue().ensureCapacity(begin<end?end-begin:0);
		while(begin<end){
			mt.append(tuple.__get(begin));
			begin++;
		}
		return mt;
	}

	@ExportAsMethod(type=Operation._DEFINED_OP,name="as_list",paramSize=1,excludesClasses={MyList.class})
	public static MyObj as_list__(List<MyObj> ll)throws InterpreterException{
		MyObj obj0=ll.get(0);
		if(!(obj0.getClass().equals(MyTuple.class)))
			throw InterpreterException.parameterTypeError(1, obj0.getType(), "tuple");
		return new MyList(((MyTuple)obj0).getListValue());
	}
	
	final public boolean __eq(MyTuple t){
		return getListValue().equals(t.getListValue());
	}
	
	final public boolean equals(Object o){
		if(this==o) return true;
		if(o instanceof MyTuple)
			return this.__eq((MyTuple)o);
		return false;
	}

}

package polite.interpreter.basicTypes;

import java.util.ArrayList;
import java.util.List;

import polite.abstractLanguage.Operation;
import polite.interpreter.InterpreterException;


public class MyList extends MyTuple {

	public MyList() {
		super(BaseType.LIST);
		setDelimiter('[', ']');
	}

	protected MyList(ArrayList<MyObj> that) {
		this();
		setListValue(new ArrayList<MyObj>(that));
	}

	protected MyTuple __new() {
		return new MyList();
	}

	public MyObj __set(int ind, MyObj obj) {
		if (ind < 0)
			ind += __len();
		if (ind < 0) {
			ArrayList<MyObj> newList = new ArrayList<MyObj>();
			newList.ensureCapacity(Math.abs(ind) + getListValue().size());
			while (ind != 0) {
				newList.add(MyConstant.None);
				++ind;
			}
			newList.addAll(getListValue());
			this.setListValue(newList);
		}
		if (ind >= __len()) {
			getListValue().ensureCapacity(ind);
			int diff = ind - __len() + 1;
			while (diff != 0) {
				append(MyConstant.None);
				diff--;
			}
		}
		return getListValue().set(ind, obj);
	}

	public MyObj __add(int ind, MyObj obj) {
		int rind = ind;
		if (rind < 0)
			rind += __len();
		if (rind < 0 || rind > __len())
			return __set(ind, obj);
		getListValue().add(rind, obj);
		return MyConstant.None;
	}
	private MyObj __remove(int ind) {
		if(ind<0)ind+=__len();
		if(ind<0||ind>=__len())
			return MyConstant.None;
		return getListValue().remove(ind);
	}


	@ExportAsMethod(type = Operation._DEFINED_OP, name = "as_tuple", paramSize = 1)
	public static MyObj as_list__(List<MyObj> ll) throws InterpreterException {
		MyObj obj0 = ll.get(0);
		if (!(obj0.getClass().equals(MyTuple.class)))
			throw InterpreterException.parameterTypeError(1, obj0.getType(),
					"tuple");
		return new MyTuple(((MyList) obj0).getListValue());
	}

	@ExportAsMethod(type = Operation._DEFINED_OP, name = "set", paramSize = 3)
	public static MyObj set__(List<MyObj> ll) throws InterpreterException {
		MyObj obj1 = ll.get(0);
		MyObj obj2 = ll.get(1);
		MyObj obj3 = ll.get(2);
		if (!(obj1 instanceof MyList))
			throw InterpreterException.parameterTypeError(1, obj1.getType(),
					"list");
		if (!(obj2 instanceof MyInteger))
			throw InterpreterException.parameterTypeError(2, obj2.getType(),
					"integer");
		MyList ml = (MyList) obj1;
		MyInteger ind = (MyInteger) obj2;
		return ml.__set(ind.getIntValue().intValue(), obj3);
	}

	@ExportAsMethod(type = Operation._DEFINED_OP, name = "append", paramSize = 2)
	public static MyObj append__(List<MyObj> ll) throws InterpreterException {
		MyObj obj1 = ll.get(0);
		MyObj obj2 = ll.get(1);
		if (!(obj1 instanceof MyList))
			throw InterpreterException.parameterTypeError(1, obj1.getType(),
					"list");
		((MyList) obj1).append(obj2);
		return MyConstant.None;
	}

	@ExportAsMethod(type = Operation._DEFINED_OP, name = "add", paramSize = 3)
	public static MyObj add__(List<MyObj> ll) throws InterpreterException {
		MyObj obj1 = ll.get(0);
		MyObj obj2 = ll.get(1);
		MyObj obj3 = ll.get(2);
		if (!(obj1 instanceof MyList))
			throw InterpreterException.parameterTypeError(1, obj1.getType(),
					"list");
		if (!(obj2 instanceof MyInteger))
			throw InterpreterException.parameterTypeError(2, obj2.getType(),
					"integer");
		MyList ml = (MyList) obj1;
		MyInteger ind = (MyInteger) obj2;
		return ml.__add(ind.getIntValue().intValue(), obj3);
	}

	@ExportAsMethod(type = Operation._DEFINED_OP, name = "remove", paramSize = 2)
	public static MyObj remove__(List<MyObj> ll) throws InterpreterException {
		MyObj obj1 = ll.get(0);
		MyObj obj2 = ll.get(1);
		if (!(obj1 instanceof MyList))
			throw InterpreterException.parameterTypeError(1, obj1.getType(),
					"list");
		if (!(obj2 instanceof MyInteger))
			throw InterpreterException.parameterTypeError(2, obj2.getType(),
					"integer");
		MyList ml = (MyList) obj1;
		MyInteger ind = (MyInteger) obj2;
		return ml.__remove(ind.getIntValue().intValue());
	}

	
}

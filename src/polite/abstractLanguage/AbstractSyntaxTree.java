package polite.abstractLanguage;

import java.io.File;
import java.io.PrintStream;

import polite.abstractLanguage.Node.Module;


public class AbstractSyntaxTree {
	private Module mainModule;
	public AbstractSyntaxTree(){
		mainModule=new Module("__main__");
	}
	public Module getMainModule() {
		return mainModule;
	}
	public boolean validateTree(){
		return mainModule.isValid(null);
	}
	public String printTree(String fileName){
		try{
			File f = new File(fileName);
			Node.setOutput(new PrintStream(f));
			mainModule.print();
			return f.getAbsolutePath();
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public void printTree(){
		Node.setOutput(System.out);
		mainModule.print();
	}
}

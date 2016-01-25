package polite;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import polite.abstractLanguage.AbstractSyntaxTree;
import polite.interpreter.Interpreter;
import polite.parser.Parser;
import polite.util.Logger;

public class polite {

	// abstract syntax tree save location, stands for intermediate language file
	public final static String defaultILExtension = ".ilf";

	public static void main(String... argv) throws Exception {
		InputStream in;
		AbstractSyntaxTree ast;
		String iloPath = "";
		if (argv.length == 0) {
			// in = System.in;
			// iloPath = defaultILName+defaultILExtension;
			System.out.println("Enter path name to file.");
			return;
		} else {
			// pretty sloppy, but it works .... might come back to fix this?
			// FIXME: perhaps?
			String fileName = argv[0];
			File file;
			if (!new File(fileName).exists())
				if (new File(System.getProperty("user.dir"), fileName).exists())
					fileName = new File(System.getProperty("user.dir"),
							fileName).getAbsolutePath();
				else {
					System.out.println("no file found");
					return;
				}

			file = new File(fileName);
			in = new FileInputStream(file);
			iloPath = new File(file.getParent(), file.getName().split("\\.")[0]
					+ defaultILExtension).getAbsolutePath();
		}
		Parser parser = new Parser(in);
		ast = parser.start();
		if (ast == null) {
			return;
		}
		@SuppressWarnings("unused")
		String savedPath = ast.printTree(iloPath);
		// System.out.println("Syntax tree saved to: "+savedPath);
		if (!ast.validateTree()) {
			System.err.println("Failed ast validation");
		} else {
			// System.out.println("Program Output>>>");
			Interpreter.run(ast);
		}

		if (Logger.hasPropertiesAdded())
			Logger.writePropertyFile();

	}
}

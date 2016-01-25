package polite.interpreter.basicTypes;

import polite.abstractLanguage.Operation;

public @interface ExportFunctionAs {
	Operation type();
	String name() default "";
}

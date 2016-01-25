package polite.interpreter.basicTypes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface ExportAsFunction {
	String name();
	int paramSize();
	boolean extendedParam() default false;
}

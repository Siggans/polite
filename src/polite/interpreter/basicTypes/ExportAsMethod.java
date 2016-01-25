package polite.interpreter.basicTypes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import polite.abstractLanguage.Operation;


@Retention(RetentionPolicy.RUNTIME)

public @interface ExportAsMethod {
	Operation type();
	String name() default "";
	int paramSize() default 0;
	boolean extendedParam() default false;
	Class<?>[] excludesClasses() default {};
}

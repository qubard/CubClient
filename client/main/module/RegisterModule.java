package client.main.module;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.*;
import static java.lang.annotation.ElementType.*;

@Retention(value = RUNTIME)
@Target(value = TYPE)
public @interface RegisterModule {

	int key() default -1;

	boolean pressed() default false;

	boolean listed() default false;

	int color() default 0xFFFFFF;

	int secondary_color() default 0xFFFFFF;
}

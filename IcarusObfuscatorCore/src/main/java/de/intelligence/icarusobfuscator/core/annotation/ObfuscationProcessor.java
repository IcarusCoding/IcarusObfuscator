package de.intelligence.icarusobfuscator.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ObfuscationProcessor {

    String name();

    String description() default "";

    int priority() default 0;

    //TODO add minimum java version

}

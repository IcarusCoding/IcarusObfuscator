package de.intelligence.icarusobfuscator.core.annotation;

import de.intelligence.icarusobfuscator.core.utils.JavaVersion;

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

    JavaVersion minVersion() default JavaVersion.JAVA_1_1;

}

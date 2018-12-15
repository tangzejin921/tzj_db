package com.tzj.db.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldTag {
	FieldType type();
	boolean isKey() default false;
	int length() default 64;//type 为varchar时才有必要加这个
}

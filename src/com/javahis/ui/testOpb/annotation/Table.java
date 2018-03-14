package com.javahis.ui.testOpb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TABLE
 * @author zhangp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.TYPE })
public @interface Table {
	/**
	 * ±íÃû
	 * @return
	 */
	String tableName();
}

package io.box.db.executor.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰消息类和业务逻辑执行类
 * @author xingchencheng
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageAnnotation {
	int msgType();  //用于消息区分
}
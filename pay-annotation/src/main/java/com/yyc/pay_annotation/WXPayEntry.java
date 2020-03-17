package com.yyc.pay_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ==========================
 *
 * @author yuanyanchao <a href="mailto:lenglong110@qq.com">Contact me.</a>
 * @date 2019-06-17
 * ==========================
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface WXPayEntry {
    String packageName();
}

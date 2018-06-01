package com.hao.demo.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @Desc
 * @Author linguoding
 * @Email lingguodingg@gmail.com
 * @Date 2018/5/10
 */
@Documented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScoped {
}

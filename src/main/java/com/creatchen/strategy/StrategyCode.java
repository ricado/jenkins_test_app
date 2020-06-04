package com.creatchen.strategy;

import java.lang.annotation.*;

/**
 * @author chen cy
 * @date 2020/5/27 11:41 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StrategyCode {
    String[] value();
}

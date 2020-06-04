package com.creatchen.strategy;

import lombok.Getter;

/**
 * @author chen cy
 * @date 2020/5/30 6:35 下午
 */
@Getter
public enum StrategyError {
    STRATEGY_NON_EXISTENT("10240300", "【%s】策略不存在"),
    STRATEGY_CODE_NON_EXISTENT("10240301", "【%s】策略的code值为【%s】不存在"),
    STRATEGY_CODE_REPEAT("10240302", "【%s】策略类code值为[%s]存在重复,[%s,%s]"),
    STRATEGY_LOST_ANNOTATION("10240302", "策略类的实现类[%s]缺少@StrategyCode或@StrategyIntCode注解"),
    ;

    private String code;

    private String msg;

    StrategyError(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public StrategyException get(){
        return new StrategyException(getMsg());
    }

    public StrategyException format(Object... valus){
        String msg = String.format(getMsg(),valus);
        return new StrategyException(msg);
    }
}

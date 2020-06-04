package com.creatchen.jenkins.sgy;

import com.creatchen.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 人
 * @author chen cy
 * @date 2020/5/28 12:56 上午
 */
@Strategy("people")
public interface PeopleSgy {

    Logger logger = LoggerFactory.getLogger(PeopleSgy.class);

    /**
     * 默认方法使用sad
     * @return String
     */
    default String sayAndLog(){
        String say = say();
        logger.info(say);
        return say;
    }

    /**
     * say
     * @return String
     */
    String say();
}

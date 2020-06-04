package com.creatchen.jenkins.sgy;

import com.creatchen.strategy.StrategyCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author chen cy
 * @date 2020/5/28 12:57 上午
 */
@Slf4j
@Service
@StrategyCode("World")
public class WorldSgy implements PeopleSgy {
    @Override
    public String say() {
        return "hello world";
    }
}

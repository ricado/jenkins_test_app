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
@StrategyCode("China")
public class ChineseSgy implements PeopleSgy {
    @Override
    public String say() {
        return "我是中国人";
    }
}

package com.creatchen.main;

import com.creatchen.jenkins.sgy.PeopleSgy;
import com.creatchen.strategy.StrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class Demo01ApplicationTests {

	@Autowired
	private StrategyFactory strategyFactory;

	@Test
	void contextLoads() {
		PeopleSgy peopleSgy = strategyFactory.getSgy("China", PeopleSgy.class);
		log.info(peopleSgy.say());
	}

}

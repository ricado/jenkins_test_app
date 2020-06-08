package com.creatchen.main;

import com.creatchen.jenkins.sgy.PeopleSgy;
import com.creatchen.strategy.StrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@SpringBootApplication
@ComponentScan("com")
@RestController
public class MainApplication {

    @Autowired
    private StrategyFactory strategyFactory;

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) throws InvocationTargetException, IllegalAccessException {
        String result = (String) strategyFactory.execute2(name, PeopleSgy.class);
        log.info("result : {}",result);
        return String.format("Hello,%s!\n%s", name,result);
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
     }

}

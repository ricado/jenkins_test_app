package com.creatchen.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author chen cy
 * @date 2020/5/28 1:26 上午
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext arg0){
        SpringUtil.applicationContext = arg0;
    }

    public static ApplicationContext getApplicationContext(){
        return SpringUtil.applicationContext;
    }

}

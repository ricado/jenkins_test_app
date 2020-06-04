package com.creatchen.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 策略类工厂
 * 维护策略的映射关系,获取
 *
 * @author creatchen
 * @date 2020/5/27 11:50 下午
 */
@Slf4j
@Service
public class StrategyFactory implements InitializingBean {

    /**
     * 策略表
     */
    protected static final Table<Class<?>, String, Object> STRATEGY_TABLE = HashBasedTable.create();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        // 获取@Strategy的Bean
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(Strategy.class);
        if (MapUtils.isEmpty(map)) {
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("策略类中心初始化");
        // 遍历@Strategy的Bean哈希表,制作策略表STRATEGY_TABLE
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            // 策略实现类名称
            String sgyImplName = clazz.getSimpleName();
            log.info("Strategy impl class name :{}", clazz.getSimpleName());
            // 实现类的@StrategyCode
            String[] codes = getCodes(clazz);
            // 获取策略实现类实现的接口数组
            List<Class<?>> classes = getStrategyInterfaces(clazz);
            classes.forEach(aClass -> {
                log.info("interface : {}" + aClass.getSimpleName());
                String sgyName = aClass.getSimpleName();
                for (String code : codes) {
                    if (STRATEGY_TABLE.contains(aClass, code)) {
                        String repeatName = STRATEGY_TABLE.get(aClass, code).getClass().getSimpleName();
                        throw StrategyError.STRATEGY_CODE_REPEAT.format(sgyName, code, sgyImplName, repeatName);
                    }
                    STRATEGY_TABLE.put(aClass, code, entry.getValue());
                }
            });
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        // 打印策略表
        showTable();
    }

    /**
     * 获取使用@Strategy的接口
     *
     * @param clazz clazz
     * @return return
     */
    private List<Class<?>> getStrategyInterfaces(Class<?> clazz) {
        List<Class<?>> list = new ArrayList<>();
        Class<?>[] classes = clazz.getInterfaces();
        Arrays.stream(classes)
                // 从接口中过滤掉没有使用@Strategy注解的
                .filter(aClass -> aClass.isAnnotationPresent(Strategy.class))
                .forEach(list::add);
        Optional.ofNullable(clazz.getSuperclass())
                .ifPresent(superclass -> list.addAll(getStrategyInterfaces(superclass)));
        return list;
    }

    /**
     * 展示策略树
     */
    private void showTable() {
        if (STRATEGY_TABLE.isEmpty()) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        for (Class<?> aClass : STRATEGY_TABLE.rowKeySet()) {
            Map<String, Object> map = STRATEGY_TABLE.row(aClass);
            Map<String, Object> sgyMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sgyMap.put(entry.getKey(), entry.getValue().getClass().getSimpleName());
            }
            jsonObject.put(aClass.getSimpleName(), sgyMap);
        }
        log.info("STRATEGY_TABLE:\n{}", JSON.toJSONString(jsonObject, true));
    }

    /**
     * 获取实现类的所有绑定的编码
     * @param clazz clazz
     * @return String[]
     */
    private String[] getCodes(Class<?> clazz) {
        StrategyCode strategyCode = clazz.getAnnotation(StrategyCode.class);
        if (null == strategyCode) {
            StrategyIntCode strategyIntCode = clazz.getAnnotation(StrategyIntCode.class);
            if (null == strategyIntCode) {
                // 缺少注解则抛出异常
                throw StrategyError.STRATEGY_LOST_ANNOTATION.format(clazz.getSimpleName());
            }
            int[] codes = strategyIntCode.value();
            String[] codeStrArray = new String[0];
            for (int code : codes) {
                codeStrArray = ArrayUtils.add(codeStrArray, String.valueOf(code));
            }
            return codeStrArray;
        }
        return strategyCode.value();
    }

    /**
     * 获取指定的策略类,获取不到则抛出异常
     *
     * @param code  code
     * @param clazz clazz
     * @param <T>   <T>
     * @return T
     */
    public <T> T getSgyWithException(String code, Class<T> clazz) {
        if (!STRATEGY_TABLE.containsRow(clazz)) {
            throw StrategyError.STRATEGY_NON_EXISTENT.format(clazz.getSimpleName());
        }
        if (!STRATEGY_TABLE.contains(clazz, code)) {
            throw StrategyError.STRATEGY_CODE_NON_EXISTENT.format(clazz.getSimpleName(), code);

        }
        return (T) STRATEGY_TABLE.row(clazz).get(code);
    }

    /**
     * 获取指定的策略类,获取不到则抛出异常
     *
     * @param code  code
     * @param clazz clazz
     * @param <T>   <T>
     * @return T
     */
    public <T> T getSgyWithException(int code, Class<T> clazz) {
        String str = String.valueOf(code);
        return getSgyWithException(str, clazz);
    }

    /**
     * 获取指定的策略类,获取不到则返回null
     *
     * @param code  code
     * @param clazz clazz
     * @param <T>   <T>
     * @return T
     */
    public <T> T getSgy(String code, Class<T> clazz) {
        if (!STRATEGY_TABLE.contains(clazz, code)) {
            return null;
        }
        Object object = STRATEGY_TABLE.row(clazz).get(code);
        return (T) object;
    }

    /**
     * 获取指定的策略类,获取不到则返回null
     *
     * @param code  code
     * @param clazz clazz
     * @param <T>   <T>
     * @return T
     */
    public <T> T getSgy(int code, Class<T> clazz) {
        String str = String.valueOf(code);
        return getSgy(str, clazz);
    }

    /**
     * 获取策略类并且执行第一个方法
     * @param code code 策略类编码
     * @param tClass tClass 策略类接口
     * @param args args 策略类方法参数
     * @param <T> T
     * @return Pair left为是否找到该策略类 right为反参
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException IllegalAccessException
     */
    public <T> Pair<Boolean,Object> execute(String code, Class<T> tClass, Object... args) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = tClass.getMethods();
        return execute(code,tClass,methods[0],args);
    }

    /**
     * 获取策略类并且执行指定方法
     * @param code code 策略类编码
     * @param tClass tClass 策略类接口
     * @param method 指定方法
     * @param args args 策略类方法参数
     * @param <T> T
     * @return Pair left为是否找到该策略类 right为反参
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException IllegalAccessException
     */
    public <T> Pair<Boolean,Object> execute(String code,Class<T> tClass,Method method,Object... args) throws InvocationTargetException, IllegalAccessException {
        T sgy = getSgy(code,tClass);
        if(sgy == null){
            return new ImmutablePair<>(false,null);
        }
        return new ImmutablePair<>(true,method.invoke(sgy,args));
    }

    public <T> Object execute2(String code,Class<T> tClass,Object... args) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = tClass.getMethods();
        log.info("method name : {}",methods[0].getName());
        return execute2(code,tClass,methods[0],args);
    }

    public <T> Object execute2(String code, Class<T> tClass, Method method,Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(getSgy(code,tClass),args);
    }
}

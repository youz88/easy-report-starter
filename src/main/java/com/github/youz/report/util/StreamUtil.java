package com.github.youz.report.util;

import com.mybatisflex.core.util.ArrayUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合工具类
 */
public class StreamUtil {

    /**
     * 抽取集合中的某个属性为一个集合
     *
     * @param list    集合
     * @param mapping 查询属性的方法
     * @param filters 筛选条件
     * @param <E>     集合中的元素类型
     * @param <T>     属性类型
     * @return 属性的集合
     */
    @SafeVarargs
    public static <E, T> List<T> toList(List<E> list, Function<E, T> mapping, Predicate<E>... filters) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return appendFilters(list.stream(), filters)
                .map(mapping)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 抽取集合中的某个属性为一个集合
     *
     * @param list    集合
     * @param mapping 查询属性的方法
     * @param <E>     集合中的元素类型
     * @param <T>     属性类型
     * @return 属性的集合
     */
    @SafeVarargs
    public static <E, T> Set<T> toSet(List<E> list, Function<E, T> mapping, Predicate<E>... filters) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptySet();
        }
        return appendFilters(list.stream(), filters)
                .map(mapping)
                .collect(Collectors.toSet());
    }

    /**
     * @param list         集合
     * @param keyMapping   key查询属性的方法
     * @param valueMapping value查询属性的方法
     * @param filters      过滤条件
     * @param <E>          集合中的元素类型
     * @param <K>          key
     * @param <V>          value
     * @return map
     */
    @SafeVarargs
    public static <E, K, V> Map<K, V> toMap(Collection<E> list, Function<E, K> keyMapping, Function<E, V> valueMapping, Predicate<? super E>... filters) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return appendFilters(list.stream(), filters).collect(Collectors.toMap(keyMapping, valueMapping, (oldKey, newKey) -> oldKey));
    }


    /**
     * 添加过滤条件
     *
     * @param stream  数据流
     * @param filters 过滤条件
     * @param <E>     集合中的元素类型
     * @return 数据流
     */
    @SafeVarargs
    private static <E> Stream<E> appendFilters(Stream<E> stream, Predicate<? super E>... filters) {
        if (ArrayUtil.isNotEmpty(filters)) {
            for (Predicate<? super E> filter : filters) {
                stream = stream.filter(filter);
            }
        }
        return stream;
    }

}

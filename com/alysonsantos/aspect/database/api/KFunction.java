package com.alysonsantos.aspect.database.api;

import java.util.function.Function;

/**
 * Is trying block functional interface
 *
 * @param <T> input generic param, has used to ResultSet
 * @param <R> out generic param, has used to returns an object type
 */
public interface KFunction<T, R> extends Function<T, R> {

    @Override
    default R apply(T t) {
        try {
            return kApply(t);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    R kApply(T t) throws Exception;
}
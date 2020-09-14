package com.alysonsantos.aspect.database.api;

import java.sql.ResultSet;
import java.util.Collection;

/**
 * The interface has provide basic methods
 *
 * @author zkingboos_
 */
public interface JdbcProvider {

    boolean openConnection();

    JdbcProvider preOpen();

    void closeConnection();

    boolean hasConnection();

    void update(String query, Object... objects);

    <K> Collection<K> queryCollection(String query, KFunction<ResultSet, K> function, Object... objects);

    <K> K query(String query, KFunction<ResultSet, K> consumer, Object... objects);

    void close(AutoCloseable... closeables);
}
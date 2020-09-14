package com.alysonsantos.aspect.repository;

import com.alysonsantos.aspect.database.api.JdbcProvider;
import com.alysonsantos.aspect.database.api.KFunction;

import java.sql.ResultSet;
import java.util.Collection;

public interface Repository<R, K> {

    JdbcProvider getProvider();

    KFunction<ResultSet, K> getDeserializer();

    Collection<K> selectAll();

    K selectOne(R r);

    void insert(R r, K k);

    void update(R r, K k);

    void delete(R r);

}

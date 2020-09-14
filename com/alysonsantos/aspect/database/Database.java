package com.alysonsantos.aspect.database;

import com.alysonsantos.aspect.database.api.JdbcProvider;
import com.alysonsantos.aspect.AspectSpawners;

public interface Database {

    void startDatabase(AspectSpawners plugin);

    JdbcProvider getJdbcProvider();

}

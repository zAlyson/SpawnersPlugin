package com.alysonsantos.aspect.database;

import com.alysonsantos.aspect.database.api.JdbcProvider;
import com.alysonsantos.aspect.database.mysql.UniversalCredentials;
import com.alysonsantos.aspect.AspectSpawners;

public class MySQLDatabase implements Database {

    private JdbcProvider provider;

    public MySQLDatabase(AspectSpawners plugin) {
        this.startDatabase(plugin);
    }

    @Override
    public void startDatabase(AspectSpawners plugin) {
        final UniversalWrapper wrapper = new UniversalWrapper();
        final JdbcProvider provider = wrapper.newMysqlProvider(
                new UniversalCredentials(
                        "189.127.164.52",
                        "mc_1888",
                        "mc_1888",
                        "f22e4fe789"),
                20
        ).preOpen();

        this.provider = provider;
    }

    @Override
    public JdbcProvider getJdbcProvider() {
        return this.provider;
    }
}

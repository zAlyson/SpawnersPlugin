package com.alysonsantos.aspect.database;

import com.alysonsantos.aspect.database.api.JdbcProvider;
import com.alysonsantos.aspect.database.mysql.MysqlProvider;
import com.alysonsantos.aspect.database.mysql.UniversalCredentials;

public final class UniversalWrapper {

    public JdbcProvider newMysqlProvider(UniversalCredentials credentials, int maxConnections) {
        return new MysqlProvider(
                credentials, maxConnections
        );
    }
}

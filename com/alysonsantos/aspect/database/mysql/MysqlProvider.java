package com.alysonsantos.aspect.database.mysql;

import com.alysonsantos.aspect.database.Utility;
import com.alysonsantos.aspect.database.api.JdbcProvider;
import com.alysonsantos.aspect.database.api.KFunction;
import com.alysonsantos.aspect.database.api.KRunnable;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The provider for mysql for universal
 *
 * @author zkingboos_
 */
@Getter
@RequiredArgsConstructor
public class MysqlProvider extends PoolableConnection implements JdbcProvider {

    private final UniversalCredentials credentials;
    private final int maxConnections;

    @Setter
    private HikariDataSource source;

    /**
     * Close the all connections of datasource
     */
    @Override
    public void closeConnection() {
        getSource().close();
    }

    /**
     * Verify if the connections is valid
     *
     * @return if an any valid connection
     */
    @Override
    public boolean hasConnection() {
        return openConnection();
    }

    /**
     * Connect the all connections on mysql server
     *
     * @return if has a valid connection
     */
    @Override
    public boolean openConnection() {
        try {
            Connection connection = getSource().getConnection();
            final boolean result = connection != null && !connection.isClosed();
            close(connection);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Used to set hikaridatasource
     */
    @Override
    public JdbcProvider preOpen() {
        setSource(obtainDataSource(credentials, maxConnections));
        openConnection();
        return this;
    }

    /**
     * Uses just in select query
     *
     * @param query    the query of mysql
     * @param function if has a valid entry, function will be called and returns a result
     * @param objects  the objects that will be putted in the prepared statment
     * @param <K>      the generic type, used to return your prefer value
     * @return returns a optional value, applied in function parameter
     */
    @Override
    public <K> K query(
            String query,
            KFunction<ResultSet, K> function,
            Object... objects
    ) {
        try {
            Connection connection = getSource().getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            Utility.syncObjects(ps, objects);

            ResultSet set = ps.executeQuery();
            K result = set != null && set.next() ? function.apply(set) : null;

            //close the connections
            close(set, ps, connection);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Uses just in select query
     *
     * @param query    the query of mysql
     * @param function if has a valid entry, function will be called and returns a result
     * @param objects  the objects that will be putted in the prepared statment
     * @param <K>      the generic type, used to return your prefer value
     * @return returns a optional value, applied in function parameter
     */
    public <K> Collection<K> queryCollection(
            String query,
            KFunction<ResultSet, K> function,
            Object... objects
    ) {
        try {
            Connection connection = getSource().getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            Utility.syncObjects(ps, objects);

            ResultSet set = ps.executeQuery();

            List<K> collection = new LinkedList<>();
            while (set.next()) {
                collection.add(function.apply(set));
            }

            close(set, ps, connection);
            return collection;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Uses just in create, delete, insert and update querys
     *
     * @param query   the query of mysql
     * @param objects the objects that will be putted in the prepared statment
     */
    @Override
    public void update(
            String query,
            Object... objects
    ) {
        KRunnable runnable = () -> {
            Connection connection = getSource().getConnection();
            PreparedStatement ps = connection.prepareStatement(query);

            Utility.syncObjects(ps, objects);
            ps.executeUpdate();

            //close connections
            close(ps, connection);
        };

        runnable.run();
    }

    /**
     * Close the all AutoCloseable instances
     *
     * @param closeables the all closeable connections
     */
    @SneakyThrows
    public void close(AutoCloseable... closeables) {
        for (AutoCloseable close : closeables) {
            close.close();
        }
    }
}
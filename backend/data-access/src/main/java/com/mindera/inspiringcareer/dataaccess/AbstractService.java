package com.mindera.inspiringcareer.dataaccess;

import com.mindera.inspiringcareer.utils.JsonUtils;
import com.mindera.inspiringcareer.utils.Loggable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.rxjava.ext.asyncsql.AsyncSQLClient;
import io.vertx.rxjava.ext.sql.SQLConnection;
import rx.Single;

import java.util.List;

public abstract class AbstractService implements Loggable {

    private final AsyncSQLClient sqlClient;

    public AbstractService(final AsyncSQLClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    protected Single<ResultSet> queryWithParams(final String query, final JsonArray params) {
        final Single<SQLConnection> openConnection = sqlClient.rxGetConnection()
                .cache();

        final Single<Void> closeConnection = openConnection.flatMap(SQLConnection::rxClose);

        return openConnection
                .flatMap(conn -> conn.rxQueryWithParams(query, params))
                .doOnEach(a -> closeConnection.subscribe(success -> logger().debug("Closed connection successfully."),
                        error -> logger().error("Failed to close connection.", error)));
    }

    protected Single<UpdateResult> updateWithParams(final String query, final JsonArray params) {
        final Single<SQLConnection> openConnection = sqlClient.rxGetConnection()
                .cache();

        final Single<Void> closeConnection = openConnection.flatMap(SQLConnection::rxClose);

        return openConnection
                .flatMap(conn -> conn.rxUpdateWithParams(query, params))
                .doOnEach(a -> closeConnection.subscribe(success -> logger().debug("Closed connection successfully."),
                        error -> logger().error("Failed to close connection.", error)));
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    protected JsonObject getSingle(final ResultSet resultSet) {
        final List<JsonObject> rows = resultSet.getRows();
        return rows == null || rows.isEmpty() ? JsonUtils.emptyJsonObject() : rows.get(0);
    }
}

package yandex.cloud.examples.serverless.todo.db;

import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.common.transaction.TxMode;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.query.QueryClient;
import tech.ydb.query.tools.QueryReader;
import tech.ydb.query.tools.SessionRetryContext;
import tech.ydb.table.query.Params;

import java.util.function.Consumer;

public class EntityManager {
    private final SessionRetryContext retryCtx;

    public EntityManager(String database, String endpoint) {
        var connectionUrl = endpoint.contains("://") ? endpoint : "grpcs://" + endpoint;

        var transport = GrpcTransport.forEndpoint(connectionUrl, database)
                .withAuthProvider(CloudAuthHelper.getAuthProviderFromEnviron())
                .build();
        var queryClient = QueryClient.newClient(transport).build();
        this.retryCtx = SessionRetryContext.create(queryClient).build();
    }

    public void execute(String query, Params params, Consumer<QueryReader> callback) {
        var result = retryCtx.supplyResult(session -> QueryReader.readFrom(
                session.createQuery(query, TxMode.SERIALIZABLE_RW, params)
        )).join().getValue();

        if (callback != null) {
            callback.accept(result);
        }
    }

    public void execute(String query, Params params) {
        execute(query, params, null);
    }

}

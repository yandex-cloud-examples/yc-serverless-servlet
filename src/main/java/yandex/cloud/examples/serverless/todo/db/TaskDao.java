package yandex.cloud.examples.serverless.todo.db;

import tech.ydb.table.query.Params;
import tech.ydb.table.values.PrimitiveValue;
import yandex.cloud.examples.serverless.todo.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskDao implements Dao<Task> {

    private static final EntityManager entityManager = new EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"));

    @Override
    public List<Task> findAll() {
        var tasks = new ArrayList<Task>();
        entityManager.execute("SELECT TaskId, Name, Description FROM Tasks", Params.empty(), result -> {
            var resultSet = result.getResultSet(0);
            while (resultSet.next()) {
                tasks.add(Task.fromResultSet(resultSet));
            }
        });
        return tasks;
    }

    @Override
    public void save(Task task) {
        entityManager.execute(
                "DECLARE $taskId AS Utf8;" +
                        "DECLARE $name AS Utf8;" +
                        "DECLARE $description AS Utf8;" +
                        "INSERT INTO Tasks(TaskId, Name, Description, CreatedAt) VALUES ($taskId, $name, $description, CurrentUtcDatetime())",
                Params.of("$taskId", PrimitiveValue.newText(UUID.randomUUID().toString()),
                        "$name", PrimitiveValue.newText(task.getName()),
                        "$description", PrimitiveValue.newText(task.getDescription()))
        );
    }

    @Override
    public void deleteById(String taskId) {
        entityManager.execute(
                "DECLARE $taskId AS Utf8;" +
                        "DELETE FROM Tasks WHERE TaskId = $taskId",
                Params.of("$taskId", PrimitiveValue.newText(taskId)));
    }

}

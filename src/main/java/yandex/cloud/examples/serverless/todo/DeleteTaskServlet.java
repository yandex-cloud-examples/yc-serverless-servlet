package yandex.cloud.examples.serverless.todo;

import yandex.cloud.examples.serverless.todo.db.TaskDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class DeleteTaskServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        var taskId = req.getParameter("taskId");
        Objects.requireNonNull(taskId, "Parameter 'taskId' missing");

        new TaskDao().deleteById(taskId);
    }

}

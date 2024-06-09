package yandex.cloud.examples.serverless.todo;

import com.google.gson.Gson;
import yandex.cloud.examples.serverless.todo.db.TaskDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ListTasksServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var tasks = new TaskDao().findAll();
        var tasksJsonString = gson.toJson(tasks);

        var out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(tasksJsonString);
        out.flush();
    }

}

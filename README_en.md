# Tutorial for deploying a small to-do list on a serverless stack using the Java Servlet API

1. Create a `serverless` database. 
To do this, select `Yandex Database` in the list of services, click **Create database**, and select `serverless` as the database type.
Then, go to the **Navigation** tab and create the **Tasks** table. You can do this either from the UI or by running a simple SQL query:

    ```sql
    create table Tasks (
        TaskId Utf8,
        Name Utf8,
        Description Utf8,
        CreatedAt Datetime,
        primary key (TaskId)
    );
    ```

2. Create a service account in the current directory (to do this, go to the root directory and select **Service Accounts** on the left side of the menu), and then add the `viewer` and `editor` roles to that service account.

3. Create three functions (one for each servlet), upload this project into each of them, set `java11` as the runtime environment and specify the entry point depending on the current servlet. 
**Make sure to specify** the service account created at the previous step. For each function, add environment variables as follows:
    * `DATABASE`: Value in the `Database` field of your database (e.g., `/ru-centralx/yyyyyyyyyy/zzzzzzzzzz`).
    * `ENDPOINT`: Value in the `Endpoint` field of your database (e.g.,`ydb.serverless.yandexcloud.net:2135`).

    Your function entry points should be as follows:
    * yandex.cloud.examples.serverless.todo.AddTaskServlet
    * yandex.cloud.examples.serverless.todo.ListTasksServlet
    * yandex.cloud.examples.serverless.todo.DeleteTaskServlet

    To deploy a function:
    * Archive the project contents (e.g., `zip target.zip -r src pom.xml`).
    * Run a simple command (for this, you need to have `yc` installed and configured; you can learn more info [here](https://yandex.cloud/docs/cli/quickstart#install)):
    
   ```bash
    yc serverless function version create \
        --function-id=<current ID of the function> \
        --runtime=java11 \
        --entrypoint=<current entry point> \
        --memory=128mb \
        --execution-timeout=3s \
        --source-path=target.zip \
        --environment="DATABASE=<Database field value>;ENDPOINT=<Endpoint field value>"
    ```
   
    Run the command three times, each time specifying one of the entry points, the respective function ID, and values for the environment variables.
    
    * Alternatively, you can create a version through the UI. To do this, go to the function, open the **Editor** tab; in the **Method** tab, select `ZIP`, and feed the zipped project there.
    
    After that, set these parameters:
      * Entry point: the current entry point (e.g., `yandex.cloud.examples.serverless.todo.AddTaskServlet` for the function associated with this servlet).
      * Timeout, sec: 3.
      * RAM: 128 MB.
      * Service account: Select the service account created at step 2.
      * Environment variables:
        * DATABASE: `Database` field value.
        * ENDPOINT: `Endpoint` field value.

4. Create a bucket in `s3` and upload `index.html` (`src/main/resources/index.html`) to that bucket.

5. Create `API Gateway`. In the specification, delete any contents in the `paths` field and provide the following instead:

    ```openapi
      /:
        get:
          x-yc-apigateway-integration:
            type: object-storage
            bucket: <bucket>
            object: index.html
            presigned_redirect: false
            service_account: <service_account>
          operationId: static
      /add:
        post:
          x-yc-apigateway-integration:
            type: cloud-functions
            function_id: <add_servlet_function>
          operationId: addTask
      /list:
        get:
          x-yc-apigateway-integration:
            type: cloud-functions
            function_id: <list_servlet_function>
          operationId: listTasks
      /delete:
        delete:
          x-yc-apigateway-integration:
            type: cloud-functions
            function_id: <delete_servlet_function>
          operationId: deleteTask
    ```
    
    Replace `<bucket>` with the name of the bucket housing the `index.html` file, and `<service_account>`, with the the ID of the service account created at step 2. Replace all other parameter values with the IDs of the respective functions.

That’s it! Your to-do list should now be displayed when you click on the link specified in `API Gateway`.

Useful links:
* [Cloud Functions documentation](https://yandex.cloud/docs/functions/)
* [YDB documentation](https://yandex.cloud/docs/ydb/)
* [API Gateway documentation](https://yandex.cloud/docs/api-gateway/)

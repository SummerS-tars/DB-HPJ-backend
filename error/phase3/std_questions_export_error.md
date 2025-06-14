request:

```txt
GET http://localhost:5173/api/std-questions/export?type=SUBJECTIVE&version=v1.1-beta
```

result:

```json
{
    "success": false,
    "error": {
        "code": "INTERNAL_ERROR",
        "message": "内部服务器错误",
        "details": null
    }
}
```

error info:

```txt
2025-06-14 12:03:28 - Using 'application/json', given [application/json] and supported [application/json, application/*+json]
2025-06-14 12:03:28 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@50aaa275]
2025-06-14 12:03:28 - Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.]
2025-06-14 12:03:28 - Completed 500 INTERNAL_SERVER_ERROR
```

more info:

```txt
        sq1_0.id
Hibernate:
    select
        distinct sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sq1_0.status,
        t1_0.std_question_id,
        t1_1.tag,
        sq1_0.type,
        v1_0.std_question_id,
        v1_1.version,
        v1_1.created_at
    from
        std_questions sq1_0
    left join
        std_question_versions v1_0
            on sq1_0.id=v1_0.std_question_id
    left join
        version v1_1
            on v1_1.version=v1_0.version_id
    left join
        std_question_tags t1_0
            on sq1_0.id=t1_0.std_question_id
    left join
        tags t1_1
            on t1_1.tag=t1_0.tag_name
    where
        (
            ? is null
            or sq1_0.type=?
        )
        and (
            ? is null
            or sq1_0.status=?
        )
        and (
            ? is null
            or v1_0.version_id=?
        )
        and (
            ? is null
            or sq1_0.original_raw_question_id=?
        )
        and (
            ? is null
            or t1_0.tag_name=?
        )
    order by
        sq1_0.id
2025-06-14 11:58:15 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-14 11:58:15 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-14 11:58:15 - Writing [ApiResponse(success=true, data=[VersionResponse(version=1.0, createdAt=2025-06-05T12:54:23.538790, q (truncated)...]
2025-06-14 11:58:15 - Writing [ApiResponse(success=true, data=[TagResponse(tag=Authentication, questionCount=1), TagResponse(tag=De (truncated)...]
2025-06-14 11:58:15 - Completed 200 OK
2025-06-14 11:58:15 - Completed 200 OK
2025-06-14 11:58:15 -
    select
        sq1_0.version_id,
        sq1_1.id,
        sq1_1.content,
        sq1_1.created_at,
        sq1_1.original_raw_question_id,
        sq1_1.status,
        sq1_1.type
    from
        std_question_versions sq1_0
    join
        std_questions sq1_1
            on sq1_1.id=sq1_0.std_question_id
    where
        sq1_0.version_id=?
Hibernate:
    select
        sq1_0.version_id,
        sq1_1.id,
        sq1_1.content,
        sq1_1.created_at,
        sq1_1.original_raw_question_id,
        sq1_1.status,
        sq1_1.type
    from
        std_question_versions sq1_0
    join
        std_questions sq1_1
            on sq1_1.id=sq1_0.std_question_id
    where
        sq1_0.version_id=?
2025-06-14 11:58:15 - 
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
Hibernate:
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
2025-06-14 11:58:15 - 
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
Hibernate:
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
2025-06-14 11:58:15 - 
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
Hibernate:
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
2025-06-14 11:58:15 - 
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
Hibernate:
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
2025-06-14 11:58:15 - 
    select
        sq1_0.version_id,
        sq1_1.id,
        sq1_1.content,
        sq1_1.created_at,
        sq1_1.original_raw_question_id,
        sq1_1.status,
        sq1_1.type
    from
        std_question_versions sq1_0
    join
        std_questions sq1_1
            on sq1_1.id=sq1_0.std_question_id
    where
        sq1_0.version_id=?
Hibernate:
    select
        sq1_0.version_id,
        sq1_1.id,
        sq1_1.content,
        sq1_1.created_at,
        sq1_1.original_raw_question_id,
        sq1_1.status,
        sq1_1.type
    from
        std_question_versions sq1_0
    join
        std_questions sq1_1
            on sq1_1.id=sq1_0.std_question_id
    where
        sq1_0.version_id=?
2025-06-14 11:58:15 - 
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
Hibernate:
    select
        rq1_0.id,
        rq1_0.content,
        rq1_0.post_id,
        rq1_0.score,
        rq1_0.source_platform,
        rq1_0.status,
        rq1_0.tags,
        rq1_0.title
    from
        raw_questions rq1_0
    where
        rq1_0.id=?
2025-06-14 11:58:15 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-14 11:58:15 - Writing [ApiResponse(success=true, data=Page 1 of 1 containing top.thesumst.llm_eval_backend.dto.response.Sta (truncated)...]
2025-06-14 11:58:15 - Completed 200 OK
2025-06-14 12:01:12 - GET "/api/std-questions/export?type=OBJECTIVE&version=v1.0", parameters={masked}
2025-06-14 12:01:12 - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
2025-06-14 12:01:12 - Resource not found
2025-06-14 12:01:12 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-14 12:01:12 - Unexpected exception: No static resource api/std-questions/export.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.
        at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:586) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:113) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at java.base/java.lang.Thread.run(Thread.java:842) ~[na:na]
2025-06-14 12:01:12 - Using 'application/json', given [application/json] and supported [application/json, application/*+json]
2025-06-14 12:01:12 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@58e15900]
2025-06-14 12:01:12 - Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.]
2025-06-14 12:01:12 - Completed 500 INTERNAL_SERVER_ERROR
2025-06-14 12:01:20 - GET "/api/std-questions/export?type=SUBJECTIVE&version=v1.0", parameters={masked}
2025-06-14 12:01:20 - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
2025-06-14 12:01:20 - Resource not found
2025-06-14 12:01:20 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-14 12:01:20 - Unexpected exception: No static resource api/std-questions/export.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.
        at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:586) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:113) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at java.base/java.lang.Thread.run(Thread.java:842) ~[na:na]
2025-06-14 12:01:20 - Using 'application/json', given [application/json] and supported [application/json, application/*+json]
2025-06-14 12:01:20 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@9c3b532]
2025-06-14 12:01:20 - Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.]
2025-06-14 12:01:20 - Completed 500 INTERNAL_SERVER_ERROR
2025-06-14 12:01:25 - GET "/api/std-questions/export?type=OBJECTIVE&version=1.0", parameters={masked}
2025-06-14 12:01:25 - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
2025-06-14 12:01:25 - Resource not found
2025-06-14 12:01:25 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-14 12:01:25 - Unexpected exception: No static resource api/std-questions/export.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.
        at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:586) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:113) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at java.base/java.lang.Thread.run(Thread.java:842) ~[na:na]
2025-06-14 12:01:25 - Using 'application/json', given [application/json] and supported [application/json, application/*+json]
2025-06-14 12:01:25 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@6c4492c]
2025-06-14 12:01:25 - Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.]
2025-06-14 12:01:25 - Completed 500 INTERNAL_SERVER_ERROR
2025-06-14 12:01:27 - GET "/api/std-questions/export?type=SUBJECTIVE&version=v1.0", parameters={masked}
2025-06-14 12:01:27 - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
2025-06-14 12:01:27 - Resource not found
2025-06-14 12:01:27 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-14 12:01:27 - Unexpected exception: No static resource api/std-questions/export.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.
        at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:586) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:113) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at java.base/java.lang.Thread.run(Thread.java:842) ~[na:na]
2025-06-14 12:01:27 - Using 'application/json', given [application/json] and supported [application/json, application/*+json]
2025-06-14 12:01:27 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@3058c876]
2025-06-14 12:01:27 - Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.]
2025-06-14 12:01:27 - Completed 500 INTERNAL_SERVER_ERROR
2025-06-14 12:01:39 - GET "/api/std-questions/export?type=SUBJECTIVE&version=v1.1-beta", parameters={masked}
2025-06-14 12:01:39 - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
2025-06-14 12:01:39 - Resource not found
2025-06-14 12:01:39 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-14 12:01:39 - Unexpected exception: No static resource api/std-questions/export.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.
        at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:586) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:113) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at java.base/java.lang.Thread.run(Thread.java:842) ~[na:na]
2025-06-14 12:01:39 - Using 'application/json', given [application/json] and supported [application/json, application/*+json]
2025-06-14 12:01:39 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@7636c762]
2025-06-14 12:01:39 - Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.]
2025-06-14 12:01:39 - Completed 500 INTERNAL_SERVER_ERROR
2025-06-14 12:01:45 - GET "/api/std-questions/export?type=SUBJECTIVE&version=v1.1-beta", parameters={masked}
2025-06-14 12:01:45 - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
2025-06-14 12:01:45 - Resource not found
2025-06-14 12:01:45 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-14 12:01:45 - Unexpected exception: No static resource api/std-questions/export.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.
        at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:586) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:113) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at java.base/java.lang.Thread.run(Thread.java:842) ~[na:na]
2025-06-14 12:01:45 - Using 'application/json', given [application/json] and supported [application/json, application/*+json]
2025-06-14 12:01:45 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@a532156]
2025-06-14 12:01:45 - Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.]
2025-06-14 12:01:45 - Completed 500 INTERNAL_SERVER_ERROR
2025-06-14 12:03:27 - GET "/api/std-questions/export?type=SUBJECTIVE&version=v1.1-beta", parameters={masked}
2025-06-14 12:03:27 - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
2025-06-14 12:03:28 - Resource not found
2025-06-14 12:03:28 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-14 12:03:28 - Unexpected exception: No static resource api/std-questions/export.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/std-questions/export.
        at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:586) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.33.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:113) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.15.jar:6.1.15]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.15.jar:6.1.15]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.33.jar:10.1.33]
        at java.base/java.lang.Thread.run(Thread.java:842) ~[na:na]
```
problem:

when the frontend try to get the analysis tags, it get a server internal error

request:

```txt
GET /api/v1/analysis-tags?page=0&size=20&sortBy=analysisTagId&sortDir=desc 
```

response: 500 Internal Server Error

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

backend error log:

```txt
2025-06-15 03:58:49 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-15 03:58:49 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@153ac97c]
2025-06-15 03:58:49 - Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/v1/analysis-tags.]
2025-06-15 03:58:49 - Completed 500 INTERNAL_SERVER_ERROR
```

more info:

```txt
2025-06-15 03:58:49 - GET "/api/v1/analysis-tags?page=0&size=20&sortBy=analysisTagId&sortDir=desc", parameters={masked}
2025-06-15 03:58:49 - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/], classpath [resources/], classpath [static/], classpath [public/], ServletContext [/]]
2025-06-15 03:58:49 - Resource not found
2025-06-15 03:58:49 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-15 03:58:49 - Unexpected exception: No static resource api/v1/analysis-tags.
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/v1/analysis-tags.
        at org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:586)
        at org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52)
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089)
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979)
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014)
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903)
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564)
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)   
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:113)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140)
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167)
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:483)
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115)
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344)
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:397)
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:905)
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741)
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190)
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63)
        at java.base/java.lang.Thread.run(Thread.java:842)
```
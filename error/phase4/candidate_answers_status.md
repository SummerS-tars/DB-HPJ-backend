problem:  

the request to update the candidate answer status get error

request:

```txt
PATCH 
http://localhost:8080/api/v1/candidate-answers/33

OPTIONS 
http://localhost:8080/api/v1/candidate-answers/33
```

response:

```txt
500 Internal Server Error

and

200 OK
```

error info:

```txt
2025-06-14 15:23:20 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-14 15:23:20 - Writing [top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler$ErrorResponse@6fd6aa6c]
2025-06-14 15:23:20 - Resolved [org.springframework.web.HttpRequestMethodNotSupportedException: Request method 'PATCH' is not supported]
2025-06-14 15:23:20 - Completed 500 INTERNAL_SERVER_ERROR
```

more info:

```txt
... more
Hibernate: 
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
Hibernate:
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
2025-06-14 15:22:45 -
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
Hibernate:
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
Hibernate:
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
Hibernate: 
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
Hibernate:
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
Hibernate:
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
Hibernate:
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
Hibernate:
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
Hibernate:
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
Hibernate:
    select
        cao1_0.candidate_answer_id,
        cao1_0.obj_answer
    from
        candidate_answers_obj cao1_0
    where
        cao1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
Hibernate:
    select
        cas1_0.candidate_answer_id,
        cas1_0.sub_answer
    from
        candidate_answers_sub cas1_0
    where
        cas1_0.candidate_answer_id=?
2025-06-14 15:22:45 - 
    select
        count(ca1_0.id)
    from
        candidate_answers ca1_0
    where
        ca1_0.type=?
        and ca1_0.status=?
Hibernate:
    select
        count(ca1_0.id)
    from
        candidate_answers ca1_0
    where
        ca1_0.type=?
        and ca1_0.status=?
2025-06-14 15:22:45 - 
    select
        sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sq1_0.status,
        sq1_0.type
    from
        std_questions sq1_0
    where
        sq1_0.id=?
Hibernate:
    select
        sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sq1_0.status,
        sq1_0.type
    from
        std_questions sq1_0
    where
        sq1_0.id=?
2025-06-14 15:22:45 - 
    select
        sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sq1_0.status,
        sq1_0.type
    from
        std_questions sq1_0
    where
        sq1_0.id=?
Hibernate:
    select
        sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sq1_0.status,
        sq1_0.type
    from
        std_questions sq1_0
    where
        sq1_0.id=?
2025-06-14 15:22:45 - 
    select
        sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sq1_0.status,
        sq1_0.type
    from
        std_questions sq1_0
    where
        sq1_0.id=?
Hibernate:
    select
        sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sq1_0.status,
        sq1_0.type
    from
        std_questions sq1_0
    where
        sq1_0.id=?
2025-06-14 15:22:45 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-14 15:22:45 - Writing [ApiResponse(success=true, data=Page 1 of 2 containing top.thesumst.llm_eval_backend.dto.response.Can (truncated)...]
2025-06-14 15:22:45 - Completed 200 OK
2025-06-14 15:23:20 - OPTIONS "/api/v1/candidate-answers/31", parameters={}
2025-06-14 15:23:20 - Mapped to org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping$HttpOptionsHandler#handle()
2025-06-14 15:23:20 - Completed 200 OK
2025-06-14 15:23:20 - PATCH "/api/v1/candidate-answers/31", parameters={}
2025-06-14 15:23:20 - Using @ExceptionHandler top.thesumst.llm_eval_backend.exception.GlobalExceptionHandler#handleGenericException(Exception)
2025-06-14 15:23:20 - Unexpected exception: Request method 'PATCH' is not supported
org.springframework.web.HttpRequestMethodNotSupportedException: Request method 'PATCH' is not supported
        at org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping.handleNoMatch(RequestMappingInfoHandlerMapping.java:267) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.handler.AbstractHandlerMethodMapping.lookupHandlerMethod(AbstractHandlerMethodMapping.java:441) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.handler.AbstractHandlerMethodMapping.getHandlerInternal(AbstractHandlerMethodMapping.java:382) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping.getHandlerInternal(RequestMappingInfoHandlerMapping.java:127) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping.getHandlerInternal(RequestMappingInfoHandlerMapping.java:68) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.handler.AbstractHandlerMapping.getHandler(AbstractHandlerMapping.java:507) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.getHandler(DispatcherServlet.java:1284) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1065) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.15.jar:6.1.15]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:888) ~[spring-webmvc-6.1.15.jar:6.1.15]
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
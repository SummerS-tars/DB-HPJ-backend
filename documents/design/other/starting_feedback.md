# Staring Feedback Confusion

Why there are some SQL statements here?

```txt
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.6)
2025-06-14 22:18:38 - Starting LlmEvalBackendApplication using Java 17.0.11 with PID 27724 (E:\_ComputerLearning\6_Practice_Database\5_HPJ\backend\llm-eval-backend\target\classes started by Sum in E:\_ComputerLearning\6_Practice_Database\5_HPJ\backend\llm-eval-backend)    
2025-06-14 22:18:38 - Running with Spring Boot v3.3.6, Spring v6.1.15
2025-06-14 22:18:38 - The following 1 profile is active: "dev"
2025-06-14 22:18:38 - Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2025-06-14 22:18:38 - For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2025-06-14 22:18:38 - Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-06-14 22:18:39 - Finished Spring Data repository scanning in 47 ms. Found 7 JPA repository interfaces.
2025-06-14 22:18:41 - Tomcat initialized with port 8080 (http)
2025-06-14 22:18:41 - Starting service [Tomcat]
2025-06-14 22:18:41 - Starting Servlet engine: [Apache Tomcat/10.1.33]
2025-06-14 22:18:41 - Initializing Spring embedded WebApplicationContext
2025-06-14 22:18:41 - Root WebApplicationContext: initialization completed in 3072 ms      
2025-06-14 22:18:41 - HikariPool-1 - Starting...
2025-06-14 22:18:42 - HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@2f7fbb5f
2025-06-14 22:18:42 - HikariPool-1 - Start completed.
2025-06-14 22:18:42 - H2 console available at '/h2-console'. Database available at 'jdbc:mysql://thesumst.top:7012/llm_evaluate?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=60000&socketTimeout=60000&autoReconnect=true&failOverReadOnly=false&maxReconnects=3'
2025-06-14 22:18:42 - Filter 'webMvcObservationFilter' configured for use
2025-06-14 22:18:42 - HHH000204: Processing PersistenceUnitInfo [name: default]
2025-06-14 22:18:42 - HHH000412: Hibernate ORM core version 6.5.3.Final
2025-06-14 22:18:42 - HHH000026: Second-level cache disabled
2025-06-14 22:18:43 - No LoadTimeWeaver setup: ignoring JPA class transformer
2025-06-14 22:18:43 - HHH90000025: MySQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)   
2025-06-14 22:18:44 - HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-06-14 22:18:44 - 
    alter table candidate_answers
       modify column notes TEXT
Hibernate: 
    alter table candidate_answers
       modify column notes TEXT
2025-06-14 22:18:44 - 
    alter table candidate_answers_sub
       modify column sub_answer TEXT not null
Hibernate: 
    alter table candidate_answers_sub
       modify column sub_answer TEXT not null
2025-06-14 22:18:44 - 
    alter table evaluation_results
       modify column content TEXT
Hibernate: 
    alter table evaluation_results
       modify column content TEXT
2025-06-14 22:18:44 - 
    alter table raw_answers
       modify column content TEXT
Hibernate: 
    alter table raw_answers
       modify column content TEXT
2025-06-14 22:18:45 - 
    alter table raw_questions
       modify column content TEXT
Hibernate: 
    alter table raw_questions
       modify column content TEXT
2025-06-14 22:18:45 - 
    alter table std_answers
       modify column notes TEXT
Hibernate: 
    alter table std_answers
       modify column notes TEXT
2025-06-14 22:18:45 - 
    alter table std_answers_sub
       modify column sub_answer TEXT not null
Hibernate: 
    alter table std_answers_sub
       modify column sub_answer TEXT not null
2025-06-14 22:18:45 - 
    alter table std_questions
       modify column content TEXT not null
Hibernate: 
    alter table std_questions
       modify column content TEXT not null
2025-06-14 22:18:46 - Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-06-14 22:18:46 - Hibernate is in classpath; If applicable, HQL parser will be used.
2025-06-14 22:18:48 - spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-06-14 22:18:49 - 53 mappings in 'requestMappingHandlerMapping'
```

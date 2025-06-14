question:

Standard Questions and Answers Exportation Error  
the output file have duplicate answers for the same question  

request:  

```txt
GET 
http://localhost:5173/api/v1/std-questions/export-with-answers?type=SUBJECTIVE&version=v1.0
```

response: 200 OK

and with a json file with the content:

```json
{
  "version": "v1.0",
  "type": "subjective",
  "number": 1,
  "q_a": [
    {
      "question": {
        "id": 5,
        "content": "Design and explain the architecture for setting up a secure OpenID provider on Ubuntu. Include security considerations and implementation steps."
      },
      "answer": [
        {
          "id": 3,
          "content": "Another answer with commas, multiple clauses, and detailed explanations about the topic."
        },
        {
          "id": 3,
          "content": "Another answer with commas, multiple clauses, and detailed explanations about the topic."
        },
        {
          "id": 3,
          "content": "Another answer with commas, multiple clauses, and detailed explanations about the topic."
        },
        {
          "id": 3,
          "content": "Another answer with commas, multiple clauses, and detailed explanations about the topic."
        }
      ]
    }
  ]
}
```

backend log info:

```txt
2025-06-15 01:24:37 - GET "/api/v1/std-questions/export-with-answers?type=SUBJECTIVE&version=v1.0", parameters={masked}
2025-06-15 01:24:37 - Mapped to top.thesumst.llm_eval_backend.controller.StandardQuestionController#exportStandardQuestionsWithAnswers(QuestionType, String, String)
2025-06-15 01:24:37 - Exporting standard questions with answers - type: SUBJECTIVE, version: v1.0, tag: null
2025-06-15 01:24:37 - Exporting standard questions with answers - version: v1.0, type: SUBJECTIVE, tag: null
2025-06-15 01:24:37 -
    select
        distinct sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sa1_0.std_question_id,
        sa1_0.id,
        sa1_0.created_at,
        sa1_0.notes,
        sa1_0.score,
        sa1_0.selected_from_candidate_id,
        sao1_0.std_answer_id,
        sao1_0.obj_answer,
        sas1_0.std_answer_id,
        sas1_0.sub_answer,
        sa1_0.status,
        sa1_0.type,
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
    left join
        std_answers sa1_0
            on sq1_0.id=sa1_0.std_question_id
    left join
        std_answers_obj sao1_0
            on sa1_0.id=sao1_0.std_answer_id
    left join
        std_answers_sub sas1_0
            on sa1_0.id=sas1_0.std_answer_id
    where
        (
            ? is null
            or sq1_0.type=?
        )
        and (
            ? is null
            or v1_0.version_id=?
        )
        and (
            ? is null
            or t1_0.tag_name=?
        )
        and sa1_0.status='ACCEPTED'
    order by
        sq1_0.id
Hibernate:
    select
        distinct sq1_0.id,
        sq1_0.content,
        sq1_0.created_at,
        sq1_0.original_raw_question_id,
        sa1_0.std_question_id,
        sa1_0.id,
        sa1_0.created_at,
        sa1_0.notes,
        sa1_0.score,
        sa1_0.selected_from_candidate_id,
        sao1_0.std_answer_id,
        sao1_0.obj_answer,
        sas1_0.std_answer_id,
        sas1_0.sub_answer,
        sa1_0.status,
        sa1_0.type,
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
    left join
        std_answers sa1_0
            on sq1_0.id=sa1_0.std_question_id
    left join
        std_answers_obj sao1_0
            on sa1_0.id=sao1_0.std_answer_id
    left join
        std_answers_sub sas1_0
            on sa1_0.id=sas1_0.std_answer_id
    where
        (
            ? is null
            or sq1_0.type=?
        )
        and (
            ? is null
            or v1_0.version_id=?
        )
        and (
            ? is null
            or t1_0.tag_name=?
        )
        and sa1_0.status='ACCEPTED'
    order by
        sq1_0.id
2025-06-15 01:24:37 - 
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
2025-06-15 01:24:37 - 
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
2025-06-15 01:24:37 - 
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
2025-06-15 01:24:37 - 
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
2025-06-15 01:24:37 - 
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
2025-06-15 01:24:37 - Found 'Content-Type:application/json' in response
2025-06-15 01:24:37 - Writing ["{<EOL><EOL>  "version" : "v1.0",<EOL><EOL>  "type" : "subjective",<EOL><EOL>  "number" : 1,<EOL><EOL>  "q_a" : [ {<EOL><EOL>    "question" : (truncated)..."]
2025-06-15 01:24:37 - Completed 200 OK
```
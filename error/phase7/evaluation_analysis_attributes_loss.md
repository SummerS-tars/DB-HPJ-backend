probem:

the frontend evaluation analysis page need to present `analysisModel`,`evaluationModel`,`standardQuestionId`,`standardQuestionContent` attributes  

but in the response of the api, these attributes are null  

request:

```txt
GET /api/v1/evaluation-analysis?page=0&size=20&sortBy=id&sortDir=desc
```

response:

```json
{
    "success": true,
    "data": {
        "content": [
            {
                "id": 1,
                "evaluationResultId": 7,
                "analysisTagId": 1,
                "score": 10,
                "createdAt": "2025-06-15T04:15:28",
                "analysisModel": null,
                "evaluationModel": null,
                "standardQuestionId": null,
                "standardQuestionContent": null
            }
        ],
        "pageable": {
            "pageNumber": 0,
            "pageSize": 20,
            "sort": {
                "empty": false,
                "sorted": true,
                "unsorted": false
            },
            "offset": 0,
            "paged": true,
            "unpaged": false
        },
        "last": true,
        "totalPages": 1,
        "totalElements": 1,
        "size": 20,
        "number": 0,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "first": true,
        "numberOfElements": 1,
        "empty": false
    },
    "message": null
}
```

backend info:

```txt
2025-06-15 04:15:43 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-15 04:15:43 - Writing [ApiResponse(success=true, data=Page 1 of 1 containing top.thesumst.llm_eval_backend.dto.response.Eva (truncated)...]
2025-06-15 04:15:43 - Completed 200 OK
```

more info:

```txt
2025-06-15 04:15:43 - GET "/api/v1/evaluation-analysis?page=0&size=20&sortBy=id&sortDir=desc", parameters={masked}
2025-06-15 04:15:43 - Mapped to top.thesumst.llm_eval_backend.controller.EvaluationAnalysisController#getAllAnalysisResults(int, int, String, String)
2025-06-15 04:15:43 - Getting all analysis results - page: 0, size: 20
2025-06-15 04:15:43 - Getting all analysis results with pagination
2025-06-15 04:15:43 - 
    select
        ea1_0.id,
        ea1_0.analysis_tag_id,
        ea1_0.created_at,
        ea1_0.evaluation_result_id,
        ea1_0.score
    from
        evaluation_analysis ea1_0
    order by
        ea1_0.id desc
    limit
        ?, ?
Hibernate:
    select
        ea1_0.id,
        ea1_0.analysis_tag_id,
        ea1_0.created_at,
        ea1_0.evaluation_result_id,
        ea1_0.score
    from
        evaluation_analysis ea1_0
    order by
        ea1_0.id desc
    limit
        ?, ?
```

problem:

the importation of the evaluation results doesn't work again after the fix

guess:

maybe the backend didn't support duplicate evaluation results
but this is not right

What I need is that, in a specific evaluation tag, the evaluation for the same subjective question can be multiple, and for objective it is only one.

request:

```txt
POST /api/v1/evaluation-results/import?evaluationTagId=1&type=SUBJECTIVE
```

response: 201 Created

```json
{
    "success": true,
    "data": {
        "message": "评估结果导入成功",
        "importedCount": 0,
        "failedCount": 0,
        "errors": null
    },
    "message": "评估结果导入完成"
}
```

backend info:

```txt
2025-06-15 02:39:52 - Duplicate evaluation result found: tagId=1, questionId=5
2025-06-15 02:39:52 - JSON import completed. Imported: 0, Failed: 0
2025-06-15 02:39:52 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-15 02:39:52 - Writing [ApiResponse(success=true, data=ImportResponse(message=评估结果导入成功, importedCount=0, failedCount=0, erro (truncated)...]
2025-06-15 02:39:52 - Completed 201 CREATED
```
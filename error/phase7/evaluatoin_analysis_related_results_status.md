problem:

when I add an evaluation analysis for some evaluation result  
the status of the evaluation was not updated  

In design, it should be updated to `ANALYZED`  
if it's all related analysis are deleted, then it should be updated to `PENDING`  

tips: I want this to be implemented by trigger just like the standard question and answer

request:

```txt
POST /api/v1/evaluation-analysis/import
```

with request load:

results: [{evaluationResultId: 7, score: 10}]

response: 200 OK

```json
{
    "success": true,
    "data": {
        "message": "Import completed - Success: 1, Skip: 0, Error: 0. ",
        "importedCount": 1,
        "failedCount": 0,
        "errors": null
    },
    "message": null
}
```

backend info:

```txt
2025-06-15 05:30:26 - Import completed - Success: 1, Skip: 0, Error: 0
2025-06-15 05:30:26 - Using 'application/json', given [application/json, text/plain, */*] and supported [application/json, application/*+json]
2025-06-15 05:30:26 - Writing [ApiResponse(success=true, data=ImportResponse(message=Import completed - Success: 1, Skip: 0, Error: (truncated)...]
2025-06-15 05:30:26 - Completed 200 OK
```

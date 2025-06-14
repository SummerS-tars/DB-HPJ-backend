api:

```txt
GET http://localhost:8080/api/v1/std-questions?page=0&size=20&sortBy=id&sortDirection=asc&type=OBJECTIVE&status=WAITING_ANSWERS&version=v1.0
```

problem: Can get right content but with no versions and tags return.

```json
{
    "success": true,
    "data": {
        "content": [
            {
                "id": 2,
                "originalRawQuestionId": 22,
                "type": "OBJECTIVE",
                "content": "Which of the following is the most effective method for automatically generating stack traces on Unix systems when a SIGSEGV occurs?\nA) Using gdb with core dumps\nB) Implementing a custom SIGSEGV handler\nC) Using valgrind for memory debugging\nD) Setting up system-wide crash reporters",
                "status": "WAITING_ANSWERS",
                "createdAt": "2025-06-05T13:22:13",
                "versions": [],
                "tags": [],
                "originalRawQuestion": {
                    "id": 22,
                    "title": "Getting stack traces on Unix systems, automatically",
                    "content": "What methods are there for automatically getting a stack trace on Unix systems? I don't mean just getting a core file or attaching interactively with GDB, but having a SIGSEGV handler that dumps a backtrace to a text file. Bonus points for the following optional features: Extra information gathering at crash time (eg. config files). Email a crash info bundle to the developers. Ability to add this in a dlopened shared library Not requiring a GUI",
                    "sourcePlatform": "stackoverflow",
                    "tags": "linux,unix,stack-trace,segmentation-fault",
                    "postId": 18265,
                    "score": 7,
                    "status": "CONVERTED"
                }
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
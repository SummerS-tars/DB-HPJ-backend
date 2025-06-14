# Raw Questions Export

I need a new function for exporting the raw questions to a json file  

the function is just similar to the standard questions export functions

## detail requirements

I need to choose:  

1. whether to include the raw questions with status 'CONVERTED'  
    not by default

2. numbers of questions to export  
    by default, export all questions  

3. need to return a file in JSON, and in the format:  

    ```json
    [
        {
        "id": 2,
        "content": "Which of the following is the most effective method for automatically generating stack traces on Unix systems when a SIGSEGV occurs?\nA) Using gdb with core dumps\nB) Implementing a custom SIGSEGV handler\nC) Using valgrind for memory debugging\nD) Setting up system-wide crash reporters"
        },
        // ... other raw questions
    ]
    ```

    with name: `raw_questions_for_standardize.json`  

## document

need a document for review and for the frontend to refer to develop the function module

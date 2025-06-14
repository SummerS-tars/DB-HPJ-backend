# Standard Question Output

I need to output the some specified std_questions in a specified format file  

like:  

`{version_id}_{question_type}_{tags}.json`

in the format:  

```json
{
    "id": 2,
    "content": "Which of the following is the most effective method for automatically generating stack traces on Unix systems when a SIGSEGV occurs?\nA) Using gdb with core dumps\nB) Implementing a custom SIGSEGV handler\nC) Using valgrind for memory debugging\nD) Setting up system-wide crash reporters"
},
// the same for other std_questions
```

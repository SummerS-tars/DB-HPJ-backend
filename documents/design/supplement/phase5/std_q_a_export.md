# Standard Questions and Answers Export

I need another exportation module for the standard questions and answers

this exportation module is used to combined the standard questions and corresponding answers into a single file  
to make it easier to be used to evaluate the evaluation results  

## Detailed Requirements

1. just like the exportation module for the standard questions  
    but here it should combine the standard questions and corresponding answers into a single file  

2. once only one type question can be selected  
    either objective or subjective  

3. this exportation should based on the version of the std question set  

4. tags should be optional filter arguments

5. the response should be a single file  
    with the name:  

    `{version}_{type}_std_q_a.json`  

    and with the attributes format:  

    ```json
    {
        "version": "v1.0",
        "type": "objective",
        "number": 1,
        "q_a": [
            {
                "question": [
                    "id": 1,
                    "content": "question content"
                ],
                "answer": [
                    {
                        "id": 1,
                        "content": "A"
                    },
                    // if it is a subjective question, the answer should be a text and there can be multiple answers
                ]
            }
        ]
    }
    ```

    attention for subjective questions, there may be multiple answers for one questions  

## document

generate a document to record the development process of this part for reference and review  
and a document to introduce the api and usage for the frontend to refer to develop the function module

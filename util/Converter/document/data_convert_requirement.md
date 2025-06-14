# data convert requirement

I need to convert the data about raw questions to the standard format

## detail requirements

1. the original data is in XML format, it should be converted to CSV format

2. the original data has the following attributes:  
    [reference](../../converter-data-set/qustion/xml/problem.xml)
    1. Id
    2. PostTypeId
    3. AcceptedAnswerId
    4. CreationDate
    5. Score
    6. ViewCount
    7. Body
    8. OwnerUserId
    9. OwnerDisplayName
    10. LastEditorUserId
    11. LastEditDate
    12. LastActivityDate
    13. Title
    14. Tags
    15. AnswerCount
    16. CommentCount
    17. ContentLicense

3. the standard format should be:
    *you should strictly follow the format below(including lowercase and sequence)*  
    [reference](../../converter-data-set/qustion/csv/problem.csv)
    1. title(from Title)
    2. content(from Body)
    3. tags(from Tags)
    4. postID(from Id)
    5. score(from Score)

## tips

I think you can break this task into several steps:

1. just select the data need in the original data(attention the content and title may include some special characters, you should handle them)

2. convert to CSV format  

3. besides, the content in the original data contains html tags, you can use a step remove them  

4. the Tags attribute should be converted from "|tag1|tag2|tag3|" to "tag1,tag2,tag3"  

realize one step at a time to make sure every step is correct and easy to debug please.

# Raw Answers Converter

Please refer to logic of the Converter for the raw questions, create converter tools for the raw answers  

## detail requirements

1. the original data is in XML format, it should be converted to CSV format  

2. the original data has the following attributes:  
    [reference](../../converter-data-set/answer/xml/answer.xml)
    1. Id
    2. PostTypeId
    3. ParentId
    4. CreationDate
    5. Score
    6. Body
    7. OwnerUserId
    8. OwnerDisplayName
    9. LastActivityDate
    10. CommentCount
    11. ContentLicense  

    needed attributes in the CSV file:

    1. rawQuestionId(from ParentId)
    2. content(from Body)
    3. postId(from Id)
    4. score(from Score)

3. the resources of answers xml will be provided in the source directory  
    and you should generate the csv file in the output directory  

## tips

the content in the original raw answer data contains html tags, too, just like the question data  

## documents

a document to record your development process for reference and debug  

and a simple introduction document to inform the user to use the tools  

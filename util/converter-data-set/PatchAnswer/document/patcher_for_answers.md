# Patcher for Answers

I need a tool to patch the raw answers which are related to the raw questions that have been exported into the database  

## detail requirements

1. your job is to find the answers related to the questions, and then extract them from the [Post-answers.xml](../../source/Posts-answers.xml) file, and just copy the related answer raws to the new file in the output directory(we can choose whether to delete it from the original file)

2. I'll provide the some json files(in the questions directory) which contains the exported raw questions postId  

    in the format:  

    ```json
    {
        "postId": [
            1,
            // ... other postIds
        ]
    }
    ```

3. the raw answers are in XML format, and have the following attributes:  

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

    and every raw in the format:  

    ```xml
      <row Id="46075" PostTypeId="2" ParentId="32027" CreationDate="2008-09-05T15:43:46.117" Score="1" Body="&lt;p&gt;It is worth noting that a lot of tools like Nant run on mono 'out of the box', i.e.&lt;/p&gt;&#xA;&#xA;&lt;pre&gt;&lt;code&gt;mono nant.exe&#xA;&lt;/code&gt;&lt;/pre&gt;&#xA;&#xA;&lt;p&gt;works&lt;/p&gt;&#xA;" OwnerUserId="3024" OwnerDisplayName="Frep D-Oronge" LastActivityDate="2008-09-05T15:43:46.117" CommentCount="0" ContentLicense="CC BY-SA 2.5" />
    ```

4. the `postId` in the questions' json file is related to the `ParentId` in the answers xml file  

### tips

the answers xml file is very large  

## document

generate a document to introduce the Patcher tools

and a simple way to use it

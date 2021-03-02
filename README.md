There is two tester that we can run.

1) /spring-boot/src/test/java/com/example/springboot/PostTester: Test the program against the real Rest Api. TO run successfully whe need to set the example.jsonPath and example.xmlPath properties in the /spring-boot/src/main/resources/application.properties file. In this path, the xml,json files is going to be generated.
2) /spring-boot/src/test/java/com/example/springboot/PostMockTester: Test the program mocking the Rest Api. By this way we can test other posiblities and test exception control cases by mocking these anormal situations. To run succesfully; besides the above mentioned properties (example.jsonPath and example.xmlPath), we need to set the example.inputJsonPath and move the json files placed in /spring-boot/src/main/resources/ to this path (They are our rest api mocked data) the files are: comment.json, post.json,postCommentConnectionException.json and postNotExistentComment.json


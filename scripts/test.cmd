call cd ./schedule-api-java
call ./mvnw clean test

call cd ../ui-react
call npm test

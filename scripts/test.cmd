call cd ./api-java
call ./mvnw clean test

call cd ../ui-react
call npm test

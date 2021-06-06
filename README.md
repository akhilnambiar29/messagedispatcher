### **MessageDispatcher Service**

Please find a simple message dispatcher service which can be used to publish sms and emails and can also be used to retrieve details of the published message.

Dynamodb has been chosen to save details about the message.

You can find more details about the api in the [swagger.yaml](swagger.yaml) file

All the above endpoints need a valid cognito session to retrieve details thus making it secure

To run the application and tests:

```shell
mvn clean install
```

[Localstack](https://github.com/localstack/localstack) has been used to spin up an instance of the
dynamodb locally and run the tests.

Tests have been written only for repository classes as of now.

Todo list:

- [ ] Add full swagger file
- [ ] Save timestamps for messages in the dynamodb
- [ ] Improve validations for input params
- [ ] Write some more tests
- [ ] AWS Cloudformation for deploying the stack on AWS APIGateway
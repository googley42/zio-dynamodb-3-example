# zio-dynamodb-3-example

A simple hello world example for using zio-dynamodb in Scala 3

Note before you run these you must first run the DynamoDBLocal docker container using the provided docker-compose file:

```
docker-compose -f docker/docker-compose.yml up -d
```

Dont forget to shut down the container after you have finished

```
docker-compose -f docker/docker-compose.yml down
```

## Usage

```bash
sbt "runMain foo.StudentZioDynamoDbExampleWithOptics"
```

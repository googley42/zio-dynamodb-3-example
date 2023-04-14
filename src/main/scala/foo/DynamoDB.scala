package foo

import software.amazon.awssdk.auth.credentials.{ AwsBasicCredentials, StaticCredentialsProvider }
import software.amazon.awssdk.regions.Region
import zio.ZLayer
import zio.aws.core.config
import zio.aws.dynamodb.DynamoDb
import zio.aws.{ dynamodb, netty }
import zio.dynamodb.DynamoDBExecutor

import java.net.URI
import zio.ULayer
import zio.aws.core.config.CommonAwsConfig
import zio.aws.core.config.AwsConfig
import zio.aws.core.httpclient.HttpClient

object DynamoDB {

  val dynamoDbLayer: ZLayer[Any, Throwable, DynamoDb] =
    ZLayer.make[DynamoDb](
      netty.NettyHttpClient.default,
      config.AwsConfig.default,
      dynamodb.DynamoDb.customized { builder =>
        builder.endpointOverride(URI.create("http://localhost:8000")).region(Region.US_EAST_1)
      }
    )

}

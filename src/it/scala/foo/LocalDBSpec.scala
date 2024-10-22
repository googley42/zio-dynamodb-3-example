package foo

import zio.test.ZIOSpecDefault
import zio.test.assertTrue
import zio.dynamodb.DynamoDBQuery
import zio.schema.DeriveSchema
import zio.schema.Schema
import zio.dynamodb.ProjectionExpression

object LocalDBSpec extends DynamoDBLocalSpec {
  def spec = suite("DynamoDB local suite")(
    test("simple put and get") {
      final case class User(id: String, name: String)
      object User {
        implicit val schema: Schema.CaseClass2[String, String, User] =
          DeriveSchema.gen[User]
        val (id, name) = ProjectionExpression.accessors[User]
      }

      withSingleIdKeyTable(tableName =>
        for {
          _ <- DynamoDBQuery.put(tableName, User("1", "John")).execute
          x <- DynamoDBQuery
            .get(tableName)(User.id.partitionKey === "1")
            .execute
            .absolve
        } yield assertTrue(x == User("1", "John"))
      )
    }
  )
}

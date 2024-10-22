package foo

import zio.test.ZIOSpecDefault
import zio.test.assertTrue
import zio.dynamodb.DynamoDBQuery
import zio.schema.DeriveSchema
import zio.schema.Schema
import zio.dynamodb.ProjectionExpression
import zio.dynamodb.PrimaryKey
import zio.schema.annotation.caseName
import zio.schema.annotation.discriminatorName
import zio.test.TestAspect

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
    },
    test("sealed trait") {
      @discriminatorName("animalType")
      sealed trait Animal
      object Animal {
        // compiler macro error "Deriving schema for Animal is not supported"
//        implicit val schema: Schema[Animal] = DeriveSchema.gen[Animal]
      }
      @caseName("the_dog")
      final case class Dog(id: String, name: String) extends Animal
      object Dog {
        implicit val schema: Schema.CaseClass2[String, String, Dog] =
          DeriveSchema.gen[Dog]
        val (id, name) = ProjectionExpression.accessors[Dog]
      }
      final case class Cat(id: String, name: String) extends Animal
      object Cat {
        implicit val schema: Schema.CaseClass2[String, String, Cat] =
          DeriveSchema.gen[Cat]
        val (id, name) = ProjectionExpression.accessors[Cat]
      }

      withSingleIdKeyTable(tableName =>
        for {
          _ <- DynamoDBQuery.put(tableName, Dog("1", "John")).execute
          x <- DynamoDBQuery.getItem(tableName, PrimaryKey("id" -> "1")).execute
          _ = println(s"XXXXXXXXXXXX $x")
        } yield assertTrue(1 == 1)
      )
    }
  ) @@ TestAspect.nondeterministic
}

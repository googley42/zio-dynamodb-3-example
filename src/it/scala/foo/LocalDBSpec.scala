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
import zio.dynamodb.Item

object LocalDBSpec extends DynamoDBLocalSpec:
  def spec = suite("DynamoDB local suite")(
    test("simple put and get") {
      final case class User(id: String, name: String)
      object User:
        given schema: Schema.CaseClass2[String, String, User] =
          DeriveSchema.gen[User]
        val (id, name) = ProjectionExpression.accessors[User]

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
      object Animal:
        @caseName("the_dog")
        final case class Dog(id: String, name: String) extends Animal
        object Dog:
          given schema: Schema.CaseClass2[String, String, Dog] =
            DeriveSchema.gen[Dog]
          val (id, name) = ProjectionExpression.accessors[Dog]
        final case class Cat(id: String, name: String) extends Animal
        object Cat:
          given schema: Schema.CaseClass2[String, String, Cat] =
            DeriveSchema.gen[Cat]
          val (id, name) = ProjectionExpression.accessors[Cat]
        given schema: Schema.Enum2[Dog, Cat, Animal] =
          DeriveSchema.gen[Animal]
        val (dog, cat) = ProjectionExpression.accessors[Animal]

      withSingleIdKeyTable(tableName =>
        for
          // when we do a put of a sealed trait, we need to specify the trait type otherwise the discriminator is not set
          _ <- DynamoDBQuery
            .put[Animal](tableName, Animal.Dog("1", "John"))
            .execute
          x <- DynamoDBQuery.getItem(tableName, PrimaryKey("id" -> "1")).execute
        yield assertTrue(
          x == Some(
            Item("id" -> "1", "animalType" -> "the_dog", "name" -> "John")
          )
        )
      )
    }
  ) @@ TestAspect.nondeterministic

end LocalDBSpec

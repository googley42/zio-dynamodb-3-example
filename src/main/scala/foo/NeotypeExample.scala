package foo

import neotype.*
import zio.schema.Schema
import zio.schema.DeriveSchema
import zio.dynamodb.json.*
import neotype.interop.zioschema.given
import zio.dynamodb.ProjectionExpression

object NeotypeExample extends App {
  type NonEmptyString = NonEmptyString.Type
  object NonEmptyString extends Newtype[String]:
    override inline def validate(input: String): Boolean =
      input.nonEmpty

  final case class User(id: String, name: NonEmptyString)
  object User:
    given schema: Schema.CaseClass2[String, NonEmptyString, User] =
      DeriveSchema.gen[User]
    val (id, name) = ProjectionExpression.accessors[User]

  val user = User("1", NonEmptyString("John"))
  val json = user.toJsonStringPretty
  println(json)
  /*
    {
        "name" : {
            "S" : "John"
        },
        "id" : {
            "S" : "1"
        }
    }
   */
}

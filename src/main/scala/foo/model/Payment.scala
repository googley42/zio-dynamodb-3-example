package foo.model

import zio.dynamodb.Annotations.enumOfCaseObjects
import zio.schema.Schema
import zio.schema.DeriveSchema

@enumOfCaseObjects
sealed trait Payment

object Payment {
  case object DebitCard extends Payment

  case object CreditCard extends Payment

  case object PayPal extends Payment

  implicit val schema: Schema.Enum3[DebitCard.type, CreditCard.type, PayPal.type, Payment] = DeriveSchema.gen[Payment]
}

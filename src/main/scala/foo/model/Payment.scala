package foo.model

import zio.schema.Schema
import zio.schema.DeriveSchema

enum Payment:
  case DebitCard
  case CreditCard
  case PayPal
object Payment:
  implicit val schema: Schema.Enum3[DebitCard.type, CreditCard.type, PayPal.type, Payment] = DeriveSchema.gen[Payment]


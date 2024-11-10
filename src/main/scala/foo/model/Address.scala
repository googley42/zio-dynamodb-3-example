package foo.model

import zio.schema.Schema
import zio.schema.DeriveSchema

final case class Address(addr1: String, postcode: String)

object Address:
  given schema: Schema.CaseClass2[String, String, Address] =
    DeriveSchema.gen[Address]

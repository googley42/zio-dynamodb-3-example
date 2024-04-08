package foo.model

import zio.dynamodb.{PrimaryKey, ProjectionExpression}
import zio.schema.DeriveSchema

import java.time.Instant
import zio.schema.Schema
import zio.dynamodb.KeyConditionExpr.PrimaryKeyExpr
import zio.dynamodb.KeyConditionExpr

final case class Student(
    email: String,
    subject: String,
    enrollmentDate: Option[Instant],
    payment: Payment,
    altPayment: Payment,
    studentNumber: Int,
    collegeName: String,
    address: Option[Address] = None,
    addresses: List[Address] = List.empty[Address],
    groups: Set[String] = Set.empty[String],
    version: Int = 0
)

object Student {
  implicit val schema: Schema.CaseClass11[
    String,
    String,
    Option[Instant],
    Payment,
    Payment,
    Int,
    String,
    Option[Address],
    List[Address],
    Set[String],
    Int,
    Student
  ] = DeriveSchema.gen[Student]
  val (
    email,
    subject,
    enrollmentDate,
    payment,
    altPayment,
    studentNumber,
    collegeName,
    address,
    addresses,
    groups,
    version
  ) =
    ProjectionExpression.accessors[Student]

  def primaryKey(email: String, subject: String): PrimaryKeyExpr[Student] =
    Student.email.partitionKey === email && Student.subject.sortKey === subject

  val enrolDate = Instant.parse("2021-03-20T01:39:33Z")
  val enrolDate2 = Instant.parse("2022-03-20T01:39:33Z")

  val avi = Student(
    "avi@gmail.com",
    "maths",
    Some(enrolDate),
    Payment.DebitCard,
    Payment.CreditCard,
    1,
    "college1",
    None,
    List(Address("line2", "postcode2")),
    Set("group1", "group2")
  )
  val adam = Student(
    "adam@gmail.com",
    "english",
    Some(enrolDate),
    Payment.CreditCard,
    Payment.DebitCard,
    2,
    "college1",
    None,
    List.empty,
    Set(
      "group1",
      "group2"
    )
  )

}

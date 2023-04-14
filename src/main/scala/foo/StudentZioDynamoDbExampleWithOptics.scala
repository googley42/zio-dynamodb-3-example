package foo

import zio.dynamodb._
import zio.dynamodb.DynamoDBQuery._
import foo.model.{ Address, Payment, Student }
import foo.model.Student.schema
import foo.DynamoDB.dynamoDbLayer
import zio.stream.ZStream
import zio.{ Console, ZIOAppDefault }

import java.time.Instant

/**
 * Type safe API example
 */
object StudentZioDynamoDbExampleWithOptics extends ZIOAppDefault {

  val enrollmentDateTyped: ProjectionExpression[Student, Option[Instant]] = Student.enrollmentDate

  private val program = for {
    _ <- createTable("student", KeySchema("email", "subject"), BillingMode.PayPerRequest)(
           AttributeDefinition.attrDefnString("email"),
           AttributeDefinition.attrDefnString("subject")
         ).execute
    _ <- batchWriteFromStream(ZStream(Student.avi, Student.adam)) { student =>
           put("student", student)
         }.runDrain
    _ <- put("student", Student.avi.copy(payment = Payment.CreditCard)).execute
    _ <- batchReadFromStream("student", ZStream(Student.avi, Student.adam))(s => Student.primaryKey(s.email, s.subject))
           .tap(pair => Console.printLine(s"student=${pair._2}"))
           .runDrain
    _ <- scanAll[Student]("student").filter {
           Student.enrollmentDate === Some(
             Student.enrolDate
           ) && Student.payment === Payment.CreditCard
         }.execute
           .map(_.runCollect)
    _ <- queryAll[Student]("student")
           .filter(
             Student.enrollmentDate === Some(Student.enrolDate) && Student.payment === Payment.CreditCard
           )
           .whereKey(Student.email === "avi@gmail.com" && Student.subject === "maths")
           .execute
           .map(_.runCollect)
    _ <- put[Student]("student", Student.avi)
           .where(
             Student.enrollmentDate === Some(
               Student.enrolDate
             ) && Student.email === "avi@gmail.com" && Student.payment === Payment.CreditCard
           )
           .execute
    _ <- update[Student]("student", Student.primaryKey("avi@gmail.com", "maths")) {
           Student.enrollmentDate.set(Some(Student.enrolDate2)) + Student.payment.set(Payment.PayPal) + Student.address
             .set(
               Some(Address("line1", "postcode1"))
             )
         }.execute
    _ <- delete("student", Student.primaryKey("adam@gmail.com", "english"))
           .where(
             Student.enrollmentDate === Some(
               Student.enrolDate
             ) && Student.payment === Payment.CreditCard
           )
           .execute
    _ <- scanAll[Student]("student").execute
           .tap(_.tap(student => Console.printLine(s"scanAll - student=$student")).runDrain)
    _ <- deleteTable("student").execute
  } yield ()

  override def run = program.provide(dynamoDbLayer, DynamoDBExecutor.live)
}

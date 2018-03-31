
case class RegisterUserRequest(
    age: Int,
    email: String,
    gender: Gender
)

sealed trait Gender
object Gender {
  case object Male extends Gender
  case object Female extends Gender
}

case class ActionRequest(
    userId: Long,
    videoId: Long,
    action: Action
)

sealed trait Action
object Action {
  case object Like extends Action
  case object Skip extends Action
  case object Play extends Action
}

object Codecs {
  import io.circe._

  implicit class DecoderExtension[A](val d: Decoder[A]) extends AnyVal {
    def validated(predicate: A => Option[String]): Decoder[A] =
      d.emap(a => predicate(a).toLeft(a))
  }

  val EmailRegexp = """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"""

  implicit val registerUserRequestDecoder: Decoder[RegisterUserRequest] =
    Decoder.forProduct3("age", "email", "gender")(RegisterUserRequest)(
    Decoder[Int]
      .validated(age => if (age < 5 || age > 120) Some("age is not valid") else None),
    Decoder[String]
      .validated(email => if (email matches EmailRegexp) None else Some("email is not valid")),
    Decoder[Int]
      .emap[Gender] {
        case 1 => Right(Gender.Male)
        case 2 => Right(Gender.Female)
        case _ => Left("gender is not valid")
      }
  )

  implicit val actionDecoder: Decoder[Action] =
    Decoder[Int].emap {
      case 1 => Right(Action.Like)
      case 2 => Right(Action.Skip)
      case 3 => Right(Action.Play)
      case _ => Left("action is not valid")
    }

  implicit val actionRequestDecoder: Decoder[ActionRequest] =
    Decoder.forProduct3("userId", "videoId", "action")(ActionRequest)
}


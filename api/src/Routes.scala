import akka.http.scaladsl.server.{ExceptionHandler, MalformedRequestContentRejection, RejectionHandler, Route}
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import Codecs._

class Routes {

  def handler: Route =
    handleRejections(rejectionHandler) {
      post(
        path("register") {
          entity(as[RegisterUserRequest]) {
            request =>
              complete("OK")
          }
        } ~
          path("action") {
            entity(as[ActionRequest]) {
              request =>
                complete("OK")
            }
          }
      )
    }

  def rejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case MalformedRequestContentRejection(_, DecodingFailures(failures)) =>
          complete {
            import io.circe.Json._
            obj(
              "erros" -> fromValues(failures.map(f => fromString(f.message)).toList)
            )
          }
      }
      .result()

}

import mill.scalalib._

object api extends ScalaModule {
  def scalaVersion = "2.12.4"

  def scalacOptions = Seq("-Ypartial-unification")

  def ivyDeps = Agg (
    ivy"com.typesafe.akka::akka-http:10.1.0",
    ivy"org.typelevel::cats-core:1.1.0"
  ) 
}

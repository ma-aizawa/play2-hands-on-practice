package controllers

import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.slick._
import slick.driver.JdbcProfile
import models.Tables._
import javax.inject.Inject
import scala.concurrent.Future
import slick.driver.H2Driver.api._

import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonController {
  // UsersRowをJSONに変換するためのWritesを定義
  implicit val usersRowWritesWrites = (
    (__ \ "id" ).write[Long] and
    (__ \ "name" ).write[String] and
    (__ \ "companyId" ).writeNullable[Int]
  )(unlift(UsersRow.unapply))
}

class JsonController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller
    with HasDatabaseConfigProvider[JdbcProfile] {
  import JsonController._

  def list = Action.async { implicit rs =>
    // ID昇順に全て取得
    db.run(Users.sortBy(t => t.id).result).map { users =>
      Ok(Json.obj("users" -> users))
    }
  }

  def create = TODO

  def update = TODO

  def remove(id: Long) = TODO
}

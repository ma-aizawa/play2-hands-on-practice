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
    (__ \ "id").write[Long] and
    (__ \ "name").write[String] and
    (__ \ "companyId").writeNullable[Int]
  )(unlift(UsersRow.unapply))

  // ユーザー情報のためのケースクラス
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])

  // JSONをUserFormに変換するためのReadsを定義
  implicit val userFormFormat = (
    (__ \ "id").readNullable[Long] and
    (__ \ "name").read[String] and
    (__ \ "companyId").readNullable[Int]
  )(UserForm)

  /* JSON DSFを使わない変換
  implict val userFormFormat = new Reads[UserForm]{
    def read(js: JsValue): UserForm = {
      UserForm(
        id = (js \ "id").asOpt[Long],
        name = (js \ "name").as[String],
        companyId = (js \ "companyId").asOpt[Int]
      )
    }
  }
  */

  /* マクロを使って定義
  implicit val userFormReads = Json.reads[UserForm]
  implicit val userFormWrites = Json.writes[UserForm]
  */
  /* マクロを使ってWritesとReadsをまとめて定義
  implicit val userFormFormat = Json.format[UserForm]
  */
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

  def create = Action.async(parse.json) { implicit rs =>
    rs.body.validate[UserForm].map { form =>
      val user = UsersRow(0, form.name, form.companyId)
      db.run(Users += user).map { _ =>
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal { e =>
      Future {
        BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toJson(e)))
      }
    }
  }

  def update = Action.async(parse.json) { implicit rs =>
    rs.body.validate[UserForm].map { form =>
      val user = UsersRow(form.id.get, form.name, form.companyId)
      db.run(Users.filter(t => t.id === user.id.bind).update(user)).map { _ =>
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal {e =>
      Future {
        BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toJson(e)))
      }
    }
  }

  def remove(id: Long) = Action.async { implicit rs =>
    db.run(Users.filter(t => t.id === id.bind).delete).map { _ =>
      Ok(Json.obj("result" -> "success"))
    }
  }
}

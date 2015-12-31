package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.db.slick._
import slick.driver.JdbcProfile
import models.Tables._
import javax.inject.Inject
import scala.concurrent.Future
import slick.driver.H2Driver.api._

class UserController @Inject() (val dbConfigProvider: DatabaseConfigProvider,
                                val messagesApi: MessagesApi) extends Controller
    with HasDatabaseConfigProvider[JdbcProfile] with I18nSupport {

  def list = Action.async { implicit rs =>
    // IDの昇順に全てのユーザ情報を取得
    db.run(Users.sortBy(t => t.id).result).map { users =>
      Ok(views.html.user.list(users))
    }
  }

  def edit(id: Option[Long]) = TODO

  def create = TODO

  def update = TODO

  def remove(id: Long) = TODO
}


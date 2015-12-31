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

class JsonController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller
    with HasDatabaseConfigProvider[JdbcProfile] {

  def list = TODO

  def create = TODO

  def update = TODO

  def remove(id: Long) = TODO
}

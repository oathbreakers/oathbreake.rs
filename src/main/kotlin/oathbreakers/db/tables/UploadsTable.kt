package oathbreakers.db.tables

import org.jetbrains.exposed.sql.Table

object UploadsTable : Table("uploads") {
    val uploader = integer("uploader")
    val slug = text("slug")
    val urlDestination = text("url_destination").nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(slug)
}
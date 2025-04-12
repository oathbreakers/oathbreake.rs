@file:OptIn(ExperimentalDatabaseMigrationApi::class)

package oathbreakers.db

import MigrationUtils
import oathbreakers.db.tables.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension

const val URL = "jdbc:postgresql://localhost:5432/postgres"
const val USER = "postgres"
const val PASSWORD = "postgres"
const val MIGRATIONS_DIRECTORY = "src/main/resources/migrations"
val TABLES = arrayOf(UsersTable)

val database = Database.connect(
    url = URL,
    user = USER,
    password = PASSWORD
)

fun main() {
    transaction(database) {
        generateMigrationScript()
    }
}

fun generateMigrationScript() {
    var highestVersion = 0
    Files.walk(Paths.get(MIGRATIONS_DIRECTORY), 1).forEach {
        if (it.isDirectory()) {
            return@forEach
        }

        val ext = it.fileName.extension
        if (ext != "sql") {
            return@forEach
        }

        val name = it.fileName.nameWithoutExtension
        val split = name.split("__")

        var ver = split[0]
        if (!ver.startsWith("V")) {
            return@forEach
        }

        ver = ver.substring(1)

        val verParsed = ver.toInt()
        if (verParsed > highestVersion) {
            highestVersion = verParsed
        }
    }

    MigrationUtils.generateMigrationScript(
        *TABLES,
        scriptDirectory = MIGRATIONS_DIRECTORY,
        scriptName = "V${highestVersion + 1}__RENAME_BEFORE_USE"
    )
}
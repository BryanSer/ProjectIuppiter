package com.github.bryanser.common.database

import Br.API.Utils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.ktorm.database.Database
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import org.ktorm.support.mysql.MySqlDialect
import java.io.File
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import java.util.*

lateinit var database: Database

fun initDatabase(plugin: Plugin) {
    val file = File(plugin.dataFolder, "database.yml")
    if (!file.exists()) {
        Utils.saveResource(plugin, "database.yml", plugin.dataFolder)
        if (!file.exists()) {
            throw IllegalStateException("数据库文件初始化失败")
        }
    }
    val db = YamlConfiguration.loadConfiguration(file)

    val sb = StringBuilder(
        String.format(
            "jdbc:mysql://%s:%d/%s?user=%s&password=%s",
            db.getString("host"),
            db.getInt("port"),
            db.getString("database"),
            db.getString("user"),
            db.getString("password")
        )
    )
    for (s in db.getStringList("options")) {
        sb.append('&')
        sb.append(s)
    }
    val config = HikariConfig()
    config.jdbcUrl = sb.toString()
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    config.idleTimeout = 60000
    config.connectionTimeout = 60000
    config.validationTimeout = 3000
    config.maxLifetime = 60000
    val source = HikariDataSource(config)
    database = Database.connect(source, dialect = MySqlDialect())
}

fun <E : Any> BaseTable<E>.uuid(name: String): Column<UUID> = registerColumn(name, UUIDSQLType)
fun <E : Any> BaseTable<E>.listString(name: String): Column<List<String>> = registerColumn(name, ListStringSQLType)

object UUIDSQLType : SqlType<UUID>(Types.VARCHAR, typeName = "uuid") {
    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: UUID) {
        ps.setString(index, parameter.toString())
    }

    override fun doGetResult(rs: ResultSet, index: Int): UUID? = rs.getString(index)?.let {
        UUID.fromString(it)
    }
}

object ListStringSQLType : SqlType<List<String>>(Types.OTHER, typeName = "listString") {
    val gson = GsonBuilder()
        .serializeNulls()
        .setLenient()
        .create()
    val type = object : TypeToken<List<String>>(){}.type
    override fun doGetResult(rs: ResultSet, index: Int): List<String>? {
        return rs.getString(index)?.let {
            gson.fromJson(it, type)
        }
    }

    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: List<String>) {
        val json = gson.toJson(parameter)
        ps.setString(index, json)
    }

}
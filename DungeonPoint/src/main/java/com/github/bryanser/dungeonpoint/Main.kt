package com.github.bryanser.dungeonpoint

import com.github.bryanser.common.coroutines.BukkitDispatchers
import com.github.bryanser.common.coroutines.launchOnIO
import com.github.bryanser.common.database.initDatabase
import com.github.bryanser.dungeonpoint.table.*
import fw.group.Group
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.Node
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Main : JavaPlugin() {
    companion object {
        lateinit var plugin: Main
    }

    private val permissionAPI by lazy{
        Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)?.provider
    }

    override fun onLoad() {
        super.onLoad()
        plugin = this
    }

    override fun onEnable() {
        BukkitDispatchers.init(this)
        initDatabase(this)
        createTable()
    }

    override fun onDisable() {
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("§c你没有权限使用这个命令")
            return true
        }
        if (args.isEmpty() || args[0].equals("help", ignoreCase = true)) {
            return false
        }
        if (args[0].equals("info", true) && args.size >= 2) {
            val dun = args[1]
            launchOnIO {
                val dungeonInfo = DungeonInfoTable.queryDungeonInfo(dun)
                if (dungeonInfo == null) {
                    sender.sendMessage("§c在数据库中找不到副本: $dun 的任何信息.")
                    return@launchOnIO
                }
                sender.sendMessage("§6以下是副本[$dun]的信息:")
                sender.sendMessage("§6完成所需分数: ${dungeonInfo.maxPoint}")
                sender.sendMessage("§6通关获得的权限(${dungeonInfo.permissions.size}条): ")
                for (permission in dungeonInfo.permissions) {
                    sender.sendMessage("§a    $permission")
                }
            }
            return true
        }
        if (args[0].equals("max", true) && args.size >= 3) {
            val name = args[1]
            val point = args[2].toIntOrNull() ?: run {
                sender.sendMessage("§c你输入的不是数字")
                return true
            }
            launchOnIO {
                val dungeonInfo = DungeonInfoTable.queryDungeonInfo(name) ?: DungeonInfo {
                    dungeonName = name
                    this.maxPoint = point
                    permissions = emptyList()
                }
                DungeonInfoTable.updatePlayerPointRecord(dungeonInfo)
                sender.sendMessage("§6设定更新完毕")
            }
            return true
        }
        if (args[0].equals("permission") && args.size >= 4) {
            val name = args[2]
            val permission = args[3]
            launchOnIO {
                val dungeonInfo = DungeonInfoTable.queryDungeonInfo(name) ?: DungeonInfo {
                    dungeonName = name
                    this.maxPoint = 1000
                    permissions = emptyList()
                }
                if (args[1].equals("add", true)) {
                    dungeonInfo.permissions = dungeonInfo.permissions.toMutableList().also {
                        it.add(permission)
                    }.toList()
                } else if (args[1].equals("remove", true)) {
                    val list = dungeonInfo.permissions.toMutableList()
                    if (!list.contains(permission)) {
                        sender.sendMessage("§c这个副本没有设定过权限: $permission")
                        return@launchOnIO
                    }
                    list.remove(permission)
                    dungeonInfo.permissions = list
                } else {
                    sender.sendMessage("§c输入的命令格式不正确")
                    return@launchOnIO
                }
                DungeonInfoTable.updatePlayerPointRecord(dungeonInfo)
                sender.sendMessage("§6设定更新完毕")
            }
            return true
        }
        if (args[0].equals("point", true) && args.size >= 4) {
            val dunName = args[1]
            val point = args[2].toIntOrNull() ?: return false
            val key = args[3]
            val playerName = args.getOrNull(4)
            val player = if (playerName != null) {
                Bukkit.getPlayer(playerName)
            } else if (sender is Player) {
                sender
            } else {
                sender.sendMessage("§c找不到玩家${playerName}")
                return true
            }
            val group = Group.SearchPlayerInGroup(player)
            if(group == null){
                sender.sendMessage("§c目标玩家: ${player.name}不在任何副本中")
                return true
            }
            val playerList = group.playerList
            launchOnIO {
                val dungeonInfo = DungeonInfoTable.queryDungeonInfo(dunName)
                if(dungeonInfo == null){
                    sender.sendMessage("§c当前副本没有设定过任何积分配置 无法获得积分")
                    return@launchOnIO
                }
                playerList.map{ p ->
                    launchOnIO {
                        val uuid = p.uniqueId
                        if(DungeonKeyTable.queryPlayerKeyExists(
                                uuid, dunName, key
                            )){
                            return@launchOnIO
                        }
                        DungeonKeyTable.addPlayerKey(uuid,dunName, key)
                        val currentPoint = DungeonPointRecordTable.queryPlayerPointRecord(
                            uuid, dunName
                        )
                        DungeonPointRecordTable.updatePlayerPointRecord(
                            uuid, dunName, currentPoint + point
                        )
                        p.sendMessage("§6任务完成获得${point}积分,当前总积分: ${currentPoint + point}")
                        if(currentPoint + point >= dungeonInfo.maxPoint && currentPoint < dungeonInfo.maxPoint){
                            p.sendMessage("§6当前副本已通关")
                            launchOnIO {
                                suspendCoroutine<Unit> {
                                    permissionAPI?.userManager?.modifyUser(uuid){
                                        for(permission in dungeonInfo.permissions){
                                            it.data().add(
                                                Node.builder(permission).build()
                                            )
                                        }
                                    }?.join()
                                    it.resume(Unit)
                                }
                            }
                        }
                    }
                }.joinAll()

            }
            return true

        }
        return false
    }
}

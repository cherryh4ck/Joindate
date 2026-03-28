package io.github.Cherryh4ck.joindate

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

import java.text.SimpleDateFormat
import java.util.Date

class Command(private val plugin: Joindate) : CommandExecutor {
    val minimessage = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player && args.isEmpty()) {
            val message: String = plugin.config.getString("console-arguments-error") ?: "<red>'console-arguments-error' is invalid. This is a config error.</red>"
            sender.sendMessage(minimessage.deserialize(message))
            return true
        }

        val targetUser = if (args.isEmpty()) {
            sender.name
        } else{
            args[0]
        }

        if (targetUser.length !in 3..16) {
            var message: String = plugin.config.getString("invalid-name") ?: "<red>'invalid-name' is invalid. This is a config error.</red>"
            message = message.replace("%player%", targetUser)
            sender.sendMessage(minimessage.deserialize(message))
            return true
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val uuidData = File(plugin.playerDataPath, "${targetUser}.yml")
            val offlineplayer = if (!uuidData.exists()) {
                Bukkit.getOfflinePlayer(targetUser)
            }
            else{
                val config = YamlConfiguration.loadConfiguration(uuidData)
                val uuid = java.util.UUID.fromString(config.getString("uuid"))
                Bukkit.getOfflinePlayer(uuid)
            }

            if (!offlineplayer.hasPlayedBefore() && !offlineplayer.isOnline){
                var message: String = plugin.config.getString("never-entered") ?: "<red>'never-entered' is invalid. This is a config error.</red>"
                message = message.replace("%player%", targetUser)

                sender.sendMessage(minimessage.deserialize(message))
                return@Runnable
            }

            val unixTime = offlineplayer.firstPlayed
            val format = if (plugin.config.getBoolean("alternative-format")) { SimpleDateFormat("dd/MM/yyyy HH:mm") } else { SimpleDateFormat("MM/dd/yyyy hh:mm a") }
            val result = format.format(Date(unixTime))

            var message: String = plugin.config.getString("join-date") ?: "<red>'join-date' is invalid. This is a config error.</red>"
            message = message.replace("%player%", targetUser)
            message = message.replace("%date%", result)

            sender.sendMessage(minimessage.deserialize(message))
        })

        return true
    }
}
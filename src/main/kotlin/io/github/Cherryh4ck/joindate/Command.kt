package io.github.Cherryh4ck.joindate

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

import java.text.SimpleDateFormat
import java.util.Date

class Command(private val plugin: Joindate) : TabExecutor {
    val minimessage = MiniMessage.miniMessage()

    fun validateUsername(username: String): Boolean {
        val regex = Regex(plugin.usernameRegex)
        return regex.matches(username)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player && args.isEmpty()) {
            sender.sendMessage(minimessage.deserialize(plugin.consoleError))
            return true
        }

        val targetUser = if (args.isEmpty()) {
            sender.name
        } else{
            args[0]
        }

        if (!validateUsername(targetUser)) {
            val message = plugin.invalidName.replace("%player%", targetUser)
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
                val message = plugin.neverEntered.replace("%player%", targetUser)
                sender.sendMessage(minimessage.deserialize(message))
                return@Runnable
            }

            val unixTime = offlineplayer.firstPlayed
            val format = if (plugin.alternativeFormat) {
                SimpleDateFormat("dd/MM/yyyy HH:mm") }
            else {
                SimpleDateFormat("MM/dd/yyyy hh:mm a")
            }
            val result = format.format(Date(unixTime))

            var message: String = if (targetUser != sender.name){
                plugin.joinDate
            }
            else{
                plugin.sameUsername
            }
            message = message.replace("%player%", targetUser)
            message = message.replace("%date%", result)

            sender.sendMessage(minimessage.deserialize(message))
        })

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        return if (args.size == 1){
            Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        else{
            emptyList()
        }
    }
}
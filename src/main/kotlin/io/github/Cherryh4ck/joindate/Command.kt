package io.github.Cherryh4ck.joindate

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit.getOfflinePlayer

import java.text.SimpleDateFormat
import java.util.Date

class Command(private val plugin: Plugin) : CommandExecutor {
    val minimessage = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player){
            val userLocale = sender.locale().toString()

            val targetUser = if (args.isEmpty()) {
                sender.name
            } else{
                args[0]
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                val offlineplayer = getOfflinePlayer(targetUser)

                val isSpanish = userLocale.startsWith("es")
                if (!offlineplayer.hasPlayedBefore() && !offlineplayer.isOnline){
                    val message = if (isSpanish){
                        minimessage.deserialize("<red>${offlineplayer.name} nunca entró al servidor.</red>")
                    }
                    else{
                        minimessage.deserialize("<red>${offlineplayer.name} has never entered the server.</red>")
                    }

                    sender.sendMessage(message)
                    return@Runnable
                }

                val unixTime = offlineplayer.firstPlayed
                val format = if (isSpanish) { SimpleDateFormat("dd/MM/yyyy HH:mm") } else { SimpleDateFormat("MM/dd/yyyy hh:mm a") }
                val result = format.format(Date(unixTime))

                val message = if (isSpanish){
                    minimessage.deserialize("<gold>${offlineplayer.name} se unió al servidor el <bold>${result}</bold>.</gold>")
                }
                else{
                    minimessage.deserialize("<gold>${offlineplayer.name} joined the server on <bold>${result}</bold>.</gold>")
                }

                sender.sendMessage(message)
            })
        }
        else{
            return false
        }

        return true
    }
}
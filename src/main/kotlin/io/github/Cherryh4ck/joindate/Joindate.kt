package io.github.Cherryh4ck.joindate

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class Joindate : JavaPlugin() {
    val minimessage = MiniMessage.miniMessage()

    override fun onEnable() {
        saveDefaultConfig()

        logger.info("Plugin activated.")

        val simplejd = getCommand("simplejd")

        getCommand("joindate")?.setExecutor(Command(this))
        getCommand("jd")?.setExecutor(Command(this))

        simplejd?.setExecutor(this)
        simplejd?.tabCompleter = this
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
        var mensaje : String
        if(args.isEmpty()){
            mensaje = config.getString("plugin-info") ?: "<red>'plugin-info' is invalid. This is a config error.</red>"
            mensaje = mensaje.replace("%version%", this.pluginMeta.version)
            sender.sendMessage(minimessage.deserialize(mensaje))
            return true
        }

        when (args[0].lowercase()) {
            "reload" ->{
                reloadConfig()
                mensaje = config.getString("reload-message") ?: "<red>'reload-message' is invalid. This is a config error.</red>"
                sender.sendMessage(minimessage.deserialize(mensaje))
            }
            else ->{
                mensaje = config.getString("command-doesnt-exist") ?: "<red>'command-doesnt-exist' is invalid. This is a config error.</red>"
                sender.sendMessage(minimessage.deserialize(mensaje))
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: org.bukkit.command.Command, alias: String, args: Array<out String>): List<String>? {
        return if (args.size == 1) {
            listOf("reload").filter { it.startsWith(args[0], ignoreCase = true) }
        } else {
            emptyList()
        }
    }
}

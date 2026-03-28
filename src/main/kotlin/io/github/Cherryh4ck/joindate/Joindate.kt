package io.github.Cherryh4ck.joindate

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Joindate : JavaPlugin() {
    val minimessage = MiniMessage.miniMessage()
    val playerDataPath = File(dataFolder, "playerdata")

    private var cacheListener: CacheJoinListener? = null

    override fun onEnable() {
        saveDefaultConfig()
        if (!playerDataPath.exists()) {
            logger.info("Playerdata folder created.")
            playerDataPath.mkdirs()
        }

        val simplejd = getCommand("simplejd")

        getCommand("joindate")?.setExecutor(Command(this))
        getCommand("jd")?.setExecutor(Command(this))

        hookListeners()

        logger.info("Plugin activated.")

        simplejd?.setExecutor(this)
        simplejd?.tabCompleter = this
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun hookListeners(){
        if (config.getBoolean("use-cache-system")){
            if (cacheListener == null){
                cacheListener = CacheJoinListener(this)
                server.pluginManager.registerEvents(cacheListener!!, this)
                logger.info("Successfully hooked the cache listener.")
            }
        }
        else{
            unhookListeners()
            logger.info("Cache listener is disabled (config.yml).")
        }
    }

    fun unhookListeners() {
        cacheListener?.let {
            HandlerList.unregisterAll(it)
            cacheListener = null
        }
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
            "reload" -> {
                reloadConfig()
                unhookListeners()
                hookListeners()
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

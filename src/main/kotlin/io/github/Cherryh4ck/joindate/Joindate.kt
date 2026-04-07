package io.github.Cherryh4ck.joindate

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Joindate : JavaPlugin() {
    val minimessage = MiniMessage.miniMessage()
    val playerDataPath = File(dataFolder, "playerdata")

    var pluginInfo = config.getString("plugin-info") ?: "<red>'plugin-info' is invalid. This is a config error.</red>"
    var reloadMessage = config.getString("reload-message") ?: "<red>'reload-message' is invalid. This is a config error.</red>"
    var incorrectCommand = config.getString("command-doesnt-exist") ?: "<red>'command-doesnt-exist' is invalid. This is a config error.</red>"

    var usernameRegex = config.getString("username-regex") ?: "^[a-zA-Z0-9_]{3,16}\$"

    var consoleError = config.getString("console-arguments-error") ?: "<red>'console-arguments-error' is invalid. This is a config error.</red>"
    var invalidName = config.getString("invalid-name") ?: "<red>'invalid-name' is invalid. This is a config error.</red>"
    var neverEntered = config.getString("never-entered") ?: "<red>'never-entered' is invalid. This is a config error.</red>"
    var alternativeFormat = config.getBoolean("alternative-format")
    var joinDate = config.getString("join-date") ?: "<red>'join-date' is invalid. This is a config error.</red>"
    var sameUsername = config.getString("same-username") ?: "<red>'same-username' is invalid. This is a config error.</red>"

    var logCreationEnabled = config.getBoolean("log-cache")
    var logCreation = config.getString("log-creation") ?: "<red>'log-creation' is invalid. This is a config error.</red>"

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

    fun reloadVariables(){
        pluginInfo = config.getString("plugin-info") ?: "<red>'plugin-info' is invalid. This is a config error.</red>"
        reloadMessage = config.getString("reload-message") ?: "<red>'reload-message' is invalid. This is a config error.</red>"
        incorrectCommand = config.getString("command-doesnt-exist") ?: "<red>'command-doesnt-exist' is invalid. This is a config error.</red>"

        usernameRegex = config.getString("username-regex") ?: "^[a-zA-Z0-9_]{3,16}\$"

        consoleError = config.getString("console-arguments-error") ?: "<red>'console-arguments-error' is invalid. This is a config error.</red>"
        invalidName = config.getString("invalid-name") ?: "<red>'invalid-name' is invalid. This is a config error.</red>"
        neverEntered = config.getString("never-entered") ?: "<red>'never-entered' is invalid. This is a config error.</red>"
        alternativeFormat = config.getBoolean("alternative-format")
        joinDate = config.getString("join-date") ?: "<red>'join-date' is invalid. This is a config error.</red>"
        sameUsername = config.getString("same-username") ?: "<red>'same-username' is invalid. This is a config error.</red>"

        logCreationEnabled = config.getBoolean("log-cache")
        logCreation = config.getString("log-creation") ?: "<red>'log-creation' is invalid. This is a config error.</red>"
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
        if(args.isEmpty()){
            val message = pluginInfo.replace("%version%", this.pluginMeta.version)
            sender.sendMessage(minimessage.deserialize(message))
            return true
        }

        when (args[0].lowercase()) {
            "reload" -> {
                reloadConfig()
                reloadVariables()
                unhookListeners()
                hookListeners()
                sender.sendMessage(minimessage.deserialize(reloadMessage))
            }
            else ->{
                sender.sendMessage(minimessage.deserialize(incorrectCommand))
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
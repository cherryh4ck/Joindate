package io.github.Cherryh4ck.joindate

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File

class CacheJoinListener(private val plugin : Joindate) : Listener {
    @EventHandler

    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val playerData = File(plugin.playerDataPath, "${player.name}.yml")
            val config = YamlConfiguration.loadConfiguration(playerData)

            val getUuid = config.getString("uuid")

            if (getUuid == null || getUuid != player.uniqueId.toString()) {
                config.set("uuid", player.uniqueId.toString())
                try {
                    config.save(playerData)
                    if (plugin.config.getBoolean("log-cache")){
                        var message: String = plugin.config.getString("log-creation") ?: "<red>'log-creation' is invalid. This is a config error.</red>"
                        message = message.replace("%player%", player.name)
                        plugin.logger.info(message)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        })
    }
}
package io.github.Cherryh4ck.joindate

import org.bukkit.plugin.java.JavaPlugin

class Joindate : JavaPlugin() {

    override fun onEnable() {
        logger.info("Plugin activated.")

        getCommand("joindate")?.setExecutor(Command(this))
        getCommand("jd")?.setExecutor(Command(this))
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}

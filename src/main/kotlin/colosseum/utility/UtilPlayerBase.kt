package colosseum.utility

import colosseum.utility.events.PlayerMessageEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

object UtilPlayerBase {
    @JvmStatic
    fun sendMessage(client: Entity, messageList: List<String>) {
        for (curMessage in messageList) {
            sendMessage(client, curMessage)
        }
    }

    @JvmStatic
    fun sendMessage(client: Entity, message: String) {
        if (client !is Player) {
            return
        }

        val event = PlayerMessageEvent(client, message)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return
        }

        client.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

    @JvmStatic
    fun searchOnline(caller: Player, target: String, inform: Boolean): Player? {
        val matchList = ArrayList<Player>()

        for (cur in Bukkit.getOnlinePlayers()) {
            if (cur.name.equals(target, ignoreCase = true)) {
                return cur
            }

            if (cur.name.lowercase(Locale.getDefault()).contains(target.lowercase(Locale.getDefault()))) {
                matchList.add(cur)
            }
        }

        // No / Non-Unique
        if (matchList.size != 1) {
            if (!inform) {
                return null
            }

            // Inform
            sendMessage(caller, String.format("&e%s&r possible matches for [&e%s&r]", matchList.size, target))
            val stringBuilder = StringBuilder()
            for (matched in matchList) {
                stringBuilder.append(matched.name).append(",").append(" ")
            }
            stringBuilder.delete(stringBuilder.length - 2, stringBuilder.length)
            sendMessage(caller, String.format("Matches: &e%s&r", stringBuilder))

            return null
        }

        return matchList[0]
    }
}

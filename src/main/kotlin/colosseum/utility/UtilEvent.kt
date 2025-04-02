package colosseum.utility

import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent

object UtilEvent {
    @JvmStatic
    fun isAction(event: PlayerInteractEvent, action: ActionType?): Boolean {
        return when (action) {
            ActionType.L -> event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK
            ActionType.L_AIR -> event.action == Action.LEFT_CLICK_AIR
            ActionType.L_BLOCK -> event.action == Action.LEFT_CLICK_BLOCK
            ActionType.R -> event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK
            ActionType.R_AIR -> event.action == Action.RIGHT_CLICK_AIR
            ActionType.R_BLOCK -> event.action == Action.RIGHT_CLICK_BLOCK
            ActionType.ANY -> event.action != Action.PHYSICAL
            else -> false
        }
    }

    @JvmStatic
    fun isBowDamage(event: EntityDamageEvent): Boolean {
        if (event !is EntityDamageByEntityEvent) {
            return false
        }
        return event.damager is Arrow
    }

    @JvmStatic
    fun getDamagerEntity(event: EntityDamageEvent, ranged: Boolean): LivingEntity? {
        if (event !is EntityDamageByEntityEvent) {
            return null
        }

        //Get Damager
        if (event.damager is LivingEntity) {
            return event.damager as LivingEntity
        }

        if (!ranged) {
            return null
        }

        if (event.damager !is Projectile) {
            return null
        }

        val projectile = event.damager as Projectile

        if (projectile.shooter == null) {
            return null
        }
        if (projectile.shooter !is LivingEntity) {
            return null
        }
        return projectile.shooter as LivingEntity
    }

    enum class ActionType {
        L,
        L_AIR,
        L_BLOCK,
        R,
        R_AIR,
        R_BLOCK,
        ANY
    }
}

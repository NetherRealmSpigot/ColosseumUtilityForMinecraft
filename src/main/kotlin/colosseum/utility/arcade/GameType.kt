package colosseum.utility.arcade

import java.util.*

enum class GameType {
    Other("Other"),
    Unknown("Unknown"),
    None("None"),

    DragonEscape("Dragon Escape"),
    Gladiators("Gladiators"),
    HideAndSeek("Hide and Seek"),
    Bombers("Bombers"),
    SurvivalGames("Survival Games"),
    MicroBattle("Micro Battle"),
    MineStrike("MineStrike"),
    NanoGames("Nano Games"),
    Paintball("Paintball"),
    OneInTheQuiver("One in the Quiver"),
    Runner("Runner"),
    SkyWars("SkyWars"),
    SnowFight("Snow Fight"),
    Spleef("Spleef"),
    TurfWars("Turf Wars");

    val gameName: String
    val lobbyName: String

    constructor(gameName: String) : this(gameName, gameName)

    constructor(gameName: String, lobbyName: String) {
        this.gameName = gameName
        this.lobbyName = lobbyName
    }

    fun getName(): String {
        return gameName
    }

    companion object {
        @JvmStatic
        fun match(target: String): GameType? {
            val obj = target.lowercase(Locale.getDefault())
            var gameType: GameType? = null
            for (type in GameType.entries) {
                if (type.toString().lowercase(Locale.getDefault()).startsWith(obj) || type.getName().lowercase(Locale.getDefault()).startsWith(obj)) {
                    gameType = type
                }
            }
            return gameType
        }
    }
}
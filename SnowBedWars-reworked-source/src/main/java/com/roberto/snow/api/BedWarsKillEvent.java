package com.roberto.snow.api;

import com.roberto.snow.arena.Arena;
import org.bukkit.entity.Player;
public final class BedWarsKillEvent extends SnowEvent {
    private final Arena arena; private final Player killer; private final Player victim; private final boolean finalKill;
    public BedWarsKillEvent(Arena arena, Player killer, Player victim, boolean finalKill){this.arena=arena;this.killer=killer;this.victim=victim;this.finalKill=finalKill;}
    public Arena getArena(){return arena;} public Player getKiller(){return killer;} public Player getVictim(){return victim;} public boolean isFinalKill(){return finalKill;}
}

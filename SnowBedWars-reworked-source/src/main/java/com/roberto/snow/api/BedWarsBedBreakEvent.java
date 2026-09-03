package com.roberto.snow.api;

import com.roberto.snow.arena.Arena;
import org.bukkit.entity.Player;
public final class BedWarsBedBreakEvent extends SnowEvent {
    private final Arena arena; private final Player player;
    public BedWarsBedBreakEvent(Arena arena, Player player) { this.arena=arena; this.player=player; }
    public Arena getArena(){ return arena; } public Player getPlayer(){ return player; }
}

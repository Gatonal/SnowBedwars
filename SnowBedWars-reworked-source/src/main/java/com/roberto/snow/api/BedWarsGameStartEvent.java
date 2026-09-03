package com.roberto.snow.api;

import com.roberto.snow.arena.Arena;
public final class BedWarsGameStartEvent extends SnowEvent {
    private final Arena arena;
    public BedWarsGameStartEvent(Arena arena) { this.arena = arena; }
    public Arena getArena() { return arena; }
}

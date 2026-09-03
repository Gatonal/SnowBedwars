package com.roberto.snow.generator;

import com.roberto.snow.Snow;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/** A generator deliberately owns no entities, so no holograms can leak between games. */
public final class ResourceGenerator {
    private final Snow plugin; private final GeneratorType type; private final Location location;
    private final int configuredInterval, amount; private int remainingSeconds;
    public ResourceGenerator(Snow plugin, GeneratorType type, Location location) { this(plugin,type,location,0,1); }
    public ResourceGenerator(Snow plugin, GeneratorType type, Location location, int intervalTicks, int amount) {
        this.plugin=plugin; this.type=type; this.location=location; this.configuredInterval=intervalTicks;
        this.amount=Math.max(1,Math.min(5,amount)); this.remainingSeconds=interval();
    }
    public void tick() { if(--remainingSeconds>0)return; remainingSeconds=interval(); if(location.getWorld()!=null) location.getWorld().dropItemNaturally(location,new ItemStack(type.getMaterial(),amount)); }
    private int interval() { int ticks=configuredInterval>0?configuredInterval:plugin.getConfig().getInt("generators."+type.name().toLowerCase()+"-interval-ticks",20); return Math.max(1,ticks/20); }
    public GeneratorType getType(){return type;} public Location getLocation(){return location;}
}

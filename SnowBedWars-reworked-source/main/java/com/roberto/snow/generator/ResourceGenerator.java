package com.roberto.snow.generator;

import com.roberto.snow.Snow;
import com.roberto.snow.util.Text;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public final class ResourceGenerator {
    private final Snow plugin; private final GeneratorType type; private final Location location; private int remainingTicks; private ArmorStand hologram;
    public ResourceGenerator(Snow plugin,GeneratorType type,Location location){this.plugin=plugin;this.type=type;this.location=location;this.remainingTicks=interval();createHologram();}
    public void tick(){ if(--remainingTicks>0)return; remainingTicks=interval(); if(location.getWorld()!=null) location.getWorld().dropItemNaturally(location,new ItemStack(type.getMaterial())); updateHologram(); }
    private int interval(){return plugin.getConfig().getInt("generators."+type.name().toLowerCase()+"-interval-ticks",20)/20;}
    private void createHologram(){ if(location.getWorld()==null)return; hologram=location.getWorld().spawn(location.clone().add(0,1.5,0),ArmorStand.class); hologram.setVisible(false); hologram.setGravity(false); hologram.setCustomNameVisible(true); updateHologram(); }
    private void updateHologram(){if(hologram!=null&&!hologram.isDead())hologram.setCustomName(Text.color(type.getDisplay()+" &fem &b"+remainingTicks+"s"));}
    public GeneratorType getType(){return type;} public Location getLocation(){return location;}
}

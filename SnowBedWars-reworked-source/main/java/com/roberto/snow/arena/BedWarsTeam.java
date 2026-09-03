package com.roberto.snow.arena;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class BedWarsTeam {
    private final String id, name, colorCode;
    private final DyeColor woolColor;
    private final Set<UUID> members = new HashSet<UUID>();
    private Location spawn, bed;
    private boolean bedAlive = true;
    private int protectionLevel, sharpnessLevel, generatorLevel, healPoolLevel, traps;
    public BedWarsTeam(String id, String name, String colorCode, DyeColor woolColor) { this.id=id; this.name=name; this.colorCode=colorCode; this.woolColor=woolColor; }
    public boolean add(Player player, int limit) { if (members.size() >= limit) return false; members.add(player.getUniqueId()); return true; }
    public void remove(UUID player) { members.remove(player); }
    public boolean contains(UUID player) { return members.contains(player); }
    public Set<UUID> getMembers() { return Collections.unmodifiableSet(members); }
    public String getId() { return id; } public String getName() { return name; } public String getColorCode() { return colorCode; } public DyeColor getWoolColor() { return woolColor; }
    public Location getSpawn() { return spawn; } public void setSpawn(Location spawn) { this.spawn = spawn; }
    public Location getBed() { return bed; } public void setBed(Location bed) { this.bed = bed; }
    public boolean isBedAlive() { return bedAlive; } public void destroyBed() { bedAlive = false; }
    public void restoreBed() { bedAlive = true; }
    public int getProtectionLevel() { return protectionLevel; } public void increaseProtection() { protectionLevel++; }
    public int getSharpnessLevel() { return sharpnessLevel; } public void enableSharpness() { sharpnessLevel = 1; }
    public int getGeneratorLevel() { return generatorLevel; } public void increaseGenerator() { generatorLevel++; }
    public int getHealPoolLevel() { return healPoolLevel; } public void enableHealPool() { healPoolLevel = 1; }
    public int getTraps() { return traps; } public void addTrap() { traps++; }
}

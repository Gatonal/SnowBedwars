package com.roberto.snow.generator;
import org.bukkit.Material;
public enum GeneratorType {
    IRON(Material.IRON_INGOT, "&fFerro"), GOLD(Material.GOLD_INGOT, "&6Ouro"), DIAMOND(Material.DIAMOND, "&bDiamante"), EMERALD(Material.EMERALD, "&aEsmeralda");
    private final Material material; private final String display;
    GeneratorType(Material material,String display){this.material=material;this.display=display;}
    public Material getMaterial(){return material;} public String getDisplay(){return display;}
    public static GeneratorType parse(String value){try{return value==null?null:valueOf(value.toUpperCase());}catch(IllegalArgumentException ignored){return null;}}
}

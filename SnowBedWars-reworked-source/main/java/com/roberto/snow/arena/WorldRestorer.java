package com.roberto.snow.arena;

import com.roberto.snow.Snow;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import java.io.*;

/** Reset COPY seguro: só toca mundos explicitamente configurados e com template existente. */
public final class WorldRestorer {
    private final Snow plugin;
    public WorldRestorer(Snow plugin) { this.plugin=plugin; }
    public void restore(final Arena arena) {
        final String worldName=plugin.getConfig().getString("arenas."+arena.getId()+".world");
        final File template=new File(plugin.getDataFolder(),plugin.getConfig().getString("arena.restore.templates-folder","maps")+File.separator+arena.getId());
        if(worldName==null||worldName.trim().isEmpty()||!template.isDirectory()){plugin.getArenaManager().completeReset(arena);return;}
        Bukkit.getScheduler().runTask(plugin,new Runnable(){@Override public void run(){World world=Bukkit.getWorld(worldName);if(world!=null&&!Bukkit.unloadWorld(world,false)){plugin.getLogger().warning("Não foi possível descarregar o mundo "+worldName+" para reset.");plugin.getArenaManager().completeReset(arena);return;}Bukkit.getScheduler().runTaskAsynchronously(plugin,new Runnable(){@Override public void run(){try{File target=insideWorldContainer(worldName);delete(target);copy(template,target);Bukkit.getScheduler().runTask(plugin,new Runnable(){@Override public void run(){Bukkit.createWorld(new WorldCreator(worldName));plugin.getArenaManager().completeReset(arena);}});}catch(IOException ex){plugin.getLogger().warning("Falha no reset de "+arena.getId()+": "+ex.getMessage());Bukkit.getScheduler().runTask(plugin,new Runnable(){@Override public void run(){plugin.getArenaManager().completeReset(arena);}});}});}});
    }
    private File insideWorldContainer(String name) throws IOException {File base=Bukkit.getWorldContainer().getCanonicalFile();File target=new File(base,name).getCanonicalFile();if(!target.getPath().startsWith(base.getPath()+File.separator))throw new IOException("Nome de mundo inválido");return target;}
    private void delete(File file) throws IOException {if(!file.exists())return;File[] children=file.listFiles();if(children!=null)for(File child:children)delete(child);if(!file.delete())throw new IOException("Não foi possível remover "+file);}
    private void copy(File source,File target) throws IOException {if(source.isDirectory()){if(!target.mkdirs()&&!target.isDirectory())throw new IOException("Não foi possível criar "+target);File[] children=source.listFiles();if(children!=null)for(File child:children)copy(child,new File(target,child.getName()));return;}InputStream in=new FileInputStream(source);OutputStream out=new FileOutputStream(target);byte[] buffer=new byte[8192];int read;try{while((read=in.read(buffer))!=-1)out.write(buffer,0,read);}finally{try{in.close();}finally{out.close();}}}
}

package com.xilitra.monsterslayer.utils;

import com.xilitra.monsterslayer.game.GameInstance;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class MonsterUtils {

    public static Entity spawnMob(String type, Location worldLocation, int level, GameInstance instance) {
        if(MythicMobs.inst().getAPIHelper().getMythicMob(type) != null) {

            Entity entity =  createMythicMob(type, worldLocation, level);
            instance.mobspawned.add(entity);
            return entity;
        } else {
            Entity mob = worldLocation.getWorld().spawnEntity(worldLocation, EntityType.valueOf(type));
            ((LivingEntity)mob).setRemoveWhenFarAway(false);
            mob.setPersistent(true);
            instance.mobspawned.add(mob);
            return mob;
        }
    }

    public static Entity createMythicMob(String type, Location worldLocation, int level) {
        MobManager mm = MythicMobs.inst().getMobManager();
        Entity mob = mm.spawnMob(type, worldLocation, level).getEntity().getBukkitEntity();
        ((LivingEntity)mob).setRemoveWhenFarAway(false);
        mob.setPersistent(true);
        return mob;
    }
}

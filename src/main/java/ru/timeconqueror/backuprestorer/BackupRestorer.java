package ru.timeconqueror.backuprestorer;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = BackupRestorer.MODID,
        name = BackupRestorer.NAME,
        version = BackupRestorer.VERSION)
public class BackupRestorer {
    public static final String MODID = "regionrestorer";
    public static final String NAME = "Region Restorer";
    public static final String VERSION = "GRADLETOKEN_VERSION";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.Instance(value = BackupRestorer.MODID)
    public static BackupRestorer instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
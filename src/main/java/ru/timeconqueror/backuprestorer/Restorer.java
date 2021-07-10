package ru.timeconqueror.backuprestorer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.world.chunk.storage.RegionFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Restorer {
    public static final File TEMP = new File("br/temp");
    private Pos c1;
    private Pos c2;
    private File backup;
    private File worldDir;
    private String userName;

    public static void launch() {
        try {
            new Restorer().tryLaunch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryLaunch() throws IOException {
        File propFile = new File("restore.properties");

        FileInputStream stream = new FileInputStream(propFile);
        Properties props = new Properties();
        props.load(stream);
        stream.close();

        if (!props.getProperty("shouldBeRun").equals("false")) {
            BackupRestorer.LOGGER.info("Detected needing of restoring. Launching...");
            prepare(props);
            run();

            props.setProperty("shouldBeRun", "false");
            try (FileOutputStream out = new FileOutputStream(propFile)) {
                props.store(out, null);
            }
        }
    }

    private void prepare(Properties props) {
        Pos t1 = Pos.of(Integer.parseInt(props.getProperty("x1")), Integer.parseInt(props.getProperty("z1"))).toChunkPos();
        Pos t2 = Pos.of(Integer.parseInt(props.getProperty("x2")), Integer.parseInt(props.getProperty("z2"))).toChunkPos();

        c1 = Pos.of(Math.min(t1.x, t2.x), Math.min(t1.z, t2.z));
        c2 = Pos.of(Math.max(t1.x, t2.x), Math.max(t1.z, t2.z));

        backup = new File(props.getProperty("file"));
        BackupRestorer.LOGGER.info("Backup zip: {}", backup);
        worldDir = new File(props.getProperty("worldDir"));
        BackupRestorer.LOGGER.info("World directory: {}", worldDir);

        userName = props.getProperty("backupPlayer");
    }

    public void run() throws IOException {
        ZipFile zipBackup = new ZipFile(backup);

        restoreChunks(zipBackup);
        if (userName != null && !userName.isEmpty()) {
            BackupRestorer.LOGGER.info("Requested to restore player data! Starting...");
            restorePlayer(userName, zipBackup);
        }

        zipBackup.close();
        FileUtils.deleteDirectory(TEMP);

        BackupRestorer.LOGGER.info("Finished restoring!");
    }

    private void restorePlayer(String userName, ZipFile zipBackup) throws IOException {
        String uuid = getUuid(userName);
        if (uuid == null) {
            BackupRestorer.LOGGER.warn("Player with name '{}' not found in mojang API. Skipping restoring player data...", userName);
            return;
        }

        Enumeration<? extends ZipEntry> entries = zipBackup.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            /*TODO how to handle nicks, which can contain other nicks*/
            System.out.println(entry.getName());
            if (entry.getName().contains("playerdata/") &&
                    (entry.getName().contains(userName) || entry.getName().contains(uuid))) {
                InputStream stream = zipBackup.getInputStream(entry);

                String pathTo = entry.getName().substring(worldDir.getName().length() + 1 /*this is '/'*/);
                BackupRestorer.LOGGER.info("Copying {}...", pathTo);
                FileWriter fileTo = new FileWriter(pathTo);
                IOUtils.copy(stream, fileTo);

                fileTo.close();
                stream.close();
            }
        }
    }

    private void restoreChunks(ZipFile zipBackup) throws IOException {
        BackupRestorer.LOGGER.info("Changing {} chunks...", (c2.x - c1.x) * (c2.z - c1.z));

        for (int x = c1.x; x <= c2.x; x++) {
            for (int z = c1.z; z <= c2.z; z++) {
                BackupRestorer.LOGGER.info("Changing chunk {},{}", x, z);
                RegionFile from = RegionFiles.retrieveRegionFile(zipBackup, worldDir.getName(), x, z);
                RegionFile to = RegionFiles.retrieveRegionFile(worldDir, x, z);

                DataInputStream fromS = RegionFiles.getChunkInputStream(from, x, z);
                DataOutputStream toS = RegionFiles.getChunkOutputStream(to, x, z);

                IOUtils.copy(fromS, toS);

                fromS.close();
                toS.close();
                from.close();
                to.close();
            }
        }
    }

    @Nullable
    public String getUuid(String name) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            String json = IOUtils.toString(new URL(url));
            if (json.isEmpty()) return "invalid name";
            JsonObject obj = new Gson().fromJson(json, JsonObject.class);
            return obj.get("id").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

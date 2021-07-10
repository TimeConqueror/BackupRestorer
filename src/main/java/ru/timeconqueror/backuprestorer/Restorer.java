package ru.timeconqueror.backuprestorer;

import net.minecraft.world.chunk.storage.RegionFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Properties;
import java.util.zip.ZipFile;

public class Restorer {
    public static final File TEMP = new File("br/temp");
    private Pos c1;
    private Pos c2;
    private File backup;
    private File worldDir;

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

        if (!props.getProperty("go").equals("false")) {
            BackupRestorer.LOGGER.info("Detected needing of restoring. Launching...");
            prepare(props);
            run();

//            props.setProperty("go", "false");
//            try (FileOutputStream out = new FileOutputStream(propFile)) {
//                props.store(out, null);
//            }
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
    }

    public void run() throws IOException {
        ZipFile zipBackup = new ZipFile(backup);

        BackupRestorer.LOGGER.info("Changing {}x{} chunks...", (c2.x - c1.x), (c2.z - c1.z));

        for (int x = c1.x; x <= c2.x; x++) {
            for (int z = c1.z; z <= c2.z; z++) {
                BackupRestorer.LOGGER.info("Changing chunk {},{}", x, z);
                RegionFile from = RegionFiles.retrieveRegionFile(zipBackup, worldDir.getName(), x, z);
                RegionFile to = RegionFiles.retrieveRegionFile(worldDir, x, z);

                DataInputStream fromS = from.getChunkDataInputStream(x, z);
                DataOutputStream toS = to.getChunkDataOutputStream(x, z);

                System.out.println("IOUtils.copy(fromS, toS) = " + IOUtils.copy(fromS, toS));

                fromS.close();
                toS.close();
                from.close();
                to.close();
            }
        }

        zipBackup.close();
        FileUtils.deleteDirectory(TEMP);

        BackupRestorer.LOGGER.info("Finished restoring!");
    }
}

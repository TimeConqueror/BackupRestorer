package ru.timeconqueror.backuprestorer;

import net.minecraft.world.chunk.storage.RegionFile;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class RegionFiles {
    /**
     * Returns a region file for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static RegionFile retrieveRegionFile(ZipFile file, String worldName, int p_76550_1_, int p_76550_2_) throws IOException {
        String fileName = "r." + (p_76550_1_ >> 5) + "." + (p_76550_2_ >> 5) + ".mca";
        File tempRegionFile = new File(Restorer.TEMP, fileName);
        if(!tempRegionFile.exists()) {
            ZipEntry entry = file.getEntry(worldName + "/region/" + fileName);//TODO should handle cases when chunks are not present in backup

            InputStream zipIn = file.getInputStream(entry);
            FileUtils.copyInputStreamToFile(zipIn, tempRegionFile);
            zipIn.close();
        }

        return new RegionFile(tempRegionFile);
    }

    /**
     * Returns a region file for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static RegionFile retrieveRegionFile(File worldDir, int p_76550_1_, int p_76550_2_) {
        return new RegionFile(new File(worldDir, "region/r." + (p_76550_1_ >> 5) + "." + (p_76550_2_ >> 5) + ".mca"));
    }


    public static DataInputStream getChunkInputStream(RegionFile file, int p_76549_1_, int p_76549_2_) {
        return file.getChunkDataInputStream(p_76549_1_ & 31, p_76549_2_ & 31);
    }

    public static DataOutputStream getChunkOutputStream(RegionFile file, int p_76552_1_, int p_76552_2_) {
        return file.getChunkDataOutputStream(p_76552_1_ & 31, p_76552_2_ & 31);
    }
}
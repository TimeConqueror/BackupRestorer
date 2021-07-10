package ru.timeconqueror.backuprestorer;

public class Pos {
    public final int x;
    public final int z;

    public Pos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static Pos of(int x, int z) {
        return new Pos(x, z);
    }

    public Pos toChunkPos() {
        return new Pos(this.x >> 4, this.z >> 4);
    }
}

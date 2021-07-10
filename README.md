# Backup Restorer (1.7.10)

Restores specific chunks & player data from backups created by AromaBackup.
Server-side only.

For now it's in a very raw state, but will work normally if it has proper settings.

## Setting up:
To make the mod be activated properly, we need to create the file named `restore.properties`.

We need to specify there some stuff:
`x1=-1451` - the x coordinate of the first corner of the chunk area being restored.
`z1=1343`  - the z coordinate of the first corner of the chunk area being restored.
`x2=-1305` - the x coordinate of the second corner of the chunk area being restored.
`z2=1216`  - the z coordinate of the second corner of the chunk area being restored.
`shouldBeRun=true` - if set to true, the mod will be ran before the next server starting. After all its work, this variable will be set to false automatically.
`file=C\:/IJ Projects/MCModding/RegionRestorer 1.7.10/Backup-World-2021-7-10--14-29.zip` - file with absolute of relative path to the backup zip-file.
`backupPlayer=Az_Max` - player name, whose data should also be restored from backup. Can be empty.
`worldDir=saves/World` - relative path to the save directory.

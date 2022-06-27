package com.chooloo.www.chooloolib.dbase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ad")
public class Ad {



    @NonNull
    @PrimaryKey
    public String campaignUid;

    @ColumnInfo
    public boolean isDownloaded;

    @ColumnInfo
    public boolean isPlayed;

    @ColumnInfo
    public boolean isUpdated;

    public Ad(String campaignUid, boolean isDownloaded, boolean isPlayed, boolean isUpdated, String path) {
        this.campaignUid = campaignUid;
        this.isDownloaded = isDownloaded;
        this.isPlayed = isPlayed;
        this.isUpdated = isUpdated;
        this.path = path;
    }

    @ColumnInfo
    public String path;

}

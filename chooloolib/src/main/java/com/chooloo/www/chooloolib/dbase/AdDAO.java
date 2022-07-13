package com.chooloo.www.chooloolib.dbase;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AdDAO {

    @Query("SELECT * from ad")
    List<Ad> getAll();

    @Query("SELECT * from ad where isPlayed=:status")
    Ad[] getAds(boolean status);

    @Query("SELECT EXISTS(SELECT * FROM ad WHERE campaignUid = :id)")
    boolean checkExists(String id);

    @Query("SELECT * from ad where isPlayed=0 limit 1")
    List<Ad> getAudio();

    @Query("SELECT * FROM ad where path=:path")
    Ad[] getAdByPath(String path);

    @Query("UPDATE ad set isDownloaded=:status where path=:path")
    void updateDownloaded(boolean status,String path);

    @Query("UPDATE ad set isPlayed=:status where path=:path")
    void updatePlayed(boolean status,String path);

    @Query("UPDATE ad set isUpdated=:status where path=:path")
    void updateUpdated(boolean status,String path);




    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Ad ad);

    @Delete
    void delete(Ad ad);



}

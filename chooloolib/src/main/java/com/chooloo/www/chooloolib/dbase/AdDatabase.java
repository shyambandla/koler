package com.chooloo.www.chooloolib.dbase;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Ad.class}, version = 2)
public abstract class AdDatabase extends RoomDatabase {

    public abstract AdDAO adDAO();

}

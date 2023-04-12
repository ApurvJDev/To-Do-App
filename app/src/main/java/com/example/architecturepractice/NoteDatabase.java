package com.example.architecturepractice;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    //we use this because we want to turn this class into a singleton
    private static NoteDatabase instance;

    public abstract NoteDAO noteDAO();
    //synchronized means ony one thread at a time
    //can access this method
    //this way you cannot accidentally create 2 instances of the database

    public static synchronized NoteDatabase getInstance(Context context){
        // in here we will create an only single database instance and then we can call this method from outside
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),NoteDatabase.class,"note_database")
                    .fallbackToDestructiveMigration().build();
            //when we increment the version number of the database we have to tell room
            //how to migrate to the new schema if we don't do this and increase the version number
            //app will crash because it will get an illegal state exception
            //with fallBackTODestructiveMigration() we can avoid this because it wil simply
            //delete the database and it's tables and create it from scratch
        }
        return instance;
    }

//we do this because we want some data beforehand in the recycler view
//    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//        }
//    };

//    private static PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            noteDAO.insert(new Note("Title 1", "Description 1", 1));
//            noteDAO.insert(new Note("Title 2", "Description 2", 2));
//            noteDAO.insert(new Note("Title 3", "Description 3", 3));
//            return null;
//        }
 //   }
}

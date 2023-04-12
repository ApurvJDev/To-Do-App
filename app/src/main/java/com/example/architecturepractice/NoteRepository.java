
    // repository provides an abstraction layer between different data sources and the rest of the app
    // view model doesn't have to care about where the data comes from or how it is fetched
    //it just calls methods on the repository directory
package com.example.architecturepractice;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NoteRepository {
    private final LiveData<List<Note>> allNotes;
    private final DAO_BackgroundAccess  dao_backgroundAccess;

    public NoteRepository(Application application)
    {
        NoteDatabase database= NoteDatabase.getInstance(application);
        NoteDAO noteDao = database.noteDAO();
        allNotes= noteDao.getAllNotes();
        dao_backgroundAccess = new DAO_BackgroundAccess(noteDao);
    }
    //for the below database operations we have to execute the code on the background thread
    //ourselves unlike the getAllNotes() method
    //because room doesn't allow database operations on the main thread since this could freeze our app
    //in this case to do this we will use async task
    //below methods are the API that the repository exposes to the view model

    public void insert(Note note)
    {
        dao_backgroundAccess.insertNote(note);
    }

    public void updateNote(Note note) { dao_backgroundAccess.updateNotes(note); }

    public void delete(Note note) { dao_backgroundAccess.deleteNotes(note); }

    public void deleteAllNotes() { dao_backgroundAccess.deleteAllNote(); }

    public LiveData<List<Note>> getAllNotes() {
        dao_backgroundAccess.getAllNotes();
        return allNotes;
    }
    public static class DAO_BackgroundAccess {
        private final NoteDAO noteDAO;
        LiveData<List<Note>> allNotes;
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        DAO_BackgroundAccess(NoteDAO noteDAO) {
            this.noteDAO = noteDAO;
        }

        public LiveData<List<Note>> getAllNotes()
        {
            executorService.execute(() -> allNotes = noteDAO.getAllNotes());
            return  allNotes;
        }
        public void insertNote(Note note) {
            executorService.execute(() -> noteDAO.insert(note));
        }
        public void deleteNotes(Note note)
        {
            executorService.execute(() -> noteDAO.delete(note));
        }
        public void updateNotes(Note note)
        {
            executorService.execute(() -> noteDAO.update(note));
        }
        public void deleteAllNote()
        {
            executorService.execute(noteDAO::deleteAllNotes);
        }
    }
}
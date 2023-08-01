package com.example.mvvmnote.room

import androidx.lifecycle.LiveData
import com.example.mvvmnote.room.NoteEntity

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<NoteEntity>> = noteDao.getAllData()

    suspend fun insert(note: NoteEntity){
        noteDao.insert(note)
    }
    suspend fun delete(note: NoteEntity){
        noteDao.delete(note)
    }
    suspend fun update(note: NoteEntity){
        noteDao.update(note)
    }

}
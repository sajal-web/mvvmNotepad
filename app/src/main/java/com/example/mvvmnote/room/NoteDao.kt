package com.example.mvvmnote.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note : NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)



    @Query("Select * from notesTable order by id ASC")
    fun getAllData(): LiveData<List<NoteEntity>>
}

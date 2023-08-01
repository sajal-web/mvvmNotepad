package com.example.mvvmnote

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.NoteRvAdapter
import com.example.mvvmnote.databinding.ActivityMainBinding
import com.example.mvvmnote.mvvm.NoteViewModel
import com.example.mvvmnote.room.NoteEntity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NoteRvAdapter.DeleteNoteAdapter,
    NoteRvAdapter.ItemClickListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NoteViewModel
    private var isUpdate = false
    private var noteToUpdate: NoteEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.notesRV.layoutManager = LinearLayoutManager(this)
        val adapter = NoteRvAdapter(this, this)
        binding.notesRV.adapter = adapter
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)
        viewModel.allNotes.observe(this, Observer {
            it?.let {
                adapter.updateList(it)
            }

        })
        binding.idFAB.setOnClickListener {
            showNoteDialog()
        }

        val swipeToDeleteCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = adapter.getNoteAtPosition(position)

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        (viewModel.deleteNote(note))
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.notesRV)

    }

    override fun onItemClick(noteEntity: NoteEntity) {
        viewModel.deleteNote(noteEntity)
    }


    override fun onItemClick(position: Int, note: NoteEntity) {
        showUpdateDialog(note)
    }

    // call this methode to open the dialog for updating an existing note
    private fun showUpdateDialog(note: NoteEntity) {
        isUpdate = true
        noteToUpdate = note
        showNoteDialog()
    }

    // call this method to reset the dialog to add a new note
    private fun showAddNoteDialog() {
        isUpdate = false
        noteToUpdate = null
        showNoteDialog()

    }

    private fun showNoteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(if (isUpdate) "Edit Note" else "Add Note")
        // if it's an update then pre fill the dialog with the existing note
        val dialog = dialogBuilder.create()

        val noteTitleEditText = dialogView.findViewById<EditText>(R.id.noteTitleEditText)
        val noteContentEditText = dialogView.findViewById<EditText>(R.id.noteContentEditText)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)

        if (isUpdate && noteToUpdate != null) {
            noteTitleEditText.setText(noteToUpdate!!.noteTitle)
            noteContentEditText.setText(noteToUpdate!!.noteDescription)
        }

        submitButton.setOnClickListener {
            val noteTitle = noteTitleEditText.text.toString()
            val noteDescription = noteContentEditText.text.toString()

            if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                dialog.dismiss()

                val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                val currentDateAndTime: String = sdf.format(Date())

                if (isUpdate && noteToUpdate != null) {
                    // update the existing note
                    noteToUpdate!!.noteTitle = noteTitle
                    noteToUpdate!!.noteDescription = noteDescription
                    noteToUpdate!!.timeStamp = currentDateAndTime
                    viewModel.updateNote(noteToUpdate!!)
                    Toast.makeText(this, "Note updated", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.addNote(NoteEntity(noteTitle, noteDescription, currentDateAndTime))
                    Toast.makeText(this, "$noteTitle added", Toast.LENGTH_LONG).show()
                }

            }
        }
        dialog.show()
    }
}
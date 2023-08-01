package com.example

import com.example.mvvmnote.room.NoteEntity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnote.databinding.NoteRvItemBinding

class NoteRvAdapter(
    private val listener: DeleteNoteAdapter,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<NoteRvAdapter.NoteViewHolder>() {

    val notes = ArrayList<NoteEntity>()


    inner class NoteViewHolder(private val binding: NoteRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteEntity) {
            binding.note = note
            binding.executePendingBindings()
            binding.idIVDelete.setOnClickListener {
                listener.onItemClick(notes[adapterPosition])
            }
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition, note)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NoteRvItemBinding.inflate(inflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.bind(currentNote)

    }

    fun updateList(newlist: List<NoteEntity>) {
        notes.clear()
        notes.addAll(newlist)
        notifyDataSetChanged()
    }

    interface DeleteNoteAdapter {
        fun onItemClick(noteEntity: NoteEntity)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int, note: NoteEntity)
    }

    fun getNoteAtPosition(position: Int): NoteEntity {
        return notes[position]
    }
}
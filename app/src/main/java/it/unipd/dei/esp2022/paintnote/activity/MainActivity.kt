package it.unipd.dei.esp2022.paintnote.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2022.paintnote.DatabaseHandler
import it.unipd.dei.esp2022.paintnote.ItemAdapter
import it.unipd.dei.esp2022.paintnote.Note
import it.unipd.dei.esp2022.paintnote.R

class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    /**
     * Function that shows the list of inserted notes,
     * and manages the recycler view
     */
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val tvNoNotes: TextView = findViewById(R.id.tvNoNotes)

        if(getNotesList().size > 0) {
            recyclerView.visibility = View.VISIBLE
            tvNoNotes.visibility = View.INVISIBLE

            //set the LayoutManager that RecyclerView use
            recyclerView.layoutManager = LinearLayoutManager(this)
            //item adapter class is initialized and list of notes is passed as parameter
            val itemAdapter = ItemAdapter(this, getNotesList())
            //adapter instance is set to recyclerview to inflate items
            recyclerView.adapter = itemAdapter
        } else {
            recyclerView.visibility = View.GONE
            tvNoNotes.visibility = View.VISIBLE
        }
    }

    /**
     * Function that returns the notes list from the database
     */
    private fun getNotesList(): ArrayList<Note> {
        val dbHandler = DatabaseHandler(this)
        return dbHandler.getAllNotes()
    }

    /**
     * Function that deletes a note from the database,
     * and shows an alert dialog to confirm or cancel the operation
     */
    fun deleteNote(note: Note) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure you wants to delete \"${note.title}\" note?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //yes button
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            val dbHandler = DatabaseHandler(this)
            val status = dbHandler.deleteNote(note)
            if(status > -1) {
                Toast.makeText(applicationContext, "Note deleted", Toast.LENGTH_SHORT).show()
                setupRecyclerView()
            }
            dialogInterface.dismiss()
        }

        //no button
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        //create alert dialog
        val alert: AlertDialog = builder.create()
        alert.setCancelable(true)    //cancel if click on remaining screen area
        alert.show()
    }

}
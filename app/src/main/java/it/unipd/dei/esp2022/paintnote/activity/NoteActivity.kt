package it.unipd.dei.esp2022.paintnote.activity

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.unipd.dei.esp2022.paintnote.PaintView.Companion.bitmap
import it.unipd.dei.esp2022.paintnote.PaintView.Companion.currentBrush
import it.unipd.dei.esp2022.paintnote.PaintView.Companion.isErase
import it.unipd.dei.esp2022.paintnote.PaintView.Companion.mCanvas
import it.unipd.dei.esp2022.paintnote.R
import it.unipd.dei.esp2022.paintnote.DatabaseHandler
import it.unipd.dei.esp2022.paintnote.Note

class NoteActivity: AppCompatActivity() {

    companion object {
        var path = Path()
        var paintBrush = Paint()
    }

    //private var currentNoteID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val noteTitle: EditText = findViewById(R.id.edit)
        val paint: RelativeLayout = findViewById(R.id.paint)
        var backgroundColor: Int = Color.WHITE  //default
        var currentNoteID: Int? = null

        val bundle: Bundle? = intent.extras
        if(bundle != null) {   //load existing note
            currentNoteID = bundle.getInt("id")
            val dbHandler = DatabaseHandler(this)
            val currentNote: Note? = dbHandler.getNoteByID(currentNoteID)
            noteTitle.setText(currentNote?.title)
            paint.background = BitmapDrawable(this.resources, currentNote?.bitmap)
            bitmap = currentNote?.bitmap
            if(currentNote?.isBackgroundWhite!!) backgroundColor = Color.WHITE
            else backgroundColor = Color.BLACK
        }
        else {   //new note
            bitmap = null
            bitmap?.eraseColor(Color.WHITE)
        }
        setBrushColor(backgroundColor)

        val saveBtn: ImageButton = findViewById(R.id.saveBtn)
        val whiteBackBtn: ImageButton = findViewById(R.id.whiteBackBtn)
        val blackBackBtn: ImageButton = findViewById(R.id.blackBackBtn)
        val eraserBtn: ImageButton = findViewById(R.id.eraserBtn)

        var fabVisibility: Int = View.INVISIBLE
        val brushFab: FloatingActionButton = findViewById(R.id.brush_fab)
        val defaultFab: FloatingActionButton = findViewById(R.id.default_fab)
        val redFab: FloatingActionButton = findViewById(R.id.red_fab)
        val greenFab: FloatingActionButton = findViewById(R.id.green_fab)
        val blueFab: FloatingActionButton = findViewById(R.id.blue_fab)

        if(backgroundColor == Color.WHITE) {
            defaultFab.backgroundTintList = this.applicationContext.getColorStateList(R.color.black)
            brushFab.setColorFilter(paintBrush.color)
        } else {
            defaultFab.backgroundTintList = this.applicationContext.getColorStateList(R.color.white)
            brushFab.setColorFilter(paintBrush.color)
        }

        //save
        saveBtn.setOnClickListener {
            if(currentNoteID != null) {
                if(backgroundColor == Color.WHITE) updateNote(Note(currentNoteID, noteTitle.text.toString(), bitmap!!, true))
                else updateNote(Note(currentNoteID, noteTitle.text.toString(), bitmap!!, false))
            }
            else {
                val newNote: Note
                if(backgroundColor == Color.WHITE) newNote = Note(0, noteTitle.text.toString(), bitmap!!, true)
                else newNote = Note(0, noteTitle.text.toString(), bitmap!!, false)
                addNote(newNote)
            }
        }


        whiteBackBtn.setOnClickListener {
            Toast.makeText(this, "White background", Toast.LENGTH_SHORT).show()
            backgroundColor = Color.WHITE
            //paint.setBackgroundColor(backgroundColor)
            clear()
            bitmap!!.eraseColor(backgroundColor)
            defaultFab.backgroundTintList = this.applicationContext.getColorStateList(R.color.black)
            setErase(false)
            paintBrush.color = Color.BLACK
            currentColor(paintBrush.color)
            brushFab.setColorFilter(paintBrush.color)
            eraserBtn.setBackgroundResource(R.drawable.erase_background)
        }

        blackBackBtn.setOnClickListener {
            Toast.makeText(this, "Black background", Toast.LENGTH_SHORT).show()
            backgroundColor = Color.BLACK
            //paint.setBackgroundColor(backgroundColor)
            clear()
            bitmap!!.eraseColor(backgroundColor)
            defaultFab.backgroundTintList = this.applicationContext.getColorStateList(R.color.white)
            setErase(false)
            paintBrush.color = Color.WHITE
            currentColor(paintBrush.color)
            brushFab.setColorFilter(paintBrush.color)
            eraserBtn.setBackgroundResource(R.drawable.erase_background)
        }

        eraserBtn.setOnClickListener {
            setErase(true)
            paintBrush.color = backgroundColor
            currentColor(paintBrush.color)
            brushFab.setColorFilter(paintBrush.color)
            eraserBtn.setBackgroundResource(R.drawable.erase_background_selected)
        }


        brushFab.setOnClickListener {
            val isClickable: Boolean

            if(fabVisibility == View.INVISIBLE) {
                fabVisibility = View.VISIBLE
                isClickable = true
            } else {
                fabVisibility = View.INVISIBLE
                isClickable = false
            }
            //set fab visibility
            defaultFab.visibility = fabVisibility
            redFab.visibility = fabVisibility
            greenFab.visibility = fabVisibility
            blueFab.visibility = fabVisibility

            //set fab clickable
            defaultFab.isClickable = isClickable
            redFab.isClickable = isClickable
            greenFab.isClickable = isClickable
            blueFab.isClickable = isClickable
        }

        defaultFab.setOnClickListener {
            setErase(false)
            setBrushColor(backgroundColor)
            brushFab.setColorFilter(paintBrush.color)
            brushFab.callOnClick()  //close fab
            eraserBtn.setBackgroundResource(R.drawable.erase_background)
        }
        redFab.setOnClickListener {
            setErase(false)
            paintBrush.color = Color.RED
            brushFab.setColorFilter(paintBrush.color)
            currentColor(paintBrush.color)
            brushFab.callOnClick()  //close fab
            eraserBtn.setBackgroundResource(R.drawable.erase_background)
        }
        greenFab.setOnClickListener {
            setErase(false)
            paintBrush.color = Color.GREEN
            brushFab.setColorFilter(paintBrush.color)
            currentColor(paintBrush.color)
            brushFab.callOnClick()  //close fab
            eraserBtn.setBackgroundResource(R.drawable.erase_background)
        }
        blueFab.setOnClickListener {
            setErase(false)
            paintBrush.color = Color.BLUE
            brushFab.setColorFilter(paintBrush.color)
            currentColor(paintBrush.color)
            brushFab.callOnClick()  //close fab
            eraserBtn.setBackgroundResource(R.drawable.erase_background)
        }

    }

    /**
     * Function that calls the addNote() function of the DatabaseHandler,
     * and checks if the title is empty (can not)
     */
    private fun addNote(note: Note) {
        val dbHandler = DatabaseHandler(this)
        if(note.title.isNotEmpty()) {
            val status = dbHandler.addNote(note)
            if(status > -1) Toast.makeText(applicationContext, "Note created", Toast.LENGTH_SHORT).show()
            finish()    //finish activity
        } else Toast.makeText(applicationContext, "Title cannot be blank", Toast.LENGTH_SHORT).show()
    }

    /**
     * Function that calls the updateNote() function of the DatabaseHandler,
     * and checks if the title is empty (can not)
     */
    private fun updateNote(note: Note) {
        val dbHandler = DatabaseHandler(this)
        if(note.title.isNotEmpty()) {
            val status = dbHandler.updateNote(note)
            if(status > -1) Toast.makeText(applicationContext, "Note updated", Toast.LENGTH_SHORT).show()
            finish()    //finish activity
        } else Toast.makeText(applicationContext, "Title cannot be blank", Toast.LENGTH_SHORT).show()
    }

    /**
     * Function that set the erase tool,
     * isErase=true if eraser is the current tool (bigger strokeWidth),
     * isErase=false if brush is the current tool
     */
    private fun setErase(erase: Boolean) {
        isErase = erase
        if(erase) paintBrush.strokeWidth = 32f
        else paintBrush.strokeWidth = 8f
    }

    /**
     * Function that set the current brush color
     */
    private fun currentColor(color: Int) {
        currentBrush = color
        path = Path()
    }

    /**
     * Function that set the current brush color
     * depending on background color of the paint
     */
    private fun setBrushColor(back: Int) {
        if(back == Color.WHITE) paintBrush.color = Color.BLACK
        else paintBrush.color = Color.WHITE
        currentColor(paintBrush.color)
    }

    /**
     * Function that clear the paint
     */
    private fun clear() {
        path.reset()
        mCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

}
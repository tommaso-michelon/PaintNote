package it.unipd.dei.esp2022.paintnote

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "NoteDatabase"
        const val TABLE_CONTACTS = "NoteTable"

        const val KEY_ID = "_id"
        const val KEY_TITLE = "title"
        const val KEY_PAINT = "paint"
        const val KEY_BACKGROUND = "isBackgroundWhite"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_PAINT + " BLOB, " + KEY_BACKGROUND + " INTEGER" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    /**
     *  Function that returns a list with all the saved notes in the database,
     *  it has to convert the ByteArray (that represents the paint) into a bitmap
     */
    fun getAllNotes(): ArrayList<Note> {
        val noteList: ArrayList<Note> = ArrayList()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var note: Note
        var id: Int
        var title: String
        var bitmap: Bitmap
        var byteArray: ByteArray

        if(cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE))
                byteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(KEY_PAINT))
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                if(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BACKGROUND)) == 1) note = Note(id, title, bitmap, true)
                else note = Note(id, title, bitmap, false)
                noteList.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return noteList
    }

    /**
     *  Function that returns the note (saved in the database) with the id passed as parameter,
     *  it has to convert the ByteArray (that represents the paint) into a bitmap
     */
    fun getNoteByID(idNote: Int): Note? {
        var note: Note? = null
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS WHERE $KEY_ID == $idNote"
        val db = this.readableDatabase

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return null
        }

        val id: Int
        val title: String
        val bitmap: Bitmap
        val byteArray: ByteArray

        if(cursor.moveToFirst() && cursor.count == 1) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
            title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE))
            byteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(KEY_PAINT))
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            if(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BACKGROUND)) == 1) note = Note(id, title, bitmap, true)
            else note = Note(id, title, bitmap, false)
        }
        cursor.close()
        return note
    }

    /**
     * Function to adds a note in the database
     */
    fun addNote(note: Note): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, note.title)
        val array: ByteArray = imageToBitmap(note.bitmap!!)
        contentValues.put(KEY_PAINT, array)
        if(note.isBackgroundWhite!!) contentValues.put(KEY_BACKGROUND, 1)
        else contentValues.put(KEY_BACKGROUND, 0)
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close()
        return success
    }

    /**
     * Function to updates a note in the database
     */
    fun updateNote(note: Note): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, note.title)
        contentValues.put(KEY_PAINT, imageToBitmap(note.bitmap!!))
        if(note.isBackgroundWhite!!) contentValues.put(KEY_BACKGROUND, 1)
        else contentValues.put(KEY_BACKGROUND, 0)
        val success = db.update(TABLE_CONTACTS, contentValues, KEY_ID + "=" + note.id, null)
        db.close()
        return success
    }

    /**
     * Function to deletes a note from the database
     */
    fun deleteNote(note: Note): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, note.id)
        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + note.id, null)
        db.close()
        return success
    }

    /**
     * Function that converts a bitmap in a ByteArray,
     * using a compress function,
     * to insert a paint in the database
     */
    private fun imageToBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

}
package kite.com.notes.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kite.com.notes.model.Note
import java.util.*


class NotesDataBaseHandler(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_TITULO + " TEXT," +
                KEY_CONTENT + " TEXT," +
                KEY_DATE + " LONG" + ")"

        db?.execSQL(CREATE_NOTE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * CRUD
     */

    fun createNote(note: Note) {

        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put(KEY_TITULO, note.tituloNota)
        values.put(KEY_CONTENT, note.contentNota)
        values.put(KEY_DATE, System.currentTimeMillis())

        db.insert(TABLE_NAME, null, values)
        db.close()

    }

    fun readNote(id: Int): Note {
        val db: SQLiteDatabase = writableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME, arrayOf(
                KEY_ID,
                KEY_TITULO, KEY_CONTENT, KEY_DATE
            ), "$KEY_ID=?", arrayOf(id.toString()),
            null, null, null, null
        )

        cursor.moveToFirst()
        cursor.use { cursor1 ->
            val note = Note()
            note.tituloNota = cursor1.getString(cursor1.getColumnIndex(KEY_TITULO))
            note.contentNota = cursor1.getString(cursor1.getColumnIndex(KEY_CONTENT))
            note.dateNota = cursor1.getLong(cursor1.getColumnIndex(KEY_DATE))
            note.idNota = cursor.getInt(cursor1.getColumnIndex(KEY_ID))


            return note
        }


    }

    fun readNotes(): ArrayList<Note> {

        val db: SQLiteDatabase = readableDatabase
        val list: ArrayList<Note> = ArrayList()

        //Select all notes from table
        val selectAll = "SELECT * FROM $TABLE_NAME"

        val cursor: Cursor = db.rawQuery(selectAll, null)

        //loop through our notes
        if (cursor.moveToFirst()) {
            cursor.use { cursor1 ->
                do {
                    val note = Note()
                    note.idNota = cursor1.getInt(cursor1.getColumnIndex(KEY_ID))
                    note.tituloNota = cursor1.getString(cursor1.getColumnIndex(KEY_TITULO))
                    note.contentNota = cursor1.getString(cursor1.getColumnIndex(KEY_CONTENT))
                    note.dateNota = cursor1.getLong(cursor1.getColumnIndex(KEY_DATE))

                    list.add(note)
                } while (cursor1.moveToNext())
            }

        }
        return list
    }

    fun updateNota(note: Note, id: Int): Int {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put(KEY_TITULO, note.tituloNota)
        values.put(KEY_CONTENT, note.contentNota)
        values.put(KEY_DATE, System.currentTimeMillis())

        //Update a row
        return db.update(TABLE_NAME, values, "$KEY_ID=?", arrayOf(id.toString()))
    }

    fun deleteNota(id: Int) {
        val db: SQLiteDatabase = writableDatabase
        db.delete(TABLE_NAME, "$KEY_ID=?", arrayOf(id.toString()))
        db.close()
    }

    fun getNotasCount(): Int {
        val db: SQLiteDatabase = readableDatabase
        val countQuey = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(countQuey, null)
        cursor.use { cursor1 ->
            return cursor1.count
        }


    }


}
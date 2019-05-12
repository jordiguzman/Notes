package kite.com.notes.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kite.com.notes.model.Tags

class TagsDataBaseHandler(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME_TAGS, null,
    DATABASE_VERSION_TAGS
) {

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TAG_TABLE = "CREATE TABLE " + TABLE_NAME_TAGS + "(" + KEY_ID_TAGS + " INTEGER PRIMARY KEY, " +
                KEY_TITULO_TAGS + " TEXT" + ")"
        db?.execSQL(CREATE_TAG_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_TAGS")
        onCreate(db)
    }

    fun createTag(tags: Tags) {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put(KEY_TITULO_TAGS, tags.type)

        db.insert(TABLE_NAME_TAGS, null, values)
        db.close()
    }

    fun readTag(id: Int): Tags {
        val db: SQLiteDatabase = writableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME_TAGS, arrayOf(
                KEY_ID_TAGS,
                KEY_TITULO_TAGS
            ), "$KEY_ID_TAGS=?", arrayOf(id.toString()),
            null, null, null, null
        )

        cursor.moveToFirst()
        cursor.use { cursor1 ->
            val tags = Tags()
            tags.type = cursor1.getString(cursor1.getColumnIndex(KEY_TITULO_TAGS))
            tags.idTag = cursor1.getInt(cursor1.getColumnIndex(KEY_ID_TAGS))
            return tags
        }
    }

    fun readTags(): ArrayList<Tags> {

        val db: SQLiteDatabase = readableDatabase
        val list: ArrayList<Tags> = ArrayList()
        val selectAll = "SELECT * FROM $TABLE_NAME_TAGS"
        val cursor: Cursor = db.rawQuery(selectAll, null)

        if (cursor.moveToFirst()) {
            cursor.use { cursor1 ->
                do {
                    val tag = Tags()
                    tag.idTag = cursor1.getInt(cursor1.getColumnIndex(KEY_ID_TAGS))
                    tag.type = cursor1.getString(cursor1.getColumnIndex(KEY_TITULO_TAGS))
                    list.add(tag)
                } while (cursor1.moveToNext())
            }
        }
        return list
    }

}



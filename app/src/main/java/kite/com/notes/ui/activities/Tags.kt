package kite.com.notes.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kite.com.notes.R
import kite.com.notes.data.TagsDataBaseHandler
import kite.com.notes.model.Tags
import kotlinx.android.synthetic.main.activity_tags.*

class Tags : AppCompatActivity() {

    private var dbHandlerTags: TagsDataBaseHandler? = null
    private var listTags: ArrayList<Tags>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)
        supportActionBar!!.title = "Tags"

        dbHandlerTags = TagsDataBaseHandler(this)
        readTags()
    }


    private fun readTags() {
        listTags = ArrayList()
        listTags = dbHandlerTags!!.readTags()
        Log.d("TAGS", listTags!![1].type)
        tv_tags.text = listTags!![0].type.plus(listTags!![1].type)

    }
}

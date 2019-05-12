package kite.com.notes


import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import kite.com.notes.data.NotesDataBaseHandler
import kite.com.notes.data.TagsDataBaseHandler
import kite.com.notes.data.adapter.NoteListAdapter
import kite.com.notes.model.Note
import kite.com.notes.ui.activities.CrearNota
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.design.longSnackbar
import kite.com.notes.model.Tags
import org.jetbrains.anko.*
import java.util.*
import kotlin.collections.ArrayList
import java.lang.Class as Class1


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var dbHandler: NotesDataBaseHandler? = null
    private var dbHandlerTags: TagsDataBaseHandler? = null
    private var adapter: NoteListAdapter? = null
    private var noteList: ArrayList<Note>? = null
    private var noteListItems: ArrayList<Note>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var prefsTags: SharedPreferences? = null
    private var prefLanguage: SharedPreferences? = null
    private val PREF_NAME = "prefTags"
    private val PREF_LANGUAGE = "prefLang"
    private var languageToLoad: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        toolbar.title = getString(R.string.app_name)

        /**
         * A QUE NO VA
         */


        dbHandler = NotesDataBaseHandler(this)
        dbHandlerTags = TagsDataBaseHandler(this)
        readNotes()
        if (noteList!!.isEmpty()){
            drawer_layout.longSnackbar("No hay notas")
        }

        val dataShared: SharedPreferences = getSharedPreferences(PREF_NAME, 0)
        val isTags: Boolean = dataShared.getBoolean("tags", false)
        if (!isTags) {
            createInitTags()
        }



        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            createNewNote()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


    }


    private fun createInitTags() {
        prefsTags = getSharedPreferences(PREF_NAME, 0)
        val editor: SharedPreferences.Editor = prefsTags!!.edit()
        editor.putBoolean("tags", true)
        editor.apply()
        val tags = Tags()
        tags.type = "Tabajo"
        dbHandlerTags!!.createTag(tags)
        tags.type = "Hogar"
        dbHandlerTags!!.createTag(tags)

    }

    private fun readNotes() {

        noteList = ArrayList()
        noteListItems = ArrayList()
        layoutManager = LinearLayoutManager(this)
        adapter = NoteListAdapter(noteListItems!!, this)

        //Setup list

        rv_main.layoutManager = layoutManager
        rv_main.adapter = adapter

        //Load notes

        noteList = dbHandler!!.readNotes()
        noteList!!.reverse()
        for (c in noteList!!.iterator()){
            val note = Note()
            note.tituloNota = c.tituloNota
            note.contentNota = c.contentNota
            note.dateNota = c.dateNota
            note.idNota = c.idNota
            noteListItems!!.add(note)

        }

        adapter!!.notifyDataSetChanged()

    }

    private fun createNewNote() {
        gotoActivity(CrearNota::class.java)

    }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_language -> {
                alertLanguage()
            }
            R.id.nav_info -> {

            }
            R.id.nav_tags -> {

            }
            R.id.nav_colors -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun gotoActivity(toClass: java.lang.Class<*>) {
        val intent = Intent(this, toClass)
        startActivity(intent)

    }

    private fun alertLanguage() {
        val idiomas = listOf("Español", "English")
        selector("Cambía el idioma", idiomas) { _, i ->
            if (i == 0) {
                changeSpanish()
            } else if (i == 1) {
                changeEnglish()
            }
        }

    }

    private fun changeSpanish() {
        languageToLoad = "es"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        gotoActivity(MainActivity::class.java)
        setSharedPreferences()
    }

    private fun setSharedPreferences() {
        prefLanguage = getSharedPreferences(PREF_LANGUAGE, 0)
        val editor: SharedPreferences.Editor = prefLanguage!!.edit()
        editor.putString("lang", languageToLoad)
        editor.apply()
    }

    private fun changeEnglish() {
        languageToLoad = "en"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        gotoActivity(MainActivity::class.java)
        setSharedPreferences()
    }


}

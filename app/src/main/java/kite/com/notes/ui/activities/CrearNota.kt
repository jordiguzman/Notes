package kite.com.notes.ui.activities

import android.app.ActionBar
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.text.Layout
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import kite.com.notes.MainActivity
import org.jetbrains.anko.design.longSnackbar
import kite.com.notes.R
import kite.com.notes.data.NotesDataBaseHandler
import kite.com.notes.data.TagsDataBaseHandler
import kite.com.notes.model.Note
import kite.com.notes.model.Tags
import kotlinx.android.synthetic.main.activity_crear_nota.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.image
import java.text.DateFormat
import android.view.ViewGroup.LayoutParams as LayoutParams1

class CrearNota : AppCompatActivity() {

    private var dbHandler: NotesDataBaseHandler? = null

    private var idNota: Int? = null
    private var tituloNota: String? = null
    private var bundle: Bundle? = null
    private var isEditable: Boolean = false
    private var receivedIntent: Intent? = null
    private var receivedAction: String? = null
    private var receivedType: String? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_nota)

        supportActionBar!!.title = getString(R.string.app_name)


        dbHandler = NotesDataBaseHandler(this)

        bundle = intent.extras
        if (bundle != null){
            val fromCreate: Boolean = bundle!!.getBoolean("fromCreate")
            if (!fromCreate) {
                getShareTextFromOtherApps()
            } else {
                readNotes()
            }
        }else {
            fab_crear_nota.setOnClickListener {
                toSaveNote()
            }
        }

        iv_tag.setOnClickListener {
            startActivity(Intent(this, kite.com.notes.ui.activities.Tags::class.java))
        }


    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun readNotes() {
        if (intent.extras != null) {
            bundle = intent.extras
            idNota = bundle!!.getInt("id")
            tituloNota = bundle!!.getString("titulo")
            supportActionBar!!.title = tituloNota
            changeUI()
            readNote(idNota!!)
            fab_crear_nota.setOnClickListener {
                if (!isEditable) {
                    et_titulo.isEnabled = true
                    et_content_nota.isEnabled = true
                    fab_crear_nota.image = getDrawable(R.drawable.ic_save_black_24dp)
                    isEditable = true
                } else {
                    toSaveNote()
                }
            }
        } else {
            fab_crear_nota.setOnClickListener {
                toSaveNote()
            }
        }

    }

    private fun getShareTextFromOtherApps() {
        receivedIntent = intent
        if (receivedAction != (Intent.ACTION_SEND)) {
            receivedAction = receivedIntent!!.action!!
            receivedType = receivedIntent!!.type

            val receivedText: String = receivedIntent!!.getStringExtra(Intent.EXTRA_TEXT)
            fab_crear_nota.setOnClickListener {
                toSaveNote()
            }
            et_content_nota.setText(receivedText)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun changeUI() {
        fab_crear_nota.image = getDrawable(R.drawable.ic_edit_black_24dp)
        et_titulo.isEnabled = false
        et_content_nota.isEnabled = false

    }

    private fun toSaveNote() {
        val note = Note()
        if (et_titulo.text.isEmpty() || et_content_nota.text.isEmpty()) {
            constrain_crear_nota.longSnackbar(getString(R.string.text_save_note))
        } else {
            note.tituloNota = et_titulo.text.toString()
            note.contentNota = et_content_nota.text.toString()
            if (!isEditable) {
                saveToBD(note)
            } else {
                updateToBd(note)
            }
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun updateToBd(note: Note) {
        dbHandler!!.updateNota(note, idNota!!)
        isEditable = false
    }

    private fun saveToBD(note: Note) {
        dbHandler!!.createNote(note)
    }

    private fun readNote(id: Int) {
        val note: Note = dbHandler!!.readNote(id)
        et_titulo.setText(note.tituloNota)
        et_content_nota.setText(note.contentNota)
        val dateFormat = DateFormat.getDateTimeInstance().format(note.dateNota)
        tv_date.text = dateFormat


    }
}

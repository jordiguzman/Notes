package kite.com.notes.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kite.com.notes.MainActivity
import kite.com.notes.R
import kite.com.notes.data.*
import kite.com.notes.model.Note
import kotlinx.android.synthetic.main.activity_crear_nota.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.image
import java.text.DateFormat
import android.view.ViewGroup.LayoutParams as LayoutParams1


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CrearNota : AppCompatActivity(), View.OnClickListener {


    private var dbHandler: NotesDataBaseHandler? = null
    private var idNota: Int? = null
    private var tituloNota: String? = null
    private var colorNota: Int? = null
    private var bundle: Bundle? = null
    private var isEditable: Boolean = false
    private var receivedIntent: Intent? = null
    private var receivedAction: String? = null
    private var receivedType: String? = null
    private var coloresNota: IntArray? = null
    private var coloresNotaPale: IntArray? = null
    private var alert: AlertDialog? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_nota)

        supportActionBar!!.title = getString(R.string.app_name)
        coloresNota = resources.getIntArray(R.array.colores_notas)
        coloresNotaPale = resources.getIntArray(R.array.colores_notas_pale)


        dbHandler = NotesDataBaseHandler(this)

        bundle = intent.extras
        if (bundle != null) {
            val fromCreate: Boolean = bundle!!.getBoolean("fromCreate")
            if (!fromCreate) {
                getShareTextFromOtherApps()
            } else {
                readNotes()
            }
        } else {
            fab_crear_nota.setOnClickListener {
                toSaveNote(checkColor(colorNota!!))
            }
        }

        iv_tag.setOnClickListener {
            startActivity(Intent(this, kite.com.notes.ui.activities.Tags::class.java))
        }
        iv_color.setOnClickListener {
            dialogColores()
        }

        for (c: Int in coloresNota!!.iterator()) {
            Log.d("COLOR", c.toString())
        }


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_white -> {
                val pos = 0
                changeColorNote(pos)
            }
            R.id.btn_blue -> {
                val pos = 1
                changeColorNote(pos)
            }
            R.id.btn_yellow -> {
                val pos = 2
                changeColorNote(pos)
            }
            R.id.btn_pink -> {
                val pos = 3
                changeColorNote(pos)
            }
            R.id.btn_red -> {
                val pos = 4
                changeColorNote(pos)
            }
            R.id.btn_green -> {
                val pos = 5
                changeColorNote(pos)
            }
            R.id.btn_grey -> {
                val pos = 6
                changeColorNote(pos)
            }
        }
    }

    private fun changeColorNote(pos: Int) {
        constrain_crear_nota.setBackgroundColor(coloresNota!![pos])
        et_titulo.setBackgroundColor(coloresNotaPale!![pos])
        et_content_nota.setBackgroundColor(coloresNotaPale!![pos])
        constrain_crear_nota_opciones.setBackgroundColor(coloresNotaPale!![pos])
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(coloresNota!![pos]))
        if (alert != null) {
            alert!!.cancel()
        }

    }

    @SuppressLint("InflateParams")
    private fun dialogColores() {
        val builder = AlertDialog.Builder(this)
        alert = builder.create()
        val inflater = layoutInflater
        alert!!.setTitle("Selecciona color")
        alert!!.window.setBackgroundDrawableResource(R.color.primary_light)
        val dialogLayout = inflater.inflate(R.layout.list_colors, null)
        alert!!.setView(dialogLayout)
        alert!!.show()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun readNotes() {
        if (intent.extras != null) {
            bundle = intent.extras
            idNota = bundle!!.getInt("id")
            tituloNota = bundle!!.getString("titulo")
            colorNota = bundle!!.getInt("color")
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
                    toSaveNote(colorNota!!)
                }
            }
        } else {
            fab_crear_nota.setOnClickListener {
                toSaveNote(colorNota!!)
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
                toSaveNote(colorNota!!)
            }
            et_content_nota.setText(receivedText)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun changeUI() {
        fab_crear_nota.image = getDrawable(R.drawable.ic_edit_black_24dp)
        et_titulo.isEnabled = false
        et_content_nota.isEnabled = false
        iv_tag.isEnabled = false
        iv_color.isEnabled = false

    }

    private fun toSaveNote(color: Int) {
        val note = Note()
        if (et_titulo.text.isEmpty() || et_content_nota.text.isEmpty()) {
            constrain_crear_nota.longSnackbar(getString(R.string.text_save_note))
        } else {
            note.tituloNota = et_titulo.text.toString()
            note.contentNota = et_content_nota.text.toString()
            //Log.d("COLOR", color.toString())
            note.noteColor = color
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
        changeColorNote(checkColor(note.noteColor!!))

    }

    private fun checkColor(color: Int): Int {
        when (color) {
            WHITE -> {
                return 0
            }
            BLUE -> {
                return 1
            }
            YELLOW -> {
                return 2
            }
            PINK -> {
                return 3
            }
            RED -> {
                return 4
            }
            GREEN -> {
                return 5
            }
            GREY -> {
                return 6
            }
        }
        return -1

    }
}

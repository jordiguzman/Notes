package kite.com.notes.data.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kite.com.notes.MainActivity
import kite.com.notes.R
import kite.com.notes.data.NotesDataBaseHandler
import kite.com.notes.model.Note
import kite.com.notes.ui.activities.CrearNota
import java.text.DateFormat


class NoteListAdapter(private val list: ArrayList<Note>,
                      private val context: Context): RecyclerView.Adapter<NoteListAdapter.ViewHolder>() {





    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindViews(list[position])

     }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_row, parent, false)



        return ViewHolder(view, context, list)
     }

    override fun getItemCount(): Int {
        return list.size
     }



    inner class ViewHolder(itemView: View, context: Context, list: ArrayList<Note>): RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        private var idNota: Int? = null
        private var mContext = context
        private var mList = list

        override fun onLongClick(v: View?): Boolean {
            showAlert()
            idNota = mList[adapterPosition].idNota!!

            return true
         }
        private fun showAlert() {
            val builder = AlertDialog.Builder(itemView.context)

            builder.setMessage("¿Qué quieres hacer?")

            builder.setNegativeButton("cancelar") { _, _ ->

            }

            builder.setPositiveButton("Borrar nota") { _, _ ->
               deleteNote(idNota!!)
            }
            builder.show()
        }

        private fun deleteNote(id: Int) {
            val mPosition: Int = adapterPosition
            mList[mPosition]
            mList.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
             val db = NotesDataBaseHandler(mContext)
            db.deleteNota(id)
            if (mList.isEmpty()){
                mContext.startActivity(Intent(mContext, MainActivity::class.java))
            }
        }

        override fun onClick(v: View?) {
             val itemPosition: Int = adapterPosition
            val nota = mList[itemPosition]
            val intent= Intent(mContext, CrearNota::class.java)
            intent.putExtra("id", nota.idNota)
            intent.putExtra("fromCreate", true)
            intent.putExtra("titulo", nota.tituloNota)
            mContext.startActivity(intent)
        }

        private var noteTitle = itemView.findViewById(R.id.tv_list_note_title) as TextView
        private var noteContent = itemView.findViewById(R.id.tv_note_content) as TextView
        private var noteDate = itemView.findViewById(R.id.tv_note_date) as TextView
        private var cvAdapter = itemView.findViewById(R.id.cv_item_row) as CardView


        @RequiresApi(Build.VERSION_CODES.O)
        fun bindViews(note: Note) {
            noteTitle.text = note.tituloNota
            if (note.contentNota!!.length > 20){
                noteContent.text = note.contentNota!!.subSequence(0, 20 ).toString().plus("...")
            }else {
                noteContent.text = note.contentNota
            }

            val dateFormat = DateFormat.getDateTimeInstance().format(note.dateNota)
            noteDate.text = dateFormat
            cvAdapter.setOnClickListener(this)
            cvAdapter.setOnLongClickListener(this)


        }


    }

}
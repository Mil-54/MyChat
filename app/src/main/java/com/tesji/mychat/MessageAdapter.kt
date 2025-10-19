package com.tesji.mychat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messageList[position]
        holder.messageText.text = currentMessage.text

        // --- ESTA ES LA LÓGICA CORREGIDA ---
        // Obtenemos los parámetros de layout del TextView, no del itemView completo
        val layoutParams = holder.messageText.layoutParams as FrameLayout.LayoutParams

        // Verificamos si el mensaje fue enviado por el usuario actual
        if (currentMessage.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            // Mensaje enviado (derecha, azul)
            layoutParams.gravity = Gravity.END
            holder.messageText.background = ContextCompat.getDrawable(context, R.drawable.background_sent)
            // Cambiar color de texto para que sea legible sobre fondo azul
            holder.messageText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            // Mensaje recibido (izquierda, gris)
            layoutParams.gravity = Gravity.START
            holder.messageText.background = ContextCompat.getDrawable(context, R.drawable.background_received)
            // Cambiar color de texto para que sea legible sobre fondo gris
            holder.messageText.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        // Aplicamos los parámetros de layout actualizados al TextView
        holder.messageText.layoutParams = layoutParams
    }


    override fun getItemCount(): Int {
        return messageList.size
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.textViewMessage)
    }
}


package com.tesji.mychat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.tesji.mychat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Usar la Toolbar que definimos en el XML
        setSupportActionBar(binding.toolbar)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        messageList = ArrayList()
        // CORRECCIÓN: Se invierten los argumentos para que coincidan con el constructor del Adapter
        messageAdapter = MessageAdapter(this, messageList)

        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.adapter = messageAdapter

        // Lógica para cargar los mensajes de la base de datos
        dbRef.child("chats").child("general").child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                    // Desplazarse al último mensaje
                    binding.recyclerViewMessages.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error si es necesario
                }
            })

        // Lógica para enviar un mensaje
        binding.buttonSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString()
            val senderId = auth.currentUser?.uid

            if (messageText.isNotEmpty() && senderId != null) {
                val message = Message(messageText, senderId, System.currentTimeMillis())
                dbRef.child("chats").child("general").child("messages").push().setValue(message)
                binding.editTextMessage.setText("")
            }
        }
    }

    // --- Funciones para el Menú de Logout ---

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}


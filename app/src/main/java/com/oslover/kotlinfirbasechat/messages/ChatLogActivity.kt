package com.oslover.kotlinfirbasechat.messages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.oslover.kotlinfirbasechat.R
import com.oslover.kotlinfirbasechat.messages.LatestMessagesActivity.Companion.currentUser
import com.oslover.kotlinfirbasechat.models.ChatMessage
import com.oslover.kotlinfirbasechat.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    private var toUser: User? = null

    companion object {
        val TAG = "ChatLog"
    }
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        recycleview_chat_log.adapter = adapter

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempting to send message...")
            performSendMessage()
        }

        listenForMessages()
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        if (fromId == null || toId == null) return

        val ref =  FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }
                    else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {
        val text = text_chat_log.text.toString()
        if (text.length == 0) return

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        if (fromId == null || toId == null) return

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val timeStamp = System.currentTimeMillis()/1000
        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, timeStamp)
        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    text_chat_log.text.clear()
                    recycleview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
                .addOnFailureListener{

                }
        toRef.setValue(chatMessage)
    }
}


class ChatFromItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_chat_from_row.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_sender_chat_from_row)
    }
}

class ChatToItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_chat_to_row.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_sender_chat_to_row)
    }
}
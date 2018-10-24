package com.oslover.kotlinfirbasechat.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.oslover.kotlinfirbasechat.R
import com.oslover.kotlinfirbasechat.models.ChatMessage
import com.oslover.kotlinfirbasechat.models.User
import com.oslover.kotlinfirbasechat.registerlogin.RegisterActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {
    companion object {
        var currentUser: User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        verifyUserIsLoggedIn()
        listenLatestMessages()
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            showRegister()
        }
        else {
            fetchCurrentUser()
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecycleView() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenLatestMessages() {
        val userId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$userId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecycleView()
                adapter.add(LatestMessageRow(chatMessage))
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                var chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecycleView()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
        recycleview_latest_messages.adapter = adapter
    }

    fun showRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun showNewMessage() {
        val intent = Intent(this, NewMessageActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                showNewMessage()
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                showRegister()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder> () {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.last_message_latest_message_row.text = chatMessage.text
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}


package com.oslover.kotlinfirbasechat.view

import android.service.autofill.DateTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.oslover.kotlinfirbasechat.R
import com.oslover.kotlinfirbasechat.models.ChatMessage
import com.oslover.kotlinfirbasechat.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*
import java.text.SimpleDateFormat
import java.util.*


class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() {
    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.last_message_latest_message_row.text = chatMessage.text
        val timestamp = chatMessage.timeStamp * 1000L

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date =  Date(timestamp)
        viewHolder.itemView.date_latest_message_row.text = dateFormat.format(date)

        val chatPartnerId: String
        if ( chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        }
        else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java) ?: return
                chatPartnerUser = user
                viewHolder.itemView.username_text_latest_message_row.text = user.username
                Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.userphoto_latest_message_row)
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}
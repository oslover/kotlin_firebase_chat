package com.oslover.kotlinfirbasechat.messages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.oslover.kotlinfirbasechat.R
import com.oslover.kotlinfirbasechat.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.username

        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        recycleview_chat_log.adapter = adapter
    }
}


class ChatFromItem: Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
//        viewHolder.itemView.username_textview_new_message.text = user.username
//        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.userphoto_imageview_new_message_row)
    }
}

class ChatToItem: Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
//        viewHolder.itemView.username_textview_new_message.text = user.username
//        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.userphoto_imageview_new_message_row)
    }
}
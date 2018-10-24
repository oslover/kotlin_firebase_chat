package com.oslover.kotlinfirbasechat.view

import com.oslover.kotlinfirbasechat.R
import com.oslover.kotlinfirbasechat.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


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
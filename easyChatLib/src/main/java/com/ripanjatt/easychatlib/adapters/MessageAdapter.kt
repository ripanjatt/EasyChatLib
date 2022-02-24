package com.ripanjatt.easychatlib.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ripanjatt.easychatlib.R
import com.ripanjatt.easychatlib.dataClasses.MessageObj
import com.ripanjatt.easychatlib.databinding.IncomingMessageBinding
import com.ripanjatt.easychatlib.databinding.MessageContainerBinding
import com.ripanjatt.easychatlib.databinding.OutgoingMessageBinding
import com.ripanjatt.easychatlib.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MessageAdapter(private val list: ArrayList<MessageObj>,
                     private val context: Context,
                     private val recyclerView: RecyclerView
) : RecyclerView.Adapter<MessageAdapter.MessageHolder>() {

    init {
        updateDateTime()
    }

    class MessageHolder(val binding: MessageContainerBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        return MessageHolder(MessageContainerBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.binding.frame.removeAllViews()
        val item = list[position]
        if(item.hasView) {
            if(item.view!!.parent != null) {
                (item.view.parent as ViewGroup).removeView(item.view)
            }
            if(item.hasView) {
                val temp = item.view as ViewGroup
                if(item.showDP) {
                    if(temp.findViewById<CardView>(R.id.dpCard) != null)
                        temp.findViewById<CardView>(R.id.dpCard).visibility = View.VISIBLE
                } else {
                    if(temp.findViewById<CardView>(R.id.dpCard) != null)
                        temp.findViewById<CardView>(R.id.dpCard).visibility = View.INVISIBLE
                }
            }
            holder.binding.frame.addView(item.view)
        } else {
            if(item.isIncoming) {
                val binding = IncomingMessageBinding.inflate(LayoutInflater.from(context))
                binding.inMessage.text = item.message
                if(item.showDP) {
                    if(item.dpBitmap != null) {
                        binding.inDp.setImageBitmap(item.dpBitmap)
                    }
                    binding.dpCard.visibility = View.VISIBLE
                } else {
                    val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(4, 0, 4, 0)
                    binding.inMessage.layoutParams = layoutParams
                    binding.dpCard.visibility = View.INVISIBLE
                }
                if(position > 0 && list[position - 1].isIncoming) {
                    val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(4, 0, 4, 0)
                    binding.inMessage.layoutParams = layoutParams
                    binding.dpCard.visibility = View.INVISIBLE
                }
                binding.dateTime.text = Util.getFormattedDate(item.date)
                holder.binding.frame.addView(binding.root, 0)
            } else {
                val binding = OutgoingMessageBinding.inflate(LayoutInflater.from(context))
                binding.outMessage.text = item.message
                binding.dateTime.text = Util.getFormattedDate(item.date)
                holder.binding.frame.addView(binding.root, 0)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDateTime() {
        thread {
            var running = true
            while (running) {
                Thread.sleep(60000)
                try {
                    val start = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    val end = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    CoroutineScope(Main).launch {
                        for (i in start..end) {
                            if(list[i].hasView) {
                                val temp = list[i].view as ViewGroup
                                if(temp.findViewById<TextView>(R.id.dateTime) != null)
                                    temp.findViewById<TextView>(R.id.dateTime).text = Util.getFormattedDate(list[i].date)
                            }
                            notifyItemChanged(i)
                        }
                        notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", "$e")
                    running = false
                }
            }
        }
    }
}
package com.ripanjatt.easychatlib

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.ripanjatt.easychatlib.adapters.MessageAdapter
import com.ripanjatt.easychatlib.dataClasses.MessageObj
import com.ripanjatt.easychatlib.databinding.*
import com.ripanjatt.easychatlib.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class EasyChatLib(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {

    private var binding = MainLayoutBinding.inflate(LayoutInflater.from(context))
    private val arrayList = ArrayList<MessageObj>()
    private var dpBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_baseline_person_24)
    private var adapter: MessageAdapter = MessageAdapter(arrayList, context, binding.recyclerView)
    private var headerHeight = 0
    private var parentActivity: Activity? = null
    private var onSendListener: ((String) -> Unit)? = null
    private var onFileButtonClickListener: (() -> Unit)? = null

    init {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.root.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        binding.sendButton.setOnClickListener {
            val message = binding.inputBox.text.toString()
            if(message.trim() != "") {
                addOutgoingMessage(message.trim(), Date(System.currentTimeMillis()))
                binding.inputBox.setText("")
                onSendListener?.let { it1 -> it1(message.trim()) }
            }
        }
        binding.inputBox.setOnFocusChangeListener { _, _ ->
            animateHeader()
            delayedScroll()
        }
        binding.inputBox.setOnClickListener {
            delayedScroll()
        }
        binding.fileButton.setOnClickListener { _ ->
            if(onFileButtonClickListener != null)
                onFileButtonClickListener?.let { it -> it() }
        }
        addView(binding.root)
    }

    fun setUp(activity: Activity, title: String) {
        parentActivity = activity
        binding.headerTitle.text = title
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    fun addIncomingMessage(message: String, showDP: Boolean, date: Date) {
        arrayList.add(MessageObj(message, true, showDP, null, false, dpBitmap, date))
        notifyAndScroll()
    }

    fun addOutgoingMessage(message: String, date: Date) {
        arrayList.add(MessageObj(message, isIncoming = false, false, null, false, dpBitmap, date))
        notifyAndScroll()
    }

    fun setDP(res: Int) {
        dpBitmap = BitmapFactory.decodeResource(context.resources, res)
        binding.headerImage.setImageBitmap(dpBitmap)
        binding.mainDP.setImageBitmap(dpBitmap)
    }

    fun setDP(bitmap: Bitmap) {
        dpBitmap = bitmap
        binding.headerImage.setImageBitmap(dpBitmap)
        binding.mainDP.setImageBitmap(dpBitmap)
    }

    fun setOnSendListener(listener: (String) -> Unit) {
        onSendListener = listener
    }

    fun setOnFileButtonClickListener(listener: () -> Unit) {
        onFileButtonClickListener = listener
    }

    fun addIncomingImage(bitmap: Bitmap, message: String?, showDP: Boolean, date: Date) {
        val inBinding = IncomingImageBinding.inflate(LayoutInflater.from(context))
        if(message != null) {
            inBinding.inImgMsg.text = message.toString()
        } else {
            inBinding.inImgMsg.visibility = View.GONE
        }
        inBinding.inImg.setImageBitmap(bitmap)
        inBinding.imgDp.setImageBitmap(dpBitmap)
        inBinding.dateTime.text = Util.getFormattedDate(date)
        arrayList.add(MessageObj("", true, showDP, inBinding.root, true, null, date))
        notifyAndScroll()
    }

    fun addOutgoingImage(bitmap: Bitmap, message: String?, date: Date) {
        val outBinding = OutgoingImageBinding.inflate(LayoutInflater.from(context))
        if(message != null) {
            outBinding.outImgMsg.text = message.toString()
        } else {
            outBinding.outImgMsg.visibility = View.GONE
        }
        outBinding.outImg.setImageBitmap(bitmap)
        outBinding.dateTime.text = Util.getFormattedDate(date)
        arrayList.add(MessageObj("", false, showDP = false, outBinding.root, true, null, Date(System.currentTimeMillis())))
        notifyAndScroll()
    }

    fun addIncomingView(view: View, showDP: Boolean, date: Date) {
        val incomingViewBinding = IncomingViewBinding.inflate(LayoutInflater.from(context))
        incomingViewBinding.inFrame.addView(view)
        incomingViewBinding.dateTime.text = Util.getFormattedDate(date)
        arrayList.add(MessageObj("", true, showDP, incomingViewBinding.root, true, null, date))
        notifyAndScroll()
    }

    fun addOutgoingView(view: View, date: Date) {
        val outgoingViewBinding = OutgoingViewBinding.inflate(LayoutInflater.from(context))
        outgoingViewBinding.outFrame.addView(view)
        outgoingViewBinding.dateTime.text = Util.getFormattedDate(date)
        arrayList.add(MessageObj("", false, showDP = false, outgoingViewBinding.root, true, null, date))
        notifyAndScroll()
    }

    fun addIncomingView(res: Int, showDP: Boolean, date: Date) {
        val incomingViewBinding = IncomingViewBinding.inflate(LayoutInflater.from(context))
        incomingViewBinding.inFrame.addView(LayoutInflater.from(context).inflate(res, null))
        incomingViewBinding.dateTime.text = Util.getFormattedDate(date)
        arrayList.add(MessageObj("", true, showDP, incomingViewBinding.root, true, null, date))
        notifyAndScroll()
    }

    fun addOutgoingView(res: Int, date: Date) {
        val outgoingViewBinding = OutgoingViewBinding.inflate(LayoutInflater.from(context))
        outgoingViewBinding.outFrame.addView(LayoutInflater.from(context).inflate(res, null))
        outgoingViewBinding.dateTime.text = Util.getFormattedDate(date)
        arrayList.add(MessageObj("", false, showDP = false, outgoingViewBinding.root, true, null, date))
        notifyAndScroll()
    }

    fun deleteItemAt(index: Int) {
        arrayList.removeAt(index)
        adapter.notifyItemRemoved(index)
    }

    private fun notifyAndScroll() {
        adapter.notifyItemInserted(arrayList.size - 1)
        binding.recyclerView.smoothScrollToPosition(arrayList.size - 1)
    }

    private fun animateHeader() {
        if(parentActivity != null) {
            @Suppress("DEPRECATION")
            parentActivity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            parentActivity?.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        thread {
            headerHeight = binding.headerImage.height
            var height = headerHeight
            while (height > 0) {
                height -= 50
                Thread.sleep(25)
                CoroutineScope(Main).launch {
                    binding.headerImage.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
                }
            }
        }
    }

    private fun delayedScroll() {
        thread {
            Thread.sleep(100)
            CoroutineScope(Main).launch {
                if(arrayList.size > 0)
                    binding.recyclerView.smoothScrollToPosition(arrayList.size - 1)
            }
        }
    }
}
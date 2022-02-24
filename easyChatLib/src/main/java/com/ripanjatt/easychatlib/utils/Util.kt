package com.ripanjatt.easychatlib.utils

import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun getFormattedDate(date: Date): String {
        val current = SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault()).format(Date(System.currentTimeMillis()))
        val input = SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault()).format(date)
        if(current.split(" ")[1] == input.split(" ")[1]) {
            if(current == input) {
                return "Just now"
            }
            if(current.split(" ")[0].split(":")[0] == input.split(" ")[0].split(":")[0]){
                return "${Integer.parseInt(current.split(" ")[0].split(":")[1]) - Integer.parseInt(input.split(" ")[0].split(":")[1])} min ago"
            }
            return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
        }
        return SimpleDateFormat("hh:mm a dd/MM/yy", Locale.getDefault()).format(date)
    }
}
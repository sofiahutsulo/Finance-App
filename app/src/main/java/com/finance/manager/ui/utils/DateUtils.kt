package com.finance.manager.ui.utils

import java.text.SimpleDateFormat
import java.util.*


fun <T> List<T>.groupByDate(dateSelector: (T) -> Date): Map<String, List<T>> {
    return this.groupBy { item ->
        getDateGroupName(dateSelector(item))
    }
}


fun getDateGroupName(date: Date): String {
    val calendar = Calendar.getInstance()
    val today = calendar.time

    val transactionCalendar = Calendar.getInstance()
    transactionCalendar.time = date

    return when {

        isSameDay(date, today) -> "Сьогодні"


        isSameDay(date, getYesterday()) -> "Вчора"


        isThisWeek(date) -> {
            val dayFormat = SimpleDateFormat("EEEE", Locale("uk", "UA"))
            dayFormat.format(date).replaceFirstChar { it.uppercase() }
        }


        isThisYear(date) -> {
            val dateFormat = SimpleDateFormat("d MMMM", Locale("uk", "UA"))
            dateFormat.format(date)
        }


        else -> {
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("uk", "UA"))
            dateFormat.format(date)
        }
    }
}


private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}


private fun getYesterday(): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    return calendar.time
}


private fun isThisWeek(date: Date): Boolean {
    val calendar = Calendar.getInstance()


    val startOfWeek = Calendar.getInstance()
    startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    startOfWeek.set(Calendar.HOUR_OF_DAY, 0)
    startOfWeek.set(Calendar.MINUTE, 0)
    startOfWeek.set(Calendar.SECOND, 0)
    startOfWeek.set(Calendar.MILLISECOND, 0)


    val endOfWeek = Calendar.getInstance()
    endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    endOfWeek.set(Calendar.HOUR_OF_DAY, 23)
    endOfWeek.set(Calendar.MINUTE, 59)
    endOfWeek.set(Calendar.SECOND, 59)

    return date >= startOfWeek.time && date <= endOfWeek.time
}


private fun isThisYear(date: Date): Boolean {
    val calendar = Calendar.getInstance()
    val transactionCalendar = Calendar.getInstance()
    transactionCalendar.time = date

    return calendar.get(Calendar.YEAR) == transactionCalendar.get(Calendar.YEAR)
}

package com.mjapa21.smartwallet.presentation.pages.shared.format

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs


fun formatToday(): String {
    val formatter = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    return formatter.format(Date())
}

fun Double.formatCurrency(): String {
    return "₾" + "%,.2f".format(Locale.US, this)
}

fun Double.formatSignedAmount(): String {
    val formatted = (abs(this)).formatCurrency()
    return if (this >= 0) "+$formatted" else "-$formatted"
}

fun Double.formatBalance(): String {
    return if (this < 0) formatSignedAmount() else formatCurrency()
}

fun Long.toShortDate(): String {
    // date is epoch millis (Long) — so we can wrap directly in Date() for formatting
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    return formatter.format(Date(this))
}

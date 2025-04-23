package com.example.budgetly_asupersimplefinancetracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

data class Transaction(
    val title: String,
    val amount: Double,
    val date: String,
    val category: String,
    val isExpense: Boolean = true
)

class TransactionsAdapter(context: Context, transactions: List<Transaction>) :
    ArrayAdapter<Transaction>(context, 0, transactions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the transaction for this position
        val transaction = getItem(position)!!

        // Check if an existing view is being reused, otherwise inflate the view
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_transaction, parent, false)

        // Get views
        val categoryIcon = view.findViewById<ImageView>(R.id.category_icon)
        val title = view.findViewById<TextView>(R.id.transaction_title)
        val date = view.findViewById<TextView>(R.id.transaction_date)
        val amount = view.findViewById<TextView>(R.id.transaction_amount)

        // Set data
        title.text = transaction.title
        date.text = transaction.date

        // Format amount with currency symbol and color
        val amountText = if (transaction.isExpense) {
            amount.setTextColor(context.getColor(android.R.color.holo_red_light))
            "-$%.2f".format(transaction.amount)
        } else {
            amount.setTextColor(context.getColor(android.R.color.holo_green_dark))
            "+$%.2f".format(transaction.amount)
        }
        amount.text = amountText

        // Set category icon based on category
        val iconResId = when (transaction.category.lowercase()) {
            "food" -> R.drawable.food
            "transport" -> R.drawable.transport
            "housing" -> R.drawable.house
            "personal" -> R.drawable.personal_care
            "shopping" -> R.drawable.shopping
            "health" -> R.drawable.health
            else -> R.drawable.shopping
        }
        categoryIcon.setImageResource(iconResId)

        return view
    }
} 
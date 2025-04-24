package com.example.budgetly_asupersimplefinancetracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Model of Transaction
data class Transaction(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val amount: Double,
    val date: String,
    val category: String,
    val isExpense: Boolean = true
)

class TransactionsAdapter(private val context: Context, private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    // ViewHolder that hold the basic layout for each items

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryIcon: ImageView = view.findViewById(R.id.category_icon)
        val title: TextView = view.findViewById(R.id.transaction_title)
        val date: TextView = view.findViewById(R.id.transaction_date)
        val amount: TextView = view.findViewById(R.id.transaction_amount)
    }

    // Putting the actual view into the ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    // Binding the data to the view

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set data
        holder.title.text = transaction.title
        holder.date.text = transaction.date

        // Format amount with currency symbol and color
        val amountText = if (transaction.isExpense) {
            holder.amount.setTextColor(context.getColor(android.R.color.holo_red_light))
            "-$%.2f".format(transaction.amount)
        } else {
            holder.amount.setTextColor(context.getColor(android.R.color.holo_green_dark))
            "+$%.2f".format(transaction.amount)
        }
        holder.amount.text = amountText

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
        holder.categoryIcon.setImageResource(iconResId)
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
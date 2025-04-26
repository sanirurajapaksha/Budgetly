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
    val isExpense: Boolean = true,
    val userEmail: String
)

class TransactionsAdapter(private val context: Context, private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    interface OnTransactionClickListener {
        fun onTransactionClick(transaction: Transaction)
    }

    private var clickListener: OnTransactionClickListener? = null

    fun setOnTransactionClickListener(listener: OnTransactionClickListener) {
        this.clickListener = listener
    }

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
            "food & drink" -> R.drawable.food
            "transportation" -> R.drawable.transport
            "housing" -> R.drawable.house
            "personal care" -> R.drawable.personal_care
            "shopping" -> R.drawable.shopping
            "health care" -> R.drawable.health
            "salary" -> R.drawable.salary
            "investment" -> R.drawable.investment
            else -> R.drawable.shopping
        }
        holder.categoryIcon.setImageResource(iconResId)

        // Set click listener
        holder.itemView.setOnClickListener {
            clickListener?.onTransactionClick(transaction)
        }
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
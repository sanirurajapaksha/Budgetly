package com.example.budgetly_asupersimplefinancetracker

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Transactions.newInstance] factory method to
 * create an instance of this fragment.
 */
class Transactions : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTransaction: FloatingActionButton
    private lateinit var transactionManager: TransactionManager
    private lateinit var adapter: TransactionsAdapter
    private lateinit var budgetNotificationManager: BudgetNotificationManager
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get user email from SharedPreferences
        userEmail = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("user_email", "") ?: ""

        transactionManager = TransactionManager(requireContext())
        budgetNotificationManager = BudgetNotificationManager(requireContext())

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.transactions_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TransactionsAdapter(requireContext(), emptyList())
        recyclerView.adapter = adapter

        // Get transactions from SharedPreferences
        val transactions = transactionManager.getTransactions(userEmail)
        adapter.updateTransactions(transactions)

        // Set up click listener for transactions
        adapter.setOnTransactionClickListener(object : TransactionsAdapter.OnTransactionClickListener {
            override fun onTransactionClick(transaction: Transaction) {
                val intent = Intent(context, AddTransactionActivity::class.java).apply {
                    putExtra("transaction_id", transaction.id)
                    putExtra("title", transaction.title)
                    putExtra("amount", transaction.amount)
                    putExtra("date", transaction.date)
                    putExtra("category", transaction.category)
                    putExtra("is_expense", transaction.isExpense)
                }
                startActivity(intent)
            }
        })

        // Set up swipe to delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Get the position of the item being swiped
                val position = viewHolder.adapterPosition
                val transactions = transactionManager.getTransactions(userEmail)
                val transaction = transactions[position]
                
                // Delete from SharedPreferences
                transactionManager.deleteTransaction(transaction.id, userEmail)
                
                // Update adapter with new list
                val updatedTransactions = transactionManager.getTransactions(userEmail)
                adapter.updateTransactions(updatedTransactions)

                // Show undo snack bar
                Snackbar.make(recyclerView, "Transaction deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        // Restore the transaction
                        transactionManager.saveTransaction(transaction)
                        val restoredTransactions = transactionManager.getTransactions(userEmail)
                        adapter.updateTransactions(restoredTransactions)
                    }
                    .show()
            }

            // Handles the red background with the trash icon on swipe
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                    .addActionIcon(android.R.drawable.ic_menu_delete)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Handle FAB click
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction)
        fabAddTransaction.setOnClickListener {
            val intent = Intent(context, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list when returning from AddTransactionActivity
        val transactions = transactionManager.getTransactions(userEmail)
        adapter.updateTransactions(transactions)
        
        // Check budget status and send notifications if needed
        budgetNotificationManager.checkAndNotifyBudgetStatus(transactionManager)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Transactions.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Transactions().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
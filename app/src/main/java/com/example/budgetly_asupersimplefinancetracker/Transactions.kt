package com.example.budgetly_asupersimplefinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

    private lateinit var listView: ListView
    private lateinit var fabAddTransaction: FloatingActionButton

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

        listView = view.findViewById(R.id.transactions_list_view)
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction)

        // Sample transactions data
        val transactions = listOf(
            Transaction(
                "Grocery Shopping",
                24.99,
                "Today, 2:30 PM",
                "food"
            ),
            Transaction(
                "Bus Ticket",
                3.50,
                "Today, 1:15 PM",
                "transport"
            ),
            Transaction(
                "Rent Payment",
                1200.00,
                "Yesterday",
                "housing"
            ),
            Transaction(
                "Salary Deposit",
                3000.00,
                "Mar 1, 2024",
                "salary",
                false
            ),
            Transaction(
                "Coffee Shop",
                5.75,
                "Mar 1, 2024",
                "food"
            ),
            Transaction(
                "Pharmacy",
                32.50,
                "Feb 29, 2024",
                "health"
            )
        )

        // Set up adapter with sample data
        context?.let {
            listView.adapter = TransactionsAdapter(it, transactions)
        }

        // Handle FAB click
        fabAddTransaction.setOnClickListener {
            val intent = Intent(context, AddTransactionActivity::class.java)
            startActivity(intent)
        }
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
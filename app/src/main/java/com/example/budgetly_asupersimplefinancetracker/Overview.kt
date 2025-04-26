package com.example.budgetly_asupersimplefinancetracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.graphics.Color
import android.content.res.ColorStateList
import java.util.*
import java.text.SimpleDateFormat
import android.widget.ProgressBar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Overview.newInstance] factory method to
 * create an instance of this fragment.
 */
class Overview : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var transactionManager: TransactionManager
    private lateinit var userEmail: String
    private lateinit var recentTransactionsContainer: ViewGroup
    private lateinit var recentTransactionsCard: MaterialCardView
    private lateinit var spendingTitleText: TextView
    private lateinit var incomeTitleText: TextView
    private lateinit var categoryAmounts: Map<String, TextView>
    private lateinit var budgetAmountText: TextView
    private lateinit var remainingAmountText: TextView
    private lateinit var budgetProgressBar: ProgressBar
    private lateinit var budgetWarningText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get user email from SharedPreferences
        userEmail = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("user_email", "") ?: ""

        transactionManager = TransactionManager(requireContext())
        initializeViews(view)
        setupClickListeners()
        updateAllData()
    }

    private fun initializeViews(view: View) {
        recentTransactionsContainer = view.findViewById(R.id.recent_transactions_container)
        recentTransactionsCard = view.findViewById(R.id.recent_transactions_card)
        spendingTitleText = view.findViewById(R.id.spending_title_text)
        incomeTitleText = view.findViewById(R.id.income_title_text)

        // Initialize category amount TextViews
        categoryAmounts = mapOf(
            "food & drink" to view.findViewById(R.id.food_amount),
            "transportation" to view.findViewById(R.id.transportation_amount),
            "housing" to view.findViewById(R.id.housing_amount),
            "personal care" to view.findViewById(R.id.personal_care_amount),
            "shopping" to view.findViewById(R.id.shopping_amount),
            "health care" to view.findViewById(R.id.health_care_amount),
            "salary" to view.findViewById(R.id.salary_amount),
            "investment" to view.findViewById(R.id.investment_amount)
        )

        budgetAmountText = view.findViewById(R.id.budget_amount_text)
        remainingAmountText = view.findViewById(R.id.remaining_amount_text)
        budgetProgressBar = view.findViewById(R.id.budget_progress_bar)
        budgetWarningText = view.findViewById(R.id.budget_warning_text)
    }

    private fun setupClickListeners() {
        recentTransactionsCard.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.nav_transactions
        }
    }

    private fun updateAllData() {
        updateCategoryTiles()
        updateTotalAmounts()
        updateRecentTransactions()
        updateBudgetStatus()
    }

    private fun updateCategoryTiles() {
        val transactions = transactionManager.getTransactions(userEmail)
        
        // Reset all amounts to zero
        categoryAmounts.values.forEach { it.text = "$0" }

        // Group transactions by category and calculate totals
        val categoryTotals = transactions.groupBy { 
            it.category.lowercase()
        }.mapValues { (_, transactions) ->
            transactions.sumOf { it.amount }
        }

        // Update each category tile with its total
        categoryTotals.forEach { (category, total) ->
            categoryAmounts[category]?.text = "$${String.format("%.2f", total)}"
        }
    }

    private fun updateTotalAmounts() {
        val transactions = transactionManager.getTransactions(userEmail)
        
        val totalSpending = transactions
            .filter { it.isExpense }
            .sumOf { it.amount }

        val totalIncome = transactions
            .filter { !it.isExpense }
            .sumOf { it.amount }

        spendingTitleText.text = "Spending $${String.format("%.2f", totalSpending)}"
        incomeTitleText.text = "Income $${String.format("%.2f", totalIncome)}"
    }

    private fun updateBudgetStatus() {
        val monthlyBudget = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getFloat("monthly_budget", 0f)
        
        val currentMonthExpenses = calculateCurrentMonthExpenses()
        val remainingAmount = monthlyBudget - currentMonthExpenses
        
        // Update UI
        budgetAmountText.text = "Monthly Budget: $${String.format("%.2f", monthlyBudget)}"
        remainingAmountText.text = "Remaining: $${String.format("%.2f", remainingAmount)}"
        
        // Calculate progress percentage
        val progressPercentage = (currentMonthExpenses / monthlyBudget * 100).toInt()
        budgetProgressBar.progress = progressPercentage
        
        // Update progress bar color and show warning based on remaining amount
        when {
            progressPercentage >= 90 -> {
                budgetProgressBar.progressTintList = ColorStateList.valueOf(Color.RED)
                budgetWarningText.apply {
                    text = "Warning: Budget almost exceeded!"
                    visibility = View.VISIBLE
                }
            }
            progressPercentage >= 75 -> {
                budgetProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FFA500"))
                budgetWarningText.apply {
                    text = "Warning: Approaching budget limit"
                    visibility = View.VISIBLE
                }
            }
            else -> {
                budgetProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                budgetWarningText.visibility = View.GONE
            }
        }
    }

    private fun calculateCurrentMonthExpenses(): Float {
        return transactionManager.getTransactions(userEmail)
            .filter { transaction -> 
                val transactionDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .parse(transaction.date)
                val transactionCalendar = Calendar.getInstance().apply { time = transactionDate }
                
                transaction.isExpense &&
                transactionCalendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                transactionCalendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
            }
            .sumOf { it.amount }
            .toFloat()
    }

    override fun onResume() {
        super.onResume()
        updateAllData()
    }

    private fun updateRecentTransactions() {
        recentTransactionsContainer.removeAllViews()
        val transactions = transactionManager.getTransactions(userEmail).take(3)

        if (transactions.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "No recent transactions"
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
            recentTransactionsContainer.addView(emptyText)
            return
        }

        transactions.forEach { transaction ->
            val transactionView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_transaction, recentTransactionsContainer, false)

            // Set transaction data
            transactionView.findViewById<TextView>(R.id.transaction_title).text = transaction.title
            transactionView.findViewById<TextView>(R.id.transaction_date).text = transaction.date
            transactionView.findViewById<TextView>(R.id.transaction_amount).apply {
                text = if (transaction.isExpense) "-$%.2f".format(transaction.amount) else "+$%.2f".format(transaction.amount)
                setTextColor(ContextCompat.getColor(requireContext(), 
                    if (transaction.isExpense) android.R.color.holo_red_light else android.R.color.holo_green_dark))
            }

            // Set category icon
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
            transactionView.findViewById<ImageView>(R.id.category_icon).setImageResource(iconResId)

            recentTransactionsContainer.addView(transactionView)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Overview.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Overview().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
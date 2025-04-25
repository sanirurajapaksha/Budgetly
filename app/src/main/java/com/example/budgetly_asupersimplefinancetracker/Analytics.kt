package com.example.budgetly_asupersimplefinancetracker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Analytics.newInstance] factory method to
 * create an instance of this fragment.
 */
class Analytics : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var transactionManager: TransactionManager
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var lineChart: LineChart
    private lateinit var totalSpendingText: TextView
    private lateinit var totalIncomeText: TextView
    private lateinit var savingsRateText: TextView

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
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        transactionManager = TransactionManager(requireContext())
        
        initializeViews(view)
        setupCharts()
        updateAnalytics()
    }

    private fun initializeViews(view: View) {
        pieChart = view.findViewById(R.id.category_pie_chart)
        barChart = view.findViewById(R.id.monthly_bar_chart)
        lineChart = view.findViewById(R.id.trend_line_chart)
        totalSpendingText = view.findViewById(R.id.total_spending_text)
        totalIncomeText = view.findViewById(R.id.total_income_text)
        savingsRateText = view.findViewById(R.id.savings_rate_text)
    }

    private fun setupCharts() {
        // Setup Pie Chart
        pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setEntryLabelTextSize(12f)
            setEntryLabelColor(Color.BLACK)
            legend.isEnabled = true
            isDrawHoleEnabled = true
            holeRadius = 58f
            setTransparentCircleRadius(61f)
        }

        // Setup Bar Chart
        barChart.apply {
            description.isEnabled = false
            setFitBars(true)
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = true
        }

        // Setup Line Chart
        lineChart.apply {
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }

    private fun updateAnalytics() {
        val transactions = transactionManager.getTransactions()
        updateCategoryAnalysis(transactions)
        updateMonthlyTrends(transactions)
        updateIncomeVsExpense(transactions)
    }

    private fun updateCategoryAnalysis(transactions: List<Transaction>) {
        // Group transactions by category and calculate total amount
        val categoryTotals = transactions
            .filter { it.isExpense }
            .groupBy { it.category }
            .mapValues { (_, transactions) -> 
                transactions.sumOf { it.amount }
            }

        // Create pie chart entries
        val entries = categoryTotals.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Spending by Category")
        dataSet.colors = getCustomColors()
        dataSet.valueTextSize = 11f
        dataSet.valueTextColor = Color.BLACK

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))
        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun updateMonthlyTrends(transactions: List<Transaction>) {
        // Group transactions by month and calculate totals
        val monthlyTotals = transactions
            .filter { it.isExpense }
            .groupBy { it.date.substring(0, 3) } // Assuming date format "MMM dd, yyyy"
            .mapValues { (_, transactions) -> 
                transactions.sumOf { it.amount }
            }

        val entries = monthlyTotals.map { (month, amount) ->
            BarEntry(monthlyTotals.keys.indexOf(month).toFloat(), amount.toFloat())
        }

        val dataSet = BarDataSet(entries, "Monthly Spending")
        dataSet.colors = getCustomColors()
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.invalidate()
    }

    private fun updateIncomeVsExpense(transactions: List<Transaction>) {
        val totalIncome = transactions
            .filter { !it.isExpense }
            .sumOf { it.amount }

        val totalExpense = transactions
            .filter { it.isExpense }
            .sumOf { it.amount }

        val savingsRate = if (totalIncome > 0) {
            ((totalIncome - totalExpense) / totalIncome * 100).toInt()
        } else 0

        totalIncomeText.text = "Total Income: $${String.format("%.2f", totalIncome)}"
        totalSpendingText.text = "Total Spending: $${String.format("%.2f", totalExpense)}"
        savingsRateText.text = "Savings Rate: $savingsRate%"
    }

    private fun getCustomColors(): List<Int> {
        return listOf(
            Color.rgb(255, 179, 0),   // Food & Drink
            Color.rgb(33, 150, 243),   // Transportation
            Color.rgb(76, 175, 80),    // Housing
            Color.rgb(156, 39, 176),   // Personal Care
            Color.rgb(233, 30, 99),    // Shopping
            Color.rgb(0, 191, 165),    // Health Care
            Color.rgb(255, 152, 0),    // Salary
            Color.rgb(63, 81, 181)     // Investment
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Analytics.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Analytics().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
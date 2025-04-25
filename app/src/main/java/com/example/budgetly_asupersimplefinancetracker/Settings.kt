package com.example.budgetly_asupersimplefinancetracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var transactionManager: TransactionManager
    private lateinit var backupButton: MaterialButton
    private lateinit var restoreButton: MaterialButton
    private lateinit var lastBackupText: TextView
    private lateinit var budgetEditText: TextInputEditText
    private lateinit var saveBudgetButton: MaterialButton

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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        transactionManager = TransactionManager(requireContext())
        initializeViews(view)
        setupClickListeners()
        updateLastBackupDate()
        loadSavedBudget()
    }

    private fun initializeViews(view: View) {
        backupButton = view.findViewById(R.id.backup_button)
        restoreButton = view.findViewById(R.id.restore_button)
        lastBackupText = view.findViewById(R.id.last_backup_text)
        budgetEditText = view.findViewById(R.id.budget_edit_text)
        saveBudgetButton = view.findViewById(R.id.save_budget_button)
    }

    private fun setupClickListeners() {
        backupButton.setOnClickListener {
            backupUserData()
        }

        restoreButton.setOnClickListener {
            restoreUserData()
        }

        saveBudgetButton.setOnClickListener {
            saveBudget()
        }
    }

    private fun backupUserData() {
        try {
            // Get all transactions
            val transactions = transactionManager.getTransactions()
            
            // Get budget data
            val monthlyBudget = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getFloat("monthly_budget", 0f)

            // Create backup data object
            val backupData = JsonObject().apply {
                addProperty("backupDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
                addProperty("userName", "John Doe") // Sample user name
                add("transactions", Gson().toJsonTree(transactions))
                addProperty("monthlyBudget", monthlyBudget) // Add budget to backup
                // Add any other settings data here
                addProperty("preferredCurrency", "$") // Sample currency
            }

            // Convert to JSON string
            val jsonString = Gson().toJson(backupData)

            // Save to internal storage
            val file = File(requireContext().filesDir, "budgetly_backup.json")
            FileWriter(file).use { it.write(jsonString) }

            // Save backup date
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                .putLong("last_backup_time", System.currentTimeMillis())
                .apply()

            updateLastBackupDate()
            Toast.makeText(context, "Backup completed successfully", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreUserData() {
        try {
            // Read from internal storage
            val file = File(requireContext().filesDir, "budgetly_backup.json")
            if (!file.exists()) {
                Toast.makeText(context, "No backup file found", Toast.LENGTH_SHORT).show()
                return
            }

            val jsonString = FileReader(file).use { it.readText() }
            val backupData = Gson().fromJson(jsonString, JsonObject::class.java)

            // Restore transactions
            val transactionsArray = backupData.getAsJsonArray("transactions")
            val transactions = Gson().fromJson(transactionsArray, Array<Transaction>::class.java).toList()
            
            // Clear existing transactions and add restored ones
            transactionManager.clearAllTransactions()
            transactions.forEach { transactionManager.saveTransaction(it) }

            // Restore budget
            val monthlyBudget = backupData.get("monthlyBudget").asFloat
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                .edit()
                .putFloat("monthly_budget", monthlyBudget)
                .apply()

            // Update budget input field
            budgetEditText.setText(String.format("%.2f", monthlyBudget))

            // Restore other settings here if needed
            val preferredCurrency = backupData.get("preferredCurrency").asString
            // Save preferred currency to settings...

            Toast.makeText(context, "Restore completed successfully", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLastBackupDate() {
        val lastBackupTime = requireContext().getSharedPreferences("settings", 0)
            .getLong("last_backup_time", 0)

        if (lastBackupTime > 0) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val dateString = dateFormat.format(Date(lastBackupTime))
            lastBackupText.text = "Last backup: $dateString"
        } else {
            lastBackupText.text = "No backup yet"
        }
    }

    private fun saveBudget() {
        val budgetText = budgetEditText.text.toString()
        if (budgetText.isNotEmpty()) {
            try {
                val budget = budgetText.toDouble()
                requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .edit()
                    .putFloat("monthly_budget", budget.toFloat())
                    .apply()
                Toast.makeText(context, "Budget saved successfully", Toast.LENGTH_SHORT).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSavedBudget() {
        val budget = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getFloat("monthly_budget", 0f)
        if (budget > 0) {
            budgetEditText.setText(String.format("%.2f", budget))
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Settings.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Settings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
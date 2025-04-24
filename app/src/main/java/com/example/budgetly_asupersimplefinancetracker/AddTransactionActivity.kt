package com.example.budgetly_asupersimplefinancetracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var typeToggleGroup: MaterialButtonToggleGroup
    private lateinit var amountEditText: TextInputEditText
    private lateinit var titleEditText: TextInputEditText
    private lateinit var categoryDropdown: AutoCompleteTextView
    private lateinit var dateEditText: TextInputEditText
    private lateinit var addTransactionButton: MaterialButton
    private lateinit var transactionManager: TransactionManager

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        transactionManager = TransactionManager(this)
        initializeViews()
        setupToolbar()
        setupCategoryDropdown()
        setupDatePicker()
        setupTypeToggle()
        setupAddTransactionButton()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        typeToggleGroup = findViewById(R.id.transaction_type_toggle)
        amountEditText = findViewById(R.id.amount_edit_text)
        titleEditText = findViewById(R.id.title_edit_text)
        categoryDropdown = findViewById(R.id.category_dropdown)
        dateEditText = findViewById(R.id.date_edit_text)
        addTransactionButton = findViewById(R.id.add_transaction_button)

        // Set current date as default
        dateEditText.setText(dateFormatter.format(calendar.time))
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupAddTransactionButton() {
        addTransactionButton.setOnClickListener {
            saveTransaction()
        }
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf(
            "Food & Drink",
            "Transportation",
            "Housing",
            "Personal Care",
            "Shopping",
            "Health Care",
            "Salary",
            "Investment"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categoryDropdown.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        dateEditText.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupTypeToggle() {
        typeToggleGroup.check(R.id.expense_button)
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dateEditText.setText(dateFormatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveTransaction() {
        // Validate inputs
        val amount = amountEditText.text.toString()
        val title = titleEditText.text.toString()
        val category = categoryDropdown.text.toString()

        if (amount.isEmpty() || title.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val transaction = Transaction(
                title = title,
                amount = amount.toDouble(),
                date = dateFormatter.format(calendar.time),
                category = category,
                isExpense = typeToggleGroup.checkedButtonId == R.id.expense_button
            )

            transactionManager.saveTransaction(transaction)
            Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
        }
    }
} 
package com.example.budgetly_asupersimplefinancetracker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TransactionManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("transactions", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val transactionsKey = "transactions_list"

    fun saveTransaction(transaction: Transaction) {
        val transactions = getTransactions().toMutableList()
        transactions.add(transaction)
        saveTransactions(transactions)
    }

    fun getTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(transactionsKey, "[]")
        val type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun deleteTransaction(transactionId: String){
        val transactions = getTransactions().toMutableList()
        transactions.removeAll { it.id == transactionId }
        saveTransactions(transactions)
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(transactionsKey, json).apply()
    }
} 
package com.example.budgetly_asupersimplefinancetracker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TransactionManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("transactions", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTransaction(transaction: Transaction) {
        val transactions = getTransactions(transaction.userEmail).toMutableList()
        transactions.add(transaction)
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString("transactions_${transaction.userEmail}", json).apply()
    }

    fun getTransactions(userEmail: String): List<Transaction> {
        val json = sharedPreferences.getString("transactions_${userEmail}", "[]")
        val type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(json, type)
    }

    fun deleteTransaction(transactionId: String, userEmail: String) {
        val transactions = getTransactions(userEmail).toMutableList()
        transactions.removeIf { it.id == transactionId }
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString("transactions_${userEmail}", json).apply()
    }

    fun clearAllTransactions(userEmail: String) {
        sharedPreferences.edit().remove("transactions_${userEmail}").apply()
    }
} 
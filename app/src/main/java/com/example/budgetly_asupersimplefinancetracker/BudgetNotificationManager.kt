package com.example.budgetly_asupersimplefinancetracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

class BudgetNotificationManager(private val context: Context) {
    private val channelId = "budget_alerts"
    private val notificationId = 1
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    private val currentMonth = monthFormat.format(Date())
    private val TAG = "BudgetNotificationManager"
    private val prefs: SharedPreferences = context.getSharedPreferences("notification_state", Context.MODE_PRIVATE)
    private val userEmail = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        .getString("user_email", "") ?: ""

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Budget Alerts"
            val descriptionText = "Notifications for budget-related alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "Notification permission check: $hasPermission")
            if (!hasPermission) {
                Toast.makeText(context, "Please enable notifications in app settings to receive budget alerts", Toast.LENGTH_LONG).show()
            }
            hasPermission
        } else {
            true // Permission is not required for Android versions below 13
        }
    }

    private fun getLastNotificationState(): String {
        return prefs.getString("last_notification_state", "") ?: ""
    }

    private fun saveLastNotificationState(state: String) {
        prefs.edit().putString("last_notification_state", state).apply()
    }

    fun checkAndNotifyBudgetStatus(transactionManager: TransactionManager) {
        if (!hasNotificationPermission()) {
            Log.d(TAG, "No notification permission, skipping check")
            return
        }

        val monthlyBudget = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getFloat("monthly_budget", 0f)
        
        Log.d(TAG, "Monthly budget: $monthlyBudget")
        
        if (monthlyBudget <= 0f) {
            Log.d(TAG, "No budget set, skipping check")
            return
        }

        val transactions = transactionManager.getTransactions(userEmail)
        Log.d(TAG, "Total transactions count: ${transactions.size}")

        val currentMonthTransactions = transactions.filter { 
            try {
                val transactionDate = dateFormat.parse(it.date)
                val transactionMonth = monthFormat.format(transactionDate)
                Log.d(TAG, "Comparing dates - Transaction: $transactionMonth, Current: $currentMonth")
                transactionMonth == currentMonth && it.isExpense
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing date: ${it.date}", e)
                false
            }
        }
        
        Log.d(TAG, "Current month transactions count: ${currentMonthTransactions.size}")
        
        val totalExpenses = currentMonthTransactions.sumOf { it.amount }
        val remainingBudget = monthlyBudget - totalExpenses

        Log.d(TAG, "Total expenses: $totalExpenses, Remaining budget: $remainingBudget")

        // Determine current state
        val currentState = when {
            remainingBudget <= 0 -> "EXCEEDED"
            remainingBudget <= monthlyBudget * 0.2 -> "WARNING"
            else -> "NORMAL"
        }

        // Get last notification state
        val lastState = getLastNotificationState()

        // Only send notification if state has changed
        if (currentState != lastState) {
            when (currentState) {
                "EXCEEDED" -> {
                    Log.d(TAG, "Budget exceeded, sending notification")
                    sendBudgetExceededNotification(monthlyBudget, totalExpenses)
                    saveLastNotificationState("EXCEEDED")
                }
                "WARNING" -> {
                    Log.d(TAG, "Budget warning threshold reached, sending notification")
                    sendBudgetWarningNotification(monthlyBudget, totalExpenses, remainingBudget)
                    saveLastNotificationState("WARNING")
                }
                else -> {
                    Log.d(TAG, "Budget status normal, no notification needed")
                    saveLastNotificationState("NORMAL")
                }
            }
        } else {
            Log.d(TAG, "Budget status unchanged, skipping notification")
        }
    }

    private fun sendBudgetExceededNotification(monthlyBudget: Float, totalExpenses: Double) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_money)
            .setContentTitle("Budget Exceeded!")
            .setContentText("You have exceeded your monthly budget of $${String.format("%.2f", monthlyBudget)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Changed to HIGH
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 250, 500)) // Add vibration pattern
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            Log.d(TAG, "Budget exceeded notification sent successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to send budget exceeded notification", e)
        }
    }

    private fun sendBudgetWarningNotification(monthlyBudget: Float, totalExpenses: Double, remainingBudget: Double) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_money)
            .setContentTitle("Budget Warning")
            .setContentText("You have used ${String.format("%.0f", (totalExpenses/monthlyBudget)*100)}% of your budget. Only $${String.format("%.2f", remainingBudget)} remaining.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Changed to HIGH
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 250, 500)) // Add vibration pattern
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            Log.d(TAG, "Budget warning notification sent successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to send budget warning notification", e)
        }
    }
} 
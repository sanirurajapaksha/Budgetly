package com.example.budgetly_asupersimplefinancetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class OnboardingAdapter(
    private val activity: OnboardingActivity,
    private val onGetStartedClick: () -> Unit
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    private val layouts = listOf(
        R.layout.onboarding_screen1,
        R.layout.onboarding_screen2,
        R.layout.onboarding_screen3
    )

    private var lastPageView: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        if (viewType == R.layout.onboarding_screen3) {
            lastPageView = view
        }
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        if (position == layouts.size - 1) {
            // Last page - set up Get Started button
            holder.itemView.findViewById<Button>(R.id.getStartedButton)?.setOnClickListener {
                onGetStartedClick()
            }
        }
    }

    override fun getItemCount(): Int = layouts.size

    override fun getItemViewType(position: Int): Int = layouts[position]

    fun getLastPageView(): View? = lastPageView

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
} 
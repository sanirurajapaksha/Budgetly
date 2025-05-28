# Budgetly - A Super Simple Finance Tracker

Budgetly is a mobile application designed to help users easily track their income and expenses. It provides a straightforward way to manage personal finances, understand spending habits, and visualize financial data through simple analytics.

## Key Features

*   **User Authentication:** Secure sign-up and login functionality.
*   **Transaction Management:** Easily add, view, and delete income and expense transactions.
*   **Categorization:** Assign categories to transactions for better organization (e.g., Food, Transport, Salary).
*   **Financial Analytics:**
    *   **Spending by Category:** Visualize spending distribution with a pie chart.
    *   **Monthly Trends:** Track spending patterns over time with a bar chart.
    *   **Income vs. Expense:** Clearly see total income, total expenses, and calculate the savings rate.
*   **User Onboarding:** A simple walkthrough for new users to get started.
*   **Notifications:** Receive reminders or alerts related to your budget (Details on specific notification triggers can be expanded here).

## Kotlin Skills Demonstrated

This project showcases proficiency in Kotlin and Android development:

*   **Android Application Architecture:**
    *   Built upon a single-activity architecture using `Activity` and `Fragment` components (from AndroidX, part of Jetpack) for managing different screens and UI flows.
    *   Utilizes Jetpack Navigation component (`NavController`, `NavHostFragment`) for robust in-app navigation.
    *   User Interface (UI) is primarily designed using **XML layouts**, with Kotlin used for UI logic and event handling.
    *   Employs `SharedPreferences` for local data persistence (with GSON for object serialization).
*   **Kotlin Language Features:**
    *   **Conciseness and Readability:** Kotlin's syntax reduces boilerplate, evident in UI event listeners, data manipulation, and overall code structure.
    *   **Null Safety:** Utilizes Kotlin's null safety (`?.`, `?:`) to prevent null pointer exceptions.
    *   **Data Classes:** (Presumed for `Transaction.kt` etc.) for concise data-holding classes.
    *   **Higher-Order Functions & Lambdas:** Applied for collection operations (e.g., `filter`, `mapValues`, `sumOf` in `Analytics.kt`), promoting a functional approach to data processing.
    *   **Activity Result APIs:** Modern handling of permissions and activity results via `registerForActivityResult`.
    *   (Mention **Extension Functions** if specific examples are known or can be generically referenced e.g. "Potential for use of Extension Functions to further simplify common Android tasks.").
*   **Asynchronous Programming:**
    *   The notification permission request is handled asynchronously. Current data operations with SharedPreferences are synchronous, but the codebase is structured to potentially incorporate Kotlin Coroutines for future enhancements like database access or network calls.
*   **Third-Party Library Integration:**
    *   Effective use of **MPAndroidChart** for dynamic charts.
    *   Integration of **GSON** for JSON serialization/deserialization.
*   **UI Logic and Interaction:**
    *   Complex UI interactions and data display (e.g., `BottomNavigationView`, dynamic chart updates) are managed efficiently using Kotlin, complementing the XML-defined layouts.

## Screenshots

*(It is highly recommended to add screenshots of the application here. For example:*

*   *Login/Sign-up Screen*
*   *Main Transaction List Screen*
*   *Add/Edit Transaction Screen*
*   *Analytics Screen (Pie Chart, Bar Chart)*
*   *Onboarding Flow)*

## Libraries Used

*   **Kotlin Standard Library:** Core Kotlin functionalities.
*   **MPAndroidChart:** For displaying charts in the Analytics section.
*   **GSON:** For serializing and deserializing Kotlin objects to/from JSON.
*   **Material Components for Android:** For modern UI elements like `BottomNavigationView`.

## How to Build

1.  Clone the repository:
    ```bash
    git clone https://github.com/sanirurajapaksha/Budgetly.git
    ```
2.  Open the project in Android Studio.
3.  Let Android Studio sync and download the necessary Gradle dependencies.
4.  Build the project (`Build > Make Project`).
5.  Run the application on an Android emulator or a physical device (`Run > Run 'app'`).

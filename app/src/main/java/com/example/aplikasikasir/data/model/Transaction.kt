package com.example.aplikasikasir.data.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: String, // income or expense
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String,
    @SerializedName("reference")
    val reference: String?,
    @SerializedName("transaction_date")
    val transactionDate: String
)

data class TransactionSummary(
    @SerializedName("total_income")
    val totalIncome: Double,
    @SerializedName("total_expense")
    val totalExpense: Double,
    @SerializedName("net_cashflow")
    val netCashflow: Double
)

data class TransactionResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("summary")
    val summary: TransactionSummary?,
    @SerializedName("data")
    val data: List<Transaction>
)

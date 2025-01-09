package com.example.udhari.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

//
//@Entity(tableName = "pending_transactions")
//data class PendingTransaction(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Unique identifier for each transaction
//    val name: String, // Name of the person
//    val amount: Double, // Transaction amount, use Double for precision
//    val date: String, // Date of transaction in ISO format (e.g., "YYYY-MM-DD")
//    val type: TransactionType // Type of transaction (Owe or Collect)
//)


@Entity(
    tableName = "pending_transactions",
    foreignKeys = [
        ForeignKey(
            entity = FinanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["entityId"],
            onDelete = ForeignKey.CASCADE // Delete all transactions if the entity is deleted
        ),
        ForeignKey(
            entity = FinanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteBookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PendingTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Unique transaction ID
    val entityId: Int, // ID of the associated entity
    val noteBookId: Int,
    val description: String, // Description of the transaction
    val amount: Double, // Transaction amount
    val date: String, // Transaction date
    val type: TransactionType // Enum for type (e.g., OWE, COLLECT)
)



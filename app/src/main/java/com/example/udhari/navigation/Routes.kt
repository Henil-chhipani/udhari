package com.example.udhari.navigation


sealed class Routes(val route: String) {
    object NoteBook : Routes("noteBook")
    object AddingNoteBook : Routes("addingNoteBook")
    object NoteBookDetails : Routes("noteBookDetails/{noteBookId}") {
        fun createRoute(noteBookId: Int) = "noteBookDetails/$noteBookId"
    }

    object AddEntity : Routes("addEntity/{noteBookId}") {
        fun createRoute(noteBookId: Int) = "addEntity/$noteBookId"
    }

    object EntityDetails : Routes("entityDetails/{noteBookId}/{entityId}") {
        fun createRoute(noteBookId: Int, entityId: Int) = "entityDetails/$noteBookId/$entityId"
    }

    object TransactionForm : Routes("transactionForm/{noteBookId}/{entityId}") {
        fun createRoute(noteBookId: Int, entityId: Int) = "transactionForm/$noteBookId/$entityId"
    }
}
package com.example.udhari.navigation

import com.example.udhari.data.entity.NoteBookEntity


sealed class Routes(val route: String) {
    object NoteBook : Routes("noteBook")
    object AddingNoteBook : Routes("addingNoteBook")
    object UpdateNoteBook : Routes("updateNoteBook/{noteBookId}"){
        fun createRoute(noteBookId: Int) = "updateNoteBook/$noteBookId"
    }
    object About : Routes("about")
object Guide : Routes("guide")
    object VoiceFeatureGuide : Routes("voiceFeatureGuide")

    object NoteBookDetails : Routes("noteBookDetails/{noteBookId}") {
        fun createRoute(noteBookId: Int) = "noteBookDetails/$noteBookId"
    }

    object AddEntity : Routes("addEntity/{noteBookId}") {
        fun createRoute(noteBookId: Int) = "addEntity/$noteBookId"
    }

    object EntityDetails : Routes("entityDetails/{noteBookId}/{entityId}") {
        fun createRoute(noteBookId: Int, entityId: Int) = "entityDetails/$noteBookId/$entityId"
    }

    object UpdateEntity : Routes("updateEntity/{noteBookId}/{entityId}") {
        fun createRoute(noteBookId: Int, entityId: Int) = "updateEntity/$noteBookId/$entityId"
    }

    object TransactionForm : Routes("transactionForm/{noteBookId}/{entityId}") {
        fun createRoute(noteBookId: Int, entityId: Int) = "transactionForm/$noteBookId/$entityId"
    }
}
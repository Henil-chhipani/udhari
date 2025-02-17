package com.example.udhari.ui.entityDetails

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.TransactionType
import com.example.udhari.data.repositories.FinanceRepository
import com.example.udhari.navigation.Routes
import com.example.udhari.ui.base.BaseViewModel
import com.example.udhari.utils.SpeechRecognitionHelper
import com.example.udhari.utils.ToastManager
import com.example.udhari.utils.elseCaseHandling
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EntityDetailsViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val speechRecognitionHelper: SpeechRecognitionHelper,
) : BaseViewModel(speechRecognitionHelper) {
    private val _uiState = MutableStateFlow(EntityDetailsUiState())
    val uiState = _uiState.asStateFlow()


    fun onEvent(event: EntityDetailsUiEvent) {
        when (event) {
            is EntityDetailsUiEvent.AddId -> addId(
                entityId = event.entityId,
                noteBookId = event.noteBookId
            )

            is EntityDetailsUiEvent.DeleteTransaction -> deleteTransaction(event.transaction)
            EntityDetailsUiEvent.StartVoiceRecognition -> startVoiceRecognition()
            EntityDetailsUiEvent.StopVoiceRecognition -> stopVoiceRecognition()
            is EntityDetailsUiEvent.SetGlobalNavController -> setGlobalNavHostController(event.navController)
            is EntityDetailsUiEvent.NavigateUpdateEntity -> navigateToUpdateEntity()
            is EntityDetailsUiEvent.InitiateCall -> initiateCall(event.context)
            is EntityDetailsUiEvent.CallButtonEnable -> callButtonEnable(event.boolean)
        }
    }

    private fun callButtonEnable(boolean: Boolean) {
        _uiState.value = _uiState.value.copy(callButtonEnable = boolean)
    }

    fun addId(entityId: Int, noteBookId: Int) {
        _uiState.value = _uiState.value.copy(entityId = entityId)
        _uiState.value = _uiState.value.copy(noteBookId = noteBookId)
        fetchEntity()
        fetchPendingTransaction()
    }

    private fun setGlobalNavHostController(navController: NavHostController) {
        _uiState.value = _uiState.value.copy(globalNavController = navController)
    }

    private fun navigateToUpdateEntity() {
        _uiState.value.globalNavController?.navigate(
            Routes.UpdateEntity.createRoute(
                entityId = _uiState.value.entityId,
                noteBookId = _uiState.value.noteBookId
            )
        )
    }

    private fun initiateCall(context: Context) {
        // Get the phone number
        val phoneNumber = _uiState.value.entity.phoneNumber

        if (phoneNumber.isNullOrBlank()) {
            Log.d("Phone number", "Phone number is invalid or not available")
            return
        }
        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(callIntent)

    }


    private fun fetchEntity() {
        viewModelScope.launch {
            val entityId = _uiState.value.entityId
            if (entityId == null) {
                // Handle the case where entityId is not set
                _uiState.value = _uiState.value.copy(
                    entity = FinanceEntity(
                        noteBookId = 0,
                        name = "not available",
                        phoneNumber = "not available"
                    )
                )
                return@launch
            }

            try {
                val entity = repository.getEntityById(entityId)
                if (entity != null) {
                    _uiState.value = _uiState.value.copy(entity = entity)
                } else {
                    // Handle the case where the entity is not found
                    _uiState.value = _uiState.value.copy(
                        entity = FinanceEntity(
                            noteBookId = -1,
                            name = "not available",
                            phoneNumber = "not available"
                        )
                    )
                    // Optionally, show an error message to the user
                }
            } catch (e: Exception) {
                // Handle exceptions (e.g., network errors, database errors)
                Log.e("EntityViewModel", "Error fetching entity", e)
                _uiState.value = _uiState.value.copy(
                    entity = FinanceEntity(
                        noteBookId = 0,
                        name = "not available",
                        phoneNumber = "not available"
                    )
                )
            }
        }
    }

    private fun fetchPendingTransaction() {
        viewModelScope.launch {
            val entityId = _uiState.value.entityId
            if (entityId == null) {
                // Handle the case where entityId is not set
                _uiState.value = _uiState.value.copy(listOfPendingTransaction = emptyList())
                return@launch
            }

            try {
                val transactions = repository.getTransactionsByEntityId(entityId)
                _uiState.update { state ->
                    if (transactions.isEmpty()) {
                        state.copy(
                            isListEmpty = true,
                            listOfPendingTransaction = emptyList()
                        )
                    } else {
                        val totalAmount = calculateTotals(transactions)
                        state.copy(
                            isListEmpty = false,
                            listOfPendingTransaction = transactions,
                            totalAmount = totalAmount
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(listOfPendingTransaction = emptyList())
            }
        }
    }

    private fun deleteTransaction(transaction: PendingTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
            repository.deleteTransactionById(transaction.id)
                ToastManager.showToast("Transaction deleted",true)
            fetchPendingTransaction()

            }catch (e:Exception){
                ToastManager.showToast("Failed to Transaction", false)
                Log.d("deleteTransaction",e.message.toString())
            }
        }
    }

    fun calculateTotals(transactions: List<PendingTransaction>): Double {
        val totalToGive = transactions
            .filter { it.type == TransactionType.OWE } // Filter transactions to give
            .sumOf { it.amount } // Sum their amounts

        val totalToCollect = transactions
            .filter { it.type == TransactionType.COLLECT } // Filter transactions to collect
            .sumOf { it.amount } // Sum their amounts

        val totalAmount = totalToCollect - totalToGive

        return totalAmount
    }

    override fun handleVoiceCommand(command: String) {
        when {
            command.contains("Add Transaction", ignoreCase = true) -> {
                _uiState.value.globalNavController?.navigate(
                    Routes.TransactionForm.createRoute(
                        _uiState.value.noteBookId,
                        _uiState.value.entityId
                    )
                )
                onEvent(EntityDetailsUiEvent.StopVoiceRecognition)
            }

            command.contains("back", ignoreCase = true)->{
                _uiState.value.globalNavController?.popBackStack()
                onEvent(EntityDetailsUiEvent.StopVoiceRecognition)
            }

            command.contains("Create Transaction", ignoreCase = true) -> {
                _uiState.value.globalNavController?.navigate(
                    Routes.TransactionForm.createRoute(
                        _uiState.value.noteBookId,
                        _uiState.value.entityId
                    )
                )
                onEvent(EntityDetailsUiEvent.StopVoiceRecognition)
            }

            else -> {
                elseCaseHandling("Command not recognized") {
                    onEvent(EntityDetailsUiEvent.StopVoiceRecognition)
                }
            }
        }
    }

    override fun startVoiceRecognition() {
        super.startVoiceRecognition()
        _uiState.value = _uiState.value.copy(isVoiceRecognitionStart = true)
    }

    override fun stopVoiceRecognition() {
        super.stopVoiceRecognition()
        _uiState.value = _uiState.value.copy(isVoiceRecognitionStart = false)
    }

}


data class EntityDetailsUiState(
    val noteBookId: Int = -1,
    val entityId: Int = -1,
    val entity: FinanceEntity = FinanceEntity(noteBookId = 0, name = "", phoneNumber = ""),
    val listOfPendingTransaction: List<PendingTransaction> = emptyList(),
    val isListEmpty: Boolean = false,
    val totalAmount: Double = 0.0,
    val resultMessage: String = "",
    val isVoiceRecognitionStart: Boolean = false,
    val globalNavController: NavHostController? = null,
    val callButtonEnable: Boolean = true,
)

sealed class EntityDetailsUiEvent {
    data class AddId(val entityId: Int, val noteBookId: Int) : EntityDetailsUiEvent()
    data class SetGlobalNavController(val navController: NavHostController) :
        EntityDetailsUiEvent()

    data class DeleteTransaction(val transaction: PendingTransaction) : EntityDetailsUiEvent()
    data object StartVoiceRecognition : EntityDetailsUiEvent()
    data object StopVoiceRecognition : EntityDetailsUiEvent()
    data object NavigateUpdateEntity : EntityDetailsUiEvent()
    data class InitiateCall(var context: Context) : EntityDetailsUiEvent()

    data class CallButtonEnable(val boolean: Boolean) : EntityDetailsUiEvent()
}
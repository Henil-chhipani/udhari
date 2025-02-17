package com.example.udhari.ui.entityDetails.transactionForm

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.TransactionType
import com.example.udhari.data.repositories.FinanceRepository
import com.example.udhari.ui.base.BaseViewModel
import com.example.udhari.ui.entityDetails.EntityDetailsUiEvent
import com.example.udhari.ui.notebook.addingNoteBook.AddingNoteBookUiEvent
import com.example.udhari.utils.SpeechRecognitionHelper
import com.example.udhari.utils.ToastManager
import com.example.udhari.utils.elseCaseHandling
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    val repository: FinanceRepository,
    private val speechRecognitionHelper: SpeechRecognitionHelper,
) : BaseViewModel(speechRecognitionHelper) {
    private val _uiState = MutableStateFlow(TransactionFormUiState())
    val uiState = _uiState.asStateFlow()

    init {
        speechRecognitionHelper.updateHandler { command ->
            handleVoiceCommand(command)
        }
    }

    fun onEvent(event: TransactionFormEvent) {
        when (event) {
            is TransactionFormEvent.SetId -> setId(
                entityId = event.entityId,
                noteBookId = event.noteBookId
            )
            is TransactionFormEvent.AmountChanged -> amountChange(event.amount)
            is TransactionFormEvent.DescriptionChanged -> descriptionChange(event.description)
            is TransactionFormEvent.TypeChange -> typeChange(event.type)
            TransactionFormEvent.GetTodayDate -> getTodayDate()
            TransactionFormEvent.InsertTransaction -> insertTransaction()
            TransactionFormEvent.StartVoiceRecognition -> startVoiceRecognition()
            TransactionFormEvent.StopVoiceRecognition -> stopVoiceRecognition()
            is TransactionFormEvent.SetGlobalNavController -> setGlobalNavController(event.navController)
        }
    }



    private fun amountChange(amount: String) {
        // Trim leading and trailing spaces
        val trimmedAmount = amount.trim()

        // If amount is empty, reset and set error
        if (trimmedAmount.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                amount = "",
                isAmountError = true,
                amountError = "Amount cannot be empty"
            )
            return
        }

        // Allow only digits and a single decimal point
        var sanitizedAmount = trimmedAmount.filterIndexed { index, char ->
            char.isDigit() || (char == '.' && trimmedAmount.indexOf('.') == index)
        }

        // Ensure the first character is not just a '.' by prefixing '0' if needed
        if (sanitizedAmount.startsWith(".")) {
            sanitizedAmount = "0$sanitizedAmount"
        }

        // Validate amount format: Must be a valid decimal or integer
        val isValidAmount = sanitizedAmount.matches(Regex("^\\d+(\\.\\d{0,2})?$"))

        // If invalid, set error state
        if (!isValidAmount) {
            _uiState.value = _uiState.value.copy(
                amount = sanitizedAmount,
                isAmountError = true,
                amountError = "Enter a valid amount (up to 2 decimal places)"
            )
            return
        }

        // Ensure the value is greater than zero
        val numericAmount = sanitizedAmount.toDoubleOrNull()
        if (numericAmount == null || numericAmount <= 0) {
            _uiState.value = _uiState.value.copy(
                amount = sanitizedAmount,
                isAmountError = true,
                amountError = "Amount must be greater than zero"
            )
            return
        }

        // Update state with valid amount
        _uiState.value = _uiState.value.copy(
            amount = sanitizedAmount,
            isAmountError = false,
            amountError = ""
        )
    }



    private fun setGlobalNavController(navController: NavHostController) {
        _uiState.value = _uiState.value.copy(globalNavController = navController)
    }


    private fun setId(entityId: Int, noteBookId: Int) {
        _uiState.value = _uiState.value.copy(
            entityId = entityId,
            noteBookId = noteBookId
        )
        Log.e("ids", "${_uiState.value.entityId}, ${_uiState.value.noteBookId}")
    }

    private fun descriptionChange(description: String) {
        // Trim leading and trailing spaces
        val trimmedDescription = description.trim()

        // Check if the description is empty
        if (trimmedDescription.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                description = "",
                isDescriptionError = true,
                descriptionError = "Description cannot be empty"
            )
            return
        }

        // Validate length (Example: Ensure it's at least 3 characters)
        if (trimmedDescription.length < 3) {
            _uiState.value = _uiState.value.copy(
                description = description,
                isDescriptionError = true,
                descriptionError = "Description must be at least 3 characters long"
            )
            return
        }

        // Update state with valid description
        _uiState.value = _uiState.value.copy(
            description = description,
            isDescriptionError = false,
            descriptionError = ""
        )
    }


    private fun typeChange(type: TransactionType?) {
        if (type == null) {
            _uiState.value = _uiState.value.copy(
                type = null,
                isTypeError = true,
                typeError = "Transaction type is required"
            )
            return
        }
        // Update state with valid type
        _uiState.value = _uiState.value.copy(
            type = type,
            isTypeError = false,
            typeError = ""
        )
    }

    @SuppressLint("NewApi")
    fun getTodayDate(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Format as needed
        val date = today.format(formatter)
        _uiState.value = _uiState.value.copy(date = date)
        return date
    }


    private fun insertTransaction() {
        viewModelScope.launch {
            // Check if notebookId is valid
            if (_uiState.value.noteBookId == -1 || _uiState.value.entityId == -1) {
                ToastManager.showToast("Failed to create transaction: Notebook ID is not set", isSuccess = false)
                Log.e("InsertTransaction", "NoteBook ID is not set properly")
                return@launch
            }

            // Validate input fields
            when {
                _uiState.value.amount.isEmpty() -> {
                    _uiState.update {
                        it.copy(
                            isAmountError = true,
                            amountError = "Amount cannot be empty",
                        )
                    }
                    return@launch
                }

                _uiState.value.amount.toDoubleOrNull() == null || _uiState.value.amount.toDouble() <= 0 -> {
                    _uiState.update {
                        it.copy(
                            isAmountError = true,
                            amountError = "Enter a valid amount greater than zero"
                        )
                    }
                    return@launch
                }

                _uiState.value.description.isEmpty() -> {
                    _uiState.update {
                        it.copy(
                            isDescriptionError = true,
                            descriptionError = "Description cannot be empty",
                        )
                    }
                    return@launch
                }

                _uiState.value.type == null -> {
                    _uiState.update {
                        it.copy(
                            isTypeError = true,
                            typeError = "Transaction type is required"
                        )
                    }
                    return@launch
                }

                else -> {
                    try {
                        val result = repository.insertTransaction(
                            PendingTransaction(
                                noteBookId = _uiState.value.noteBookId,
                                entityId = _uiState.value.entityId,
                                amount = _uiState.value.amount.toDouble(),
                                description = _uiState.value.description,
                                date = _uiState.value.date,
                                type = _uiState.value.type!!
                            )
                        )

                        // Reset form state after successful transaction
                        _uiState.update {
                            it.copy(
                                amount = "",
                                description = "",
                                type = null,
                                isAmountError = false,
                                isDescriptionError = false,
                                isTypeError = false
                            )
                        }

                        ToastManager.showToast("Transaction added successfully", isSuccess = true)
                    } catch (e: Exception) {
                        ToastManager.showToast("Failed to create transaction", isSuccess = false)
                        Log.e("InsertTransaction", "Error inserting transaction: ${e.message}")
                    }
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


    override fun handleVoiceCommand(command: String) {
        when {
            command.contains("amount") -> {
                val amount = command.split("amount")[1].trim()
                onEvent(TransactionFormEvent.AmountChanged(amount))
                onEvent(TransactionFormEvent.StopVoiceRecognition)
            }

            command.contains("description") -> {
                val description = command.split("description")[1].trim()
                onEvent(TransactionFormEvent.DescriptionChanged(description))
                onEvent(TransactionFormEvent.StopVoiceRecognition)
            }

//            command.contains("type") -> {
//                val type = command.split("type")[1].trim()
//                _uiState.value = _uiState.value.copy(type = TransactionType.valueOf(type))
//                stopVoiceRecognition()
//            }

            command.contains("create") -> {
                onEvent(TransactionFormEvent.InsertTransaction)
                onEvent(TransactionFormEvent.StopVoiceRecognition)
            }

            command.contains("insert") -> {
                onEvent(TransactionFormEvent.InsertTransaction)
                onEvent(TransactionFormEvent.StopVoiceRecognition)
            }

            command.contains("back", ignoreCase = true) -> {
                _uiState.value.globalNavController?.popBackStack()
                onEvent(TransactionFormEvent.StopVoiceRecognition)
            }

            else -> {
                elseCaseHandling("Command not recognized") {
                   onEvent(TransactionFormEvent.StopVoiceRecognition)
                }
            }
        }
    }

}

sealed class TransactionFormEvent {
    data class SetId(val entityId: Int, val noteBookId: Int) : TransactionFormEvent()
    data class AmountChanged(val amount: String) : TransactionFormEvent()
    data class DescriptionChanged(val description: String) : TransactionFormEvent()
    data class TypeChange(val type: TransactionType) : TransactionFormEvent()
    data object GetTodayDate : TransactionFormEvent()
    data object InsertTransaction : TransactionFormEvent()
    data object StartVoiceRecognition : TransactionFormEvent()
    data object StopVoiceRecognition : TransactionFormEvent()
    data class SetGlobalNavController(val navController: NavHostController) : TransactionFormEvent()
}

data class TransactionFormUiState(
    val entityId: Int = -1,
    val noteBookId: Int = -1,
    val isAmountError: Boolean  = false,
    val amount: String = "",
    val amountError: String = "",
    val isDescriptionError: Boolean = false,
    val description: String = "",
    val descriptionError: String = "",
    val date: String = "",
    val isTypeError: Boolean = false,
    val type: TransactionType? = null,
    val typeError: String = "",
    val resultMessage: String = "",
    val isVoiceRecognitionStart: Boolean = false,
    val globalNavController: NavHostController? = null,
)
package me.typeflu.calculator.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class CalculatorViewModel : ViewModel() {

    var displayState by mutableStateOf("0")
        private set

    private var number1: String = ""
    private var number2: String = ""
    private var operation: CalculatorOperation? = null
    private var justCalculated: Boolean = false

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> handleNumberInput(action.number)
            is CalculatorAction.Operation -> handleOperationInput(action.operation)
            CalculatorAction.Calculate -> handleCalculate()
            CalculatorAction.Clear -> handleClear()
            CalculatorAction.Decimal -> handleDecimal()
            CalculatorAction.Delete -> handleDelete()
        }
    }

    private fun handleNumberInput(num: Int) {
        if (justCalculated) {
            displayState = ""
            number1 = ""
            justCalculated = false
        }
        if (displayState == "0" && num == 0) return // Avoid multiple leading zeros
        if (displayState == "0" && num != 0) displayState = "" // Remove leading zero
        
        if (operation == null) {
            if (displayState.length < 12) { // Limit number length
                 displayState += num.toString()
                 number1 = displayState
            }
        } else {
            if (number2.isEmpty() && displayState == number1) displayState = "" // Start typing second number
            if (displayState.length < 12) { // Limit number length
                if (number2.isEmpty() && displayState != number1) displayState = num.toString() // Edge case if display was manually cleared after op
                else displayState += num.toString()
                number2 = displayState
            }
        }
    }

    private fun handleOperationInput(op: CalculatorOperation) {
        if (number1.isNotEmpty() && number2.isNotEmpty()) {
            handleCalculate() // Calculate previous operation first
        }
        if (number1.isNotEmpty()) { // Ensure number1 is set from displayState if it's the first operation
             operation = op
             // Display state will show number1 until number2 is entered
             // So, we don't change displayState here explicitly unless to show the operation (optional)
             justCalculated = false // Allow new number input for number2
        }
    }

    private fun handleCalculate() {
        if (number1.isBlank() || number2.isBlank() || operation == null) return

        val n1 = number1.toDoubleOrNull() ?: return
        val n2 = number2.toDoubleOrNull() ?: return
        var result = 0.0

        result = when (operation) {
            CalculatorOperation.ADD -> n1 + n2
            CalculatorOperation.SUBTRACT -> n1 - n2
            CalculatorOperation.MULTIPLY -> n1 * n2
            CalculatorOperation.DIVIDE -> if (n2 != 0.0) n1 / n2 else Double.NaN // Handle division by zero
            else -> return
        }
        
        displayState = if (result.isNaN()) "Error" 
                       else if (result == result.toLong().toDouble()) result.toLong().toString() // Show as Int if no decimal part
                       else result.toString()
        
        number1 = displayState // Store result for chained calculations
        number2 = ""
        operation = null
        justCalculated = true
    }

    private fun handleClear() {
        displayState = "0"
        number1 = ""
        number2 = ""
        operation = null
        justCalculated = false
    }

    private fun handleDecimal() {
        if (justCalculated) {
            displayState = "0."
            number1 = "0."
            justCalculated = false
            return
        }
        if (!displayState.contains(".")) {
            if (operation == null) {
                 displayState += "."
                 number1 = displayState
            } else {
                if (number2.isEmpty() && displayState == number1) displayState = "0." // Start decimal for second number
                else displayState += "."
                number2 = displayState
            }
        }
    }

    private fun handleDelete() {
        if (justCalculated || displayState == "Error" || displayState == "0") {
            if (displayState != "0") handleClear() // Clear if it's an error or result, else do nothing if already 0
            return
        }

        if (displayState.isNotEmpty()) {
            displayState = displayState.dropLast(1)
            if (displayState.isEmpty()) {
                displayState = "0"
            }
        }
        
        if (operation == null) {
            number1 = displayState
        } else {
            number2 = displayState
            if(number2.isEmpty() && displayState == "0") { // If number2 becomes empty, revert display to number1
                displayState = number1
            }
        }
         if (number1 == "0" && number2.isEmpty() && operation == null) handleClear() // Back to initial state

    }
}

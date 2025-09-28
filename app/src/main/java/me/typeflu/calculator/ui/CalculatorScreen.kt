package me.typeflu.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.typeflu.calculator.ui.theme.CalculatorTheme

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
}

enum class CalculatorOperation {
    ADD, SUBTRACT, MULTIPLY, DIVIDE
}

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = viewModel.displayState, // Use ViewModel state for display
                fontSize = 80.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp),
                maxLines = 1
            )
            CalculatorButtonsLayout(onAction = viewModel::onAction) // Pass action handler
        }
    }
}

@Composable
fun CalculatorButtonsLayout(onAction: (CalculatorAction) -> Unit) {
    val buttonRows = listOf(
        listOf(CalculatorAction.Clear, CalculatorAction.Delete, CalculatorAction.Operation(CalculatorOperation.DIVIDE)),
        listOf(CalculatorAction.Number(7), CalculatorAction.Number(8), CalculatorAction.Number(9), CalculatorAction.Operation(CalculatorOperation.MULTIPLY)),
        listOf(CalculatorAction.Number(4), CalculatorAction.Number(5), CalculatorAction.Number(6), CalculatorAction.Operation(CalculatorOperation.SUBTRACT)),
        listOf(CalculatorAction.Number(1), CalculatorAction.Number(2), CalculatorAction.Number(3), CalculatorAction.Operation(CalculatorOperation.ADD)),
        listOf(CalculatorAction.Number(0), CalculatorAction.Decimal, CalculatorAction.Calculate)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        buttonRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { action ->
                    CalculatorButton(
                        action = action,
                        modifier = Modifier
                            .weight(if (action is CalculatorAction.Number && action.number == 0 || action == CalculatorAction.Calculate) 2f else 1f)
                            .aspectRatio(if (action is CalculatorAction.Number && action.number == 0 || action == CalculatorAction.Calculate) 2f else 1f),
                        onClick = onAction
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    action: CalculatorAction,
    modifier: Modifier = Modifier,
    onClick: (CalculatorAction) -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(getButtonBackgroundColor(action))
            .clickable { onClick(action) }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getButtonSymbol(action),
            fontSize = 32.sp,
            color = getButtonTextColor(action)
        )
    }
}

fun getButtonSymbol(action: CalculatorAction): String {
    return when (action) {
        is CalculatorAction.Number -> action.number.toString()
        is CalculatorAction.Operation -> when (action.operation) {
            CalculatorOperation.ADD -> "+"
            CalculatorOperation.SUBTRACT -> "-"
            CalculatorOperation.MULTIPLY -> "×"
            CalculatorOperation.DIVIDE -> "÷"
        }
        CalculatorAction.Clear -> "AC"
        CalculatorAction.Delete -> "DEL"
        CalculatorAction.Decimal -> "."
        CalculatorAction.Calculate -> "="
    }
}

@Composable
fun getButtonBackgroundColor(action: CalculatorAction): Color {
    return when (action) {
        is CalculatorAction.Number, CalculatorAction.Decimal -> MaterialTheme.colorScheme.secondaryContainer
        is CalculatorAction.Operation -> MaterialTheme.colorScheme.tertiaryContainer
        CalculatorAction.Calculate -> MaterialTheme.colorScheme.primaryContainer
        CalculatorAction.Clear, CalculatorAction.Delete -> MaterialTheme.colorScheme.errorContainer
    }
}

@Composable
fun getButtonTextColor(action: CalculatorAction): Color {
     return when (action) {
        is CalculatorAction.Number, CalculatorAction.Decimal -> MaterialTheme.colorScheme.onSecondaryContainer
        is CalculatorAction.Operation -> MaterialTheme.colorScheme.onTertiaryContainer
        CalculatorAction.Calculate -> MaterialTheme.colorScheme.onPrimaryContainer
        CalculatorAction.Clear, CalculatorAction.Delete -> MaterialTheme.colorScheme.onErrorContainer
    }
}


@Preview(showBackground = true, name = "Calculator Screen Light")
@Composable
fun CalculatorScreenPreviewLight() {
    CalculatorTheme(darkTheme = false) {
        CalculatorScreen()
    }
}

@Preview(showBackground = true, name = "Calculator Screen Dark")
@Composable
fun CalculatorScreenPreviewDark() {
    CalculatorTheme(darkTheme = true) {
        CalculatorScreen()
    }
}

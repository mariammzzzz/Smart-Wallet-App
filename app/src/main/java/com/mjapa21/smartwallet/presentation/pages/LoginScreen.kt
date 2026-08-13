package com.mjapa21.smartwallet.presentation.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onSubmit: (
        name: String,
        cardNumber: String,
        cvv: String,
        expiryDate: String,
        monthlyIncome: String
    ) -> Unit = { _, _, _, _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var monthlyIncome by remember { mutableStateOf("") }

    // Outer column: scrollable fields take all available space, button is pinned below them.
    // imePadding() on this outer container pushes the whole bottom section (the button)
    // up above the keyboard when it's open, instead of letting the keyboard cover it.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {

            Text(text = "Personal Info")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = "Card Info")
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { input ->
                    // we keep digits only, cap at 16 digits (max 16 digit card number)
                    cardNumber = input.filter { it.isDigit() }.take(16)
                },
                label = { Text(text = "Card Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { input ->
                        // store raw digits only (no "/") — the slash is purely a display concern,
                        // handled below by ExpiryDateVisualTransformation
                        expiryDate = input.filter { it.isDigit() }.take(4)
                    },
                    label = { Text(text = "MM/YY") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = ExpiryDateVisualTransformation(),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = { input ->
                        cvv = input.filter { it.isDigit() }.take(4)
                    },
                    label = { Text(text = "CVV") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(text = "Income")
            OutlinedTextField(
                value = monthlyIncome,
                onValueChange = { input ->
                    monthlyIncome = input.filter { it.isDigit() || it == '.' }
                },
                label = { Text(text = "Monthly Income") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // button is pinned outside the scroll — always visible right above the keyboard (or at
        // the bottom of the screen when the keyboard is closed).
        Button(
            onClick = { onSubmit(name, cardNumber, cvv, expiryDate, monthlyIncome) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp)
        ) {
            Text(text = "Continue")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}

/**
 * Displays a raw 4-digit string (e.g. "0408") as "04/08"
 * before this i changed the displayed text inside onValueChange but that messed up the cursor position after / got inserted
 * for example if the user typed 1234, the displayed text would be 12/43 because cursor would not go at the end of the string after typing 3
 * Visual transformation fixes that issue by keeping the raw text as is and only changing how it is displayed to the user, while also keeping track of cursor position
 */
class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(4)

        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                append(char)
                if (index == 1 && digits.length > 2) append('/')
            }
        }

        val offsetMapping = object : OffsetMapping {
            // raw digit index -> index in the displayed "MM/YY" string
            override fun originalToTransformed(offset: Int): Int {
                return if (offset <= 2) offset else offset + 1
            }

            // displayed index -> index in the raw digit string
            override fun transformedToOriginal(offset: Int): Int {
                return if (offset <= 2) offset else offset - 1
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
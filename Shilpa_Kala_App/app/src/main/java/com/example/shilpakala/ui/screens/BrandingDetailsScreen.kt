package com.example.shilpakala.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shilpakala.R
import com.example.shilpakala.model.ProductData

/**
 * Premium Onboarding/Input form for artisan branding details.
 * Optimized with bilingual support, strict validation, and modern UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandingDetailsScreen(
    initial: ProductData,
    isProcessing: Boolean,
    onSubmit: (ProductData) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Form States
    var artisanName by remember { mutableStateOf(initial.artisanName) }
    var village by remember { mutableStateOf(initial.village) }
    var craftType by remember { mutableStateOf(initial.material) }
    var productName by remember { mutableStateOf(initial.productName) }
    var price by remember { mutableStateOf(initial.priceInr) }

    // 2. Interaction Tracking (to show errors only after focus/change)
    var artisanTouched by remember { mutableStateOf(false) }
    var villageTouched by remember { mutableStateOf(false) }
    var craftTouched by remember { mutableStateOf(false) }
    var productTouched by remember { mutableStateOf(false) }
    var priceTouched by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val gold = Color(0xFFD4AF37)
    val shape = RoundedCornerShape(16.dp)

    // 3. Validation Logic for UI Feedback
    val nameError = if (artisanTouched && artisanName.isBlank()) stringResource(R.string.error_name) else null
    val villageError = if (villageTouched && village.isBlank()) stringResource(R.string.error_village) else null
    val craftError = if (craftTouched && craftType.isBlank()) stringResource(R.string.error_craft) else null
    val productError = if (productTouched && productName.isBlank()) stringResource(R.string.error_product) else null
    val priceError = if (priceTouched && (price.isBlank() || !price.all { it.isDigit() })) stringResource(R.string.error_price) else null

    // Live Validation for button state
    val isFormValid = artisanName.isNotBlank() && 
                      village.isNotBlank() && 
                      craftType.isNotBlank() && 
                      productName.isNotBlank() && 
                      price.isNotBlank() && 
                      price.all { it.isDigit() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF080808), Color(0xFF121212))
                )
            )
            .statusBarsPadding()
    ) {
        // Subtle Gold Ambient Glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopEnd)
                .offset(x = 200.dp, y = (-100).dp)
                .background(gold.copy(alpha = 0.05f), RoundedCornerShape(200.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.branding_details),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.heritage_branding),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 1. Your Name (ನಿಮ್ಮ ಹೆಸರು)
            PremiumInputField(
                value = artisanName,
                onValueChange = { artisanName = it; artisanTouched = true },
                label = stringResource(id = R.string.label_artisan_name),
                errorText = nameError,
                enabled = !isProcessing,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                leadingIcon = Icons.Default.Person,
                gold = gold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Village (ಗ್ರಾಮ)
            PremiumInputField(
                value = village,
                onValueChange = { village = it; villageTouched = true },
                label = stringResource(id = R.string.label_village),
                errorText = villageError,
                enabled = !isProcessing,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                leadingIcon = Icons.Default.LocationOn,
                gold = gold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Craft Type (ಕಲೆ)
            PremiumInputField(
                value = craftType,
                onValueChange = { craftType = it; craftTouched = true },
                label = stringResource(id = R.string.label_craft_type),
                errorText = craftError,
                enabled = !isProcessing,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                leadingIcon = Icons.Default.Palette,
                gold = gold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Product Name (ಉತ್ಪನ್ನದ ಹೆಸರು)
            PremiumInputField(
                value = productName,
                onValueChange = { productName = it; productTouched = true },
                label = stringResource(id = R.string.label_product_name),
                errorText = productError,
                enabled = !isProcessing,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                leadingIcon = Icons.Default.Inventory2,
                gold = gold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Product Price (ಉತ್ಪನ್ನದ ಬೆಲೆ)
            PremiumInputField(
                value = price,
                onValueChange = { 
                    val filtered = it.filter { ch -> ch.isDigit() }
                    price = filtered
                    priceTouched = true 
                },
                label = stringResource(id = R.string.label_product_price),
                errorText = priceError,
                enabled = !isProcessing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                leadingIcon = Icons.Default.Payments,
                gold = gold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Premium Action Button
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "buttonScale")

            Button(
                onClick = {
                    if (isFormValid) {
                        onSubmit(
                            ProductData(
                                productName = productName.trim(),
                                material = craftType.trim(),
                                priceInr = price.trim(),
                                artisanName = artisanName.trim(),
                                village = village.trim()
                            )
                        )
                    }
                },
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .scale(animatedScale),
                shape = shape,
                enabled = isFormValid && !isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = gold,
                    contentColor = Color.Black,
                    disabledContainerColor = gold.copy(alpha = 0.2f),
                    disabledContentColor = Color.White.copy(alpha = 0.3f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = stringResource(id = R.string.processing).uppercase(), fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                } else {
                    Text(text = stringResource(id = R.string.get_started).uppercase(), fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Reusable premium input field with bilingual support and error handling.
 */
@Composable
fun PremiumInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorText: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: ImageVector? = null,
    gold: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = errorText != null,
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = if (errorText != null) MaterialTheme.colorScheme.error else gold.copy(alpha = 0.6f)) }
            } else null,
            keyboardOptions = keyboardOptions.copy(imeAction = imeAction),
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = gold,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedLabelColor = gold,
                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = gold,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )
        androidx.compose.animation.AnimatedVisibility(
            visible = errorText != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            errorText?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )
            }
        }
    }
}

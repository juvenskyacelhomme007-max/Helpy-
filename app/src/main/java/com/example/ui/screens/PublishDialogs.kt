package com.example.ui.screens

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppConstants
import com.example.data.model.Business
import com.example.data.model.Product
import com.example.data.model.ServiceItem
import com.example.ui.components.GoogleSignInButton
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.InkSlate
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TerracottaAccent
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.HelpyViewModel

@Composable
fun PublishChooserDialog(
    onDismiss: () -> Unit,
    onChooseProduct: () -> Unit,
    onChooseService: () -> Unit,
    onChooseBusiness: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Publier sur Helpy", fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        onDismiss()
                        onChooseProduct()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("choose_product_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = InkSlate),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vendre un Article / Produit", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onChooseService()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("choose_service_btn"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Icon(Icons.Default.Handyman, contentDescription = null, tint = TerracottaAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Proposer un Service (Artisan, IT, Fret)", color = TextDark, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onChooseBusiness()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("choose_business_btn"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = InkSlate, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enregistrer un Commerce / Entreprise", color = TextDark, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss, border = BorderStroke(1.dp, BorderSubtle)) {
                Text("Annuler", color = TextMuted)
            }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun ImagePickerSection(
    selectedImageUri: String,
    onImageSelected: (String) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelected(uri.toString())
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Photo obligatoire de l'annonce :", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(6.dp))

        if (selectedImageUri.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BorderSubtle)
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Photo sélectionnée",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { onImageSelected("") },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Supprimer", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BorderSubtle)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .testTag("pick_photo_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = TerracottaAccent, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Choisir une photo depuis la galerie", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkSlate)
                    Text("JPG, PNG acceptés", fontSize = 11.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
fun PublishProductDialog(
    viewModel: HelpyViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var selectedCategory by remember { mutableStateOf(AppConstants.PRODUCT_CATEGORIES[1]) }
    var condition by remember { mutableStateOf("Neuf") }
    var isNegotiable by remember { mutableStateOf(true) }
    var hasDelivery by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf("Haïti") }
    var city by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsApp by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf("") }

    var currencyDropdown by remember { mutableStateOf(false) }
    var categoryDropdown by remember { mutableStateOf(false) }
    var countryDropdown by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publier une Annonce Produit", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            if (currentUser == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Connectez-vous d'abord avec Google pour que les acheteurs puissent vous contacter en toute confiance.",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    GoogleSignInButton(
                        isLoading = isAuthLoading,
                        onSignInClicked = {
                            if (context is Activity) {
                                viewModel.signInWithGoogle(context)
                            }
                        }
                    )
                }
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Image Picker
                    ImagePickerSection(
                        selectedImageUri = imageUri,
                        onImageSelected = { imageUri = it }
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de l'annonce (ex : iPhone 15 Pro)") },
                        modifier = Modifier.fillMaxWidth().testTag("product_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (état, accessoires, détails)") },
                        modifier = Modifier.fillMaxWidth().testTag("product_desc_input"),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Prix") },
                            modifier = Modifier.weight(1.2f).testTag("product_price_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Box(modifier = Modifier.weight(0.8f)) {
                            OutlinedButton(
                                onClick = { currencyDropdown = true },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(selectedCurrency, fontWeight = FontWeight.Bold, color = InkSlate)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = InkSlate)
                            }
                            DropdownMenu(
                                expanded = currencyDropdown,
                                onDismissRequest = { currencyDropdown = false }
                            ) {
                                AppConstants.SUPPORTED_CURRENCIES.forEach { curr ->
                                    DropdownMenuItem(
                                        text = { Text(curr) },
                                        onClick = {
                                            selectedCurrency = curr
                                            currencyDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Negotiable and Delivery Checkboxes
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isNegotiable,
                            onCheckedChange = { isNegotiable = it },
                            colors = CheckboxDefaults.colors(checkedColor = TerracottaAccent)
                        )
                        Text("Prix négociable", fontSize = 13.sp, color = TextDark)
                        Spacer(modifier = Modifier.width(12.dp))
                        Checkbox(
                            checked = hasDelivery,
                            onCheckedChange = { hasDelivery = it },
                            colors = CheckboxDefaults.colors(checkedColor = TerracottaAccent)
                        )
                        Text("Livraison possible", fontSize = 13.sp, color = TextDark)
                    }

                    // Category Dropdown
                    Box {
                        OutlinedButton(
                            onClick = { categoryDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text("Catégorie : $selectedCategory", color = TextDark)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = InkSlate)
                        }
                        DropdownMenu(
                            expanded = categoryDropdown,
                            onDismissRequest = { categoryDropdown = false }
                        ) {
                            AppConstants.PRODUCT_CATEGORIES.drop(1).forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Country Dropdown
                    Box {
                        OutlinedButton(
                            onClick = { countryDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text("Pays : $selectedCountry", color = TextDark)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = InkSlate)
                        }
                        DropdownMenu(
                            expanded = countryDropdown,
                            onDismissRequest = { countryDropdown = false }
                        ) {
                            AppConstants.SUPPORTED_COUNTRIES.drop(1).forEach { cnt ->
                                DropdownMenuItem(
                                    text = { Text(cnt) },
                                    onClick = {
                                        selectedCountry = cnt
                                        countryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ville / Commune (ex : Delmas, Miami, Montréal)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = whatsApp,
                        onValueChange = { whatsApp = it },
                        label = { Text("Numéro WhatsApp (avec indicatif pays)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Numéro de Téléphone pour appels directs") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (errorMessage != null) {
                        Text(errorMessage ?: "", color = Color(0xFFDC2626), fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            if (currentUser != null) {
                Button(
                    onClick = {
                        val price = priceText.toDoubleOrNull() ?: 0.0
                        if (title.isBlank() || price <= 0.0) {
                            errorMessage = "Veuillez renseigner un titre et un prix valide."
                            return@Button
                        }
                        isSubmitting = true
                        errorMessage = null
                        val finalImage = if (imageUri.isNotBlank()) imageUri else "android.resource://${context.packageName}/drawable/img_product_phone"
                        viewModel.publishProduct(
                            Product(
                                title = title,
                                description = description,
                                price = price,
                                currency = selectedCurrency,
                                category = selectedCategory,
                                condition = condition,
                                isNegotiable = isNegotiable,
                                hasDelivery = hasDelivery,
                                imageUrl = finalImage,
                                country = selectedCountry,
                                city = city.ifBlank { "N/A" },
                                sellerPhone = phone.ifBlank { whatsApp },
                                sellerWhatsApp = whatsApp.ifBlank { phone }
                            ),
                            onSuccess = {
                                isSubmitting = false
                                onDismiss()
                            },
                            onError = {
                                isSubmitting = false
                                errorMessage = it
                            }
                        )
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("submit_publish_product")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Publier l'Annonce", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, border = BorderStroke(1.dp, BorderSubtle)) { Text("Annuler", color = TextMuted) }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun PublishServiceDialog(
    viewModel: HelpyViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rateText by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var pricingType by remember { mutableStateOf("sur devis") }
    var selectedCategory by remember { mutableStateOf(AppConstants.SERVICE_CATEGORIES[1]) }
    var experienceYearsText by remember { mutableStateOf("3") }
    var selectedCountry by remember { mutableStateOf("Haïti") }
    var city by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsApp by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf("") }

    var categoryDropdown by remember { mutableStateOf(false) }
    var countryDropdown by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Proposer un Service", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            if (currentUser == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Connectez-vous avec Google pour proposer votre service.", fontSize = 14.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                    GoogleSignInButton(
                        isLoading = isAuthLoading,
                        onSignInClicked = { if (context is Activity) viewModel.signInWithGoogle(context) }
                    )
                }
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Image Picker
                    ImagePickerSection(
                        selectedImageUri = imageUri,
                        onImageSelected = { imageUri = it }
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Intitulé du service (ex : Électricien Solaire)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description de vos compétences et interventions") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = rateText,
                            onValueChange = { rateText = it },
                            label = { Text("Tarif de base") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = experienceYearsText,
                            onValueChange = { experienceYearsText = it },
                            label = { Text("Années d'exp.") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Category
                    Box {
                        OutlinedButton(
                            onClick = { categoryDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text("Catégorie : $selectedCategory", color = TextDark)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = InkSlate)
                        }
                        DropdownMenu(expanded = categoryDropdown, onDismissRequest = { categoryDropdown = false }) {
                            AppConstants.SERVICE_CATEGORIES.drop(1).forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = {
                                    selectedCategory = cat
                                    categoryDropdown = false
                                })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ville d'intervention (ex : Port-au-Prince, Miami)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = whatsApp,
                        onValueChange = { whatsApp = it },
                        label = { Text("Numéro WhatsApp") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Numéro de Téléphone") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (errorMessage != null) {
                        Text(errorMessage ?: "", color = Color(0xFFDC2626), fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            if (currentUser != null) {
                Button(
                    onClick = {
                        val rate = rateText.toDoubleOrNull() ?: 0.0
                        val exp = experienceYearsText.toIntOrNull() ?: 2
                        if (title.isBlank()) {
                            errorMessage = "Veuillez renseigner l'intitulé du service."
                            return@Button
                        }
                        isSubmitting = true
                        errorMessage = null
                        val finalImage = if (imageUri.isNotBlank()) imageUri else "android.resource://${context.packageName}/drawable/img_service_solar"
                        viewModel.publishService(
                            ServiceItem(
                                title = title,
                                description = description,
                                rate = rate,
                                currency = selectedCurrency,
                                pricingType = pricingType,
                                category = selectedCategory,
                                experienceYears = exp,
                                imageUrl = finalImage,
                                country = selectedCountry,
                                city = city.ifBlank { "N/A" },
                                providerPhone = phone.ifBlank { whatsApp },
                                providerWhatsApp = whatsApp.ifBlank { phone }
                            ),
                            onSuccess = {
                                isSubmitting = false
                                onDismiss()
                            },
                            onError = {
                                isSubmitting = false
                                errorMessage = it
                            }
                        )
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Publier le Service", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, border = BorderStroke(1.dp, BorderSubtle)) { Text("Annuler", color = TextMuted) }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun PublishBusinessDialog(
    viewModel: HelpyViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("Haïti") }
    var selectedCategory by remember { mutableStateOf(AppConstants.BUSINESS_CATEGORIES[1]) }
    var openingHours by remember { mutableStateOf("Lun - Sam : 08h00 - 18h00") }
    var phone by remember { mutableStateOf("") }
    var whatsApp by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf("") }

    var categoryDropdown by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enregistrer une Entreprise", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            if (currentUser == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Connectez-vous avec Google pour enregistrer un établissement.", fontSize = 14.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                    GoogleSignInButton(
                        isLoading = isAuthLoading,
                        onSignInClicked = { if (context is Activity) viewModel.signInWithGoogle(context) }
                    )
                }
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Image Picker
                    ImagePickerSection(
                        selectedImageUri = imageUri,
                        onImageSelected = { imageUri = it }
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom de l'entreprise ou commerce") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description des produits et services vendus") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Adresse physique (rue, numéro)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ville (ex : Cap-Haïtien, Montréal)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Category
                    Box {
                        OutlinedButton(
                            onClick = { categoryDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text("Catégorie : $selectedCategory", color = TextDark)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = InkSlate)
                        }
                        DropdownMenu(expanded = categoryDropdown, onDismissRequest = { categoryDropdown = false }) {
                            AppConstants.BUSINESS_CATEGORIES.drop(1).forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = {
                                    selectedCategory = cat
                                    categoryDropdown = false
                                })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = openingHours,
                        onValueChange = { openingHours = it },
                        label = { Text("Heures d'ouverture") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = whatsApp,
                        onValueChange = { whatsApp = it },
                        label = { Text("WhatsApp Entreprise") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Numéro Téléphone Fixe ou Mobile") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (errorMessage != null) {
                        Text(errorMessage ?: "", color = Color(0xFFDC2626), fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            if (currentUser != null) {
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Veuillez renseigner le nom de l'entreprise."
                            return@Button
                        }
                        isSubmitting = true
                        errorMessage = null
                        val finalImage = if (imageUri.isNotBlank()) imageUri else "android.resource://${context.packageName}/drawable/img_biz_resto"
                        viewModel.publishBusiness(
                            Business(
                                name = name,
                                description = description,
                                address = address,
                                city = city.ifBlank { "N/A" },
                                country = selectedCountry,
                                category = selectedCategory,
                                openingHours = openingHours,
                                imageUrl = finalImage,
                                phone = phone.ifBlank { whatsApp },
                                whatsApp = whatsApp.ifBlank { phone },
                                verified = true
                            ),
                            onSuccess = {
                                isSubmitting = false
                                onDismiss()
                            },
                            onError = {
                                isSubmitting = false
                                errorMessage = it
                            }
                        )
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Enregistrer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, border = BorderStroke(1.dp, BorderSubtle)) { Text("Annuler", color = TextMuted) }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(18.dp)
    )
}

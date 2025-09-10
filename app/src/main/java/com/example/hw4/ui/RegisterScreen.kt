package com.example.hw4.ui

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hw4.data.api.ApiClient
import com.example.hw4.data.api.ApiService
import com.example.hw4.data.model.LoginRequest
import com.example.hw4.data.model.SignupRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        RegisterContent(
            modifier = Modifier.padding(innerPadding),
            onNavigateToLogin = onNavigateToLogin,
            onRegisterSuccess = onRegisterSuccess,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun RegisterContent(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var registerError by remember { mutableStateOf<String?>(null) }

    var isNameFocused by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var apiService: ApiService = ApiClient.apiService

    fun validateName(text: String): String? {
        if (text.isBlank()) return "Full name cannot be empty"
        return null
    }

    fun validateEmail(text: String): String? {
        if (text.isBlank()) return "Email cannot be empty"
        if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            return "Invalid email format"
        }

        return null
    }

    fun validatePassword(text: String): String? {
        if (text.isBlank()) return "Password cannot be empty"
        return null
    }


    Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (nameError != null) {
                        nameError =
                            validateName(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            keyboardController?.show()
                        } else if (isNameFocused) {
                            nameError = validateName(name)
                        }
                        if (focusState.isFocused) isNameFocused = true
                    },
                label = { Text("Enter full name") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(text = nameError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) {
                        emailError =
                            validateEmail(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            keyboardController?.show()
                        } else if (isEmailFocused) {
                            emailError = validateEmail(email)
                        }
                        if (focusState.isFocused) isEmailFocused = true
                    },
                label = { Text("Enter email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) {
                        passwordError =
                            validatePassword(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            keyboardController?.show()
                        } else if (isPasswordFocused) {
                            passwordError = validatePassword(password)
                        }
                        if (focusState.isFocused) isPasswordFocused = true
                    },
                label = { Text("Enter password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    registerError = null
                    nameError = validateName(name)
                    emailError = validateEmail(email)
                    passwordError = validatePassword(password)

                    if (emailError == null && passwordError == null && nameError == null) {
                        isLoading = true

                        scope.launch {
                            try {
                                val response = apiService.register(
                                    SignupRequest(
                                        name = name,
                                        email    = email,
                                        password = password
                                    )
                                )
                                isLoading = false
                                if (response.isSuccessful) {
                                    snackbarHostState.showSnackbar("Registered successfully")
                                    apiService.login(
                                        LoginRequest(
                                            email = email,
                                            password = password
                                        )
                                    )
                                    onRegisterSuccess()
                                }
                                else {
                                    if (response.code() == 403) {
                                        emailError = "Email already exists"
                                    }
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                Log.d("Register", "Register failed"+e)
                            }

                        }
                    } else {
                        Log.d("RegisterContent", "Validation failed")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current
                    )
                } else {
                    Text("Register")
                }
            }

            if (registerError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = registerError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Login",
                    modifier = Modifier.clickable(onClick = onNavigateToLogin),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

        }
    }
}
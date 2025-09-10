package com.example.hw4.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Login") },
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
        LoginContent(
            modifier = Modifier.padding(innerPadding),
            onNavigateToRegister = onNavigateToRegister,
            onLoginSuccess = onLoginSuccess,
            snackbarHostState= snackbarHostState,
        )
    }
}

@Composable
fun LoginContent(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }


    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var loginError by remember { mutableStateOf<String?>(null) }

    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    var apiService: ApiService = ApiClient.apiService

    fun validateEmail(text: String): String? {
        if (text.isBlank()) return "Email cannot be empty"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
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
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email ),
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
                label = { Text("Password") },
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
                    loginError = null
                    emailError = validateEmail(email)
                    passwordError = validatePassword(password)

                    if (emailError == null && passwordError == null) {
                        isLoading = true

                        scope.launch {
                            try {
                                val loginRes = apiService.login(
                                    LoginRequest(
                                        email = email,
                                        password = password
                                    )
                                )
                                isLoading = false
                                if (loginRes.isSuccessful) {
                                    snackbarHostState.showSnackbar("Logged in successfully")
                                    onLoginSuccess()
                                }
                                else {
                                    emailError = "Username or password is incorrect"
                                }

                            } catch (e: Exception) {
                                isLoading = false
                                Log.d("Login", "Login failed"+e)
                            }
                        }
                    } else {
                        Log.d("LoginContent", "Validation failed")
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
                    Text("Login")
                }
            }

            if (loginError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = loginError!!,
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
                Text("Don't have an account yet?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Register",
                    modifier = Modifier.clickable(onClick = onNavigateToRegister),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
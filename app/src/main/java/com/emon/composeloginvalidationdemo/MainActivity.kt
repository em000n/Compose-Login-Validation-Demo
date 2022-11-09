package com.emon.composeloginvalidationdemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emon.composeloginvalidationdemo.ui.theme.ComposeLoginTheme

class MainActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLoginTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        val stateEvent by viewModel.uiStateEvent.collectAsState("")
        when (stateEvent) {
            is LoginViewModel.LoginUiStateEvent.LoginSuccess -> {
                Toast.makeText(context,
                    "" + (stateEvent as LoginViewModel.LoginUiStateEvent.LoginSuccess).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /* LaunchedEffect(key1 = context) {
             viewModel.uiStateEvent.collect {
                 when (it) {
                     LoginViewModel.LoginUiStateEvent.LoginSuccess -> {
                         Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
                     }
                 }
             }
         }*/

        if (viewModel.uiState.loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                color = colorResource(id = R.color.purple_700),
                strokeWidth = Dp(value = 5F)
            )
        } else {
            OutlinedTextField(
                value = viewModel.state.email,
                leadingIcon = {
                    Icon(
                        Icons.Default.AccountBox,
                        contentDescription = null,
                        tint = Color.Black
                    )
                },
                label = { Text(text = "E-mail") },
                isError = viewModel.state.emailError != null,
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { viewModel.action(LoginViewModel.LoginUiAction.EmailChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
            )
            if (viewModel.state.emailError != null) {
                Text(
                    text = viewModel.state.emailError!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(20.dp))

            //password
            var passwordVisibility: Boolean by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = viewModel.state.password,
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Black
                    )
                },

                trailingIcon = {
                    val visibilityImage = if (passwordVisibility)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            imageVector = visibilityImage,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                },
                label = { Text(text = "Password") },
                // visualTransformation = PasswordVisualTransformation(),
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = viewModel.state.passwordError != null,
                shape = MaterialTheme.shapes.medium,
                onValueChange = { viewModel.action(LoginViewModel.LoginUiAction.PasswordChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
            )
            if (viewModel.state.passwordError != null) {
                Text(
                    text = viewModel.state.passwordError!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.action(LoginViewModel.LoginUiAction.Login)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp), shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Submit")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeLoginTheme {
        LoginScreen(viewModel = LoginViewModel())
    }
}


package com.example.mkulifarm.ui.theme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mkulifarm.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginRegisterScreen(navController = rememberNavController())
        }
    }
}





@Composable
fun LoginRegisterScreen(
    navController: NavController,
    authHelper: FirebaseAuthHelper = FirebaseAuthHelper(isPreview = false)
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(45.dp)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {

        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .paddingFromBaseline(top = 40.dp, bottom = 10.dp)// Optional border
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.farm)) // Adjust with your animation file
            LottieAnimation(
                composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(2400.dp)
                    .padding(50.dp),

            )
        }
        // Customized Tab Row for Login/Register toggle
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF8BC34A),
            contentColor = Color.White
        ) {
            Tab(
                text = { Text("Login", fontWeight = FontWeight.Bold, color = if (selectedTabIndex == 0) Color.White else Color.LightGray) },
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 }
            )
            Tab(
                text = { Text("Register", fontWeight = FontWeight.Bold, color = if (selectedTabIndex == 1) Color.White else Color.LightGray) },
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedTabIndex == 0) {
            LoginForm(
                email = email,
                password = password,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onLoginClick = {
                    authHelper.loginUser(email, password,
                        onSuccess = {  val intent = Intent(context, Dashboard::class.java)
                            context.startActivity(intent)
                            Toast.makeText(
                                context,
                                "Login sucessful welcome back",
                                Toast.LENGTH_SHORT
                            ).show()

                                    },
                        onFailure = { errorMessage = it }
                    )
                },
                errorMessage = errorMessage
            )
        } else {
            RegisterForm(
                name = name,
                email = email,
                password = password,
                onNameChange = { name = it },
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onRegisterClick = {
                    authHelper.registerUser(email, password,
                        onSuccess = { val intent = Intent(context, Dashboard::class.java)
                            context.startActivity(intent)
                            Toast.makeText(
                                context,
                                "Registration succesful Welcome",
                                Toast.LENGTH_SHORT
                            ).show()

                                    },
                        onFailure = { errorMessage = it }
                    )
                },
                errorMessage = errorMessage
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Social login section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Use other methods", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            IconButton(onClick = { /* Facebook login */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",

                )
            }
            IconButton(onClick = { /* Google login */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",

                )
            }
        }
    }
}

@Composable
fun LoginForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    errorMessage: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Already have an Account?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email:") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password:") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp)
                .height(50.dp),

            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", fontSize = 16.sp, color = Color.White)
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun RegisterForm(
    name: String,
    email: String,
    password: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    errorMessage: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Here's your first step with us!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Register", fontSize = 16.sp, color = Color.White)
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun ProfileImagePicker(imageUri: Uri?, onImageSelected: (Uri?) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (imageUri != null) {
            Image(
                painter = rememberImagePainter(imageUri),
                contentDescription = "Profile Image",
                modifier = Modifier.size(100.dp)
            )
        }
        Button(onClick = { /* Logic to pick image */ }) {
            Text("Select Profile Image")
        }
    }
}

class FirebaseAuthHelper(private val isPreview: Boolean = false) {

    private val auth: FirebaseAuth? = if (!isPreview) {
        FirebaseAuth.getInstance()
    } else null

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (isPreview) {
            onSuccess() // Simulate success in preview mode
            return
        }

        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    fun registerUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (isPreview) {
            onSuccess() // Simulate success in preview mode
            return
        }

        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.localizedMessage ?: "Registration failed")
                }
            }
    }
}





@Preview(showBackground = true)
@Composable
fun LoginRegisterScreenPreview() {
    LoginRegisterScreen(
        navController = rememberNavController(),
        authHelper = FirebaseAuthHelper(isPreview = true) // Enable preview mode
    )
}

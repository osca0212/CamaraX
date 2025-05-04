package com.example.camara_custom

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.camara_custom.pantallas.PantallaCamara
import com.example.camara_custom.ui.theme.Camara_customTheme

class MainActivity : ComponentActivity() {
    private val solicitud_permisos_de_camara =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { esta_garantizado ->
            if(esta_garantizado){
                VistaDeLaCamara()
            }
            else {
                // En caso de que el usario diga no, ¿que mostramos?
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (PackageManager.PERMISSION_GRANTED){
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) -> {
                VistaDeLaCamara()
            }
            else -> {
                solicitud_permisos_de_camara.launch(Manifest.permission.CAMERA)
            }
        }

        enableEdgeToEdge()

//        setContent {
//            Camara_customTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
    }

    private fun VistaDeLaCamara(){
        setContent {
            Camara_customTheme {
                Surface() {
                    PantallaCamara()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Camara_customTheme {
        Greeting("Android")
    }
}
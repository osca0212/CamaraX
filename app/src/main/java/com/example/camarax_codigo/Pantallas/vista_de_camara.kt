package com.example.camara_custom.pantallas

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun PantallaCamara() {
    val contexto = LocalContext.current
    val cicloDeVida = LocalLifecycleOwner.current

    val lenteState = remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
    val filtroActivado = remember { mutableStateOf(true) }

    val previa = Preview.Builder().build()
    val vistaPrevia = remember { PreviewView(contexto) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(lenteState.value) {
        val selector = CameraSelector.Builder()
            .requireLensFacing(lenteState.value)
            .build()

        val proveedorCamara = contexto.obtenerProveedorDeCamara()
        proveedorCamara.unbindAll()
        proveedorCamara.bindToLifecycle(
            cicloDeVida,
            selector,
            previa,
            imageCapture
        )
        previa.setSurfaceProvider(vistaPrevia.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        AndroidView(factory = { vistaPrevia }, modifier = Modifier.fillMaxSize())

        // Capa de filtro (gris semitransparente)
        if (filtroActivado.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
            )
        }

        // Botones en fila
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                lenteState.value =
                    if (lenteState.value == CameraSelector.LENS_FACING_FRONT)
                        CameraSelector.LENS_FACING_BACK
                    else
                        CameraSelector.LENS_FACING_FRONT
            }) {
                Text("Cambiar Cámara")
            }

            Button(onClick = {
                tomar_foto(imageCapture, contexto)
            }) {
                Text("Tomar Foto")
            }

            Button(onClick = {
                filtroActivado.value = !filtroActivado.value
            }) {
                Text(if (filtroActivado.value) "Filtro Off" else "Filtro On")
            }
        }
    }
}

private suspend fun Context.obtenerProveedorDeCamara(): ProcessCameraProvider =
    suspendCoroutine { continuacion ->
        ProcessCameraProvider.getInstance(this).also { proveedorCamara ->
            proveedorCamara.addListener({
                continuacion.resume(proveedorCamara.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

private fun tomar_foto(capturadorImagen: ImageCapture, contexto: Context) {
    val nombreArchivo = "CapturaFoto.jpeg"

    val valoresDelContenido = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/nuestra_app")
        }
    }

    val salidaFoto = ImageCapture.OutputFileOptions.Builder(
        contexto.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        valoresDelContenido
    ).build()

    capturadorImagen.takePicture(
        salidaFoto,
        ContextCompat.getMainExecutor(contexto),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.v("CAPTURA_EXITO", "Éxito, la foto fue guardada correctamente")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.v("CAPTURA_ERROR", "Error al guardar la foto: ${exception.message}")
            }
        }
    )
}

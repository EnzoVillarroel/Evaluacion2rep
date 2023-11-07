package com.evillarroel.evaluacion2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.evillarroel.evaluacion2.db.AppDatabase
import com.evillarroel.evaluacion2.db.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearProductoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CrearProductoUI(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearProductoUI(context: CrearProductoActivity) {
    var nombreProducto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nombreProducto,
            onValueChange = { nombreProducto = it },
            label = { Text(text = stringResource(id = R.string.texto_nombre_producto)) },
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                if (nombreProducto.isNotBlank()) {
                    context.lifecycleScope.launch {
                        guardarProducto(nombreProducto, context)
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.texto_boton_guardar))
        }
    }
}

suspend fun guardarProducto(nombre: String, context: CrearProductoActivity) {
    val database = AppDatabase.getInstance(context.applicationContext)
    val producto = Producto(producto = nombre, comprado = false)

    withContext(Dispatchers.IO) {
        val productoId = database.productoDao().insertar(producto)

        if (productoId > 0) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }
}
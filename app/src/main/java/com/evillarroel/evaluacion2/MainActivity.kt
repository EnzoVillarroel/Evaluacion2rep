package com.evillarroel.evaluacion2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.evillarroel.evaluacion2.db.AppDatabase
import com.evillarroel.evaluacion2.db.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch( Dispatchers.IO ) {
            val productoDao = AppDatabase.getInstance(this@MainActivity).productoDao()
            val cantiRegistros = productoDao.contar()
        }

        setContent {
            PantallaPrincipalUI()
        }
    }
}

@Composable
fun PantallaPrincipalUI() {
    val contexto = LocalContext.current
    val (productos, setProductos) = remember { mutableStateOf(emptyList<Producto>()) }

    LaunchedEffect(productos) {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(contexto).productoDao()
            setProductos(dao.findAll())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (productos.isEmpty()) {
            Text(
                text = "No hay productos registrados",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                items(productos) { producto ->
                    ProductoItemUI(producto) {
                        setProductos(emptyList<Producto>())
                    }
                }
            }
        }

        Button(
            onClick = {
                val intent = Intent(contexto, CrearProductoActivity::class.java)
                contexto.startActivity(intent)
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
        ) {

            Text(text = stringResource(id = R.string.texto_boton))
        }
    }
}

@Composable
fun ProductoItemUI(producto: Producto, onSave:() -> Unit = {}) {
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        if( producto.comprado ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Tarea realizada",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch( Dispatchers.IO ) {
                        val dao = AppDatabase.getInstance( contexto ).productoDao()
                        producto.comprado = false
                        dao.actualizar( producto )
                        onSave()
                    }
                }
            )
        } else {
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Tarea por hacer",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch( Dispatchers.IO ) {
                        val dao = AppDatabase.getInstance( contexto ).productoDao()
                        producto.comprado = true
                        dao.actualizar( producto )
                        onSave()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = producto.producto,
            modifier = Modifier.weight(2f)
        )
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar tarea",
            modifier = Modifier.clickable {
                alcanceCorrutina.launch( Dispatchers.IO ) {
                    val dao = AppDatabase.getInstance( contexto ).productoDao()
                    dao.eliminar( producto )
                    onSave()
                }
            },
            tint = Color.Red
        )
    }
}
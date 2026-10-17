package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Producto de ejemplo (datos ficticios, solo en memoria mientras la app está abierta)
private data class ProductoEjemplo(
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val disponible: Boolean = true
)

@Composable
fun ManageProductsScreen() {
    val productos = remember {
        mutableStateListOf(
            ProductoEjemplo("Kit de Bienestar", "Set de aromaterapia con roll-on y bruma.", 24990),
            ProductoEjemplo("Roll-on Calma", "Aceites esenciales para momentos de estrés.", 7990),
            ProductoEjemplo("Bruma de Almohada", "Bruma relajante para el descanso nocturno.", 9990),
            ProductoEjemplo("Roll-on Foco", "Aroma para concentración y estudio.", 7990, false)
        )
    }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Panel de Gestión",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text("Productos Sanaesencia", style = MaterialTheme.typography.headlineSmall)
            }
            Button(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuevo")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Línea propia de productos naturales: kits, roll-ons y brumas (datos de ejemplo).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(productos) { index, producto ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(producto.descripcion, style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Precio: ${producto.precio} CLP",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Switch(
                                checked = producto.disponible,
                                onCheckedChange = { productos[index] = producto.copy(disponible = it) }
                            )
                            Text(
                                if (producto.disponible) "Disponible" else "Agotado",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        var nombre by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }
        val precioNum = precio.toIntOrNull()

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nuevo producto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it.filter { c -> c.isDigit() } },
                        label = { Text("Precio (CLP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        productos.add(
                            ProductoEjemplo(nombre.trim(), descripcion.trim(), precioNum ?: 0)
                        )
                        mostrarDialogo = false
                    },
                    enabled = nombre.trim().length >= 3 && precioNum != null
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }
}

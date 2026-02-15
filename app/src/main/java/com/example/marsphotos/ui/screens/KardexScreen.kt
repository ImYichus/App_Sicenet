package com.example.marsphotos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.marsphotos.model.KardexItem

@Composable
fun KardexScreen(
    kardexList: List<KardexItem>,
    modifier: Modifier = Modifier
) {
    if (kardexList.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Cargando Kardex o lista vacía...")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Kardex Académico", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(kardexList) { materia ->
                KardexItemRow(materia)
            }
        }
    }
}

@Composable
fun KardexItemRow(item: KardexItem) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            // Materia (asumimos que siempre trae nombre)
            Text(
                text = item.materia,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Convertimos el Int a String explícitamente
                Text("Calif: ${item.calificacion}")

                // Manejamos el posible nulo con el operador elvis (?:)
                Text("Sem: ${item.semestre ?: "N/A"}")
            }

            // Manejamos posibles nulos en periodo y año
            Text(
                text = "Periodo: ${item.periodo ?: ""} ${item.anio ?: ""}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
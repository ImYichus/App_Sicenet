package com.example.marsphotos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marsphotos.model.KardexItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(
    kardexList: List<KardexItem>,
    modifier: Modifier = Modifier
) {
    // 1. Extraemos los semestres únicos y los ordenamos
    val semestresDisponibles = remember(kardexList) {
        kardexList.mapNotNull { it.semestre }.distinct().sortedBy { it.toIntOrNull() ?: 0 }
    }

    // 2. Estado para saber qué semestre está seleccionado (null significa "Todos")
    var semestreSeleccionado by remember { mutableStateOf<String?>(null) }

    // 3. Filtrar la lista según la elección
    val listaFiltrada = if (semestreSeleccionado == null) {
        kardexList
    } else {
        kardexList.filter { it.semestre == semestreSeleccionado }
    }

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        // Título
        Text(
            text = "Filtro por Semestre",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF4A2C5D),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // Selector horizontal de semestres (Chips)
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = semestreSeleccionado == null,
                    onClick = { semestreSeleccionado = null },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4A2C5D),
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(semestresDisponibles) { semestre ->
                FilterChip(
                    selected = semestreSeleccionado == semestre,
                    onClick = { semestreSeleccionado = semestre },
                    label = { Text("Sem $semestre") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4A2C5D),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Divider(color = Color(0xFFF3E5F5), thickness = 1.dp)

        // Lista de materias
        if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay materias en este semestre", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listaFiltrada) { materia ->
                    KardexItemRow(materia)
                }
            }
        }
    }
}

@Composable
fun KardexItemRow(item: KardexItem) {
    val esAprobada = item.calificacion >= 70

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.materia,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2C5D)
                )
                Text(
                    text = "${item.periodo ?: ""} ${item.anio ?: ""}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Calificación con color dinámico
            Surface(
                color = if (esAprobada) Color(0xFF81C784) else Color(0xFFE57373),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(width = 50.dp, height = 35.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = item.calificacion.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
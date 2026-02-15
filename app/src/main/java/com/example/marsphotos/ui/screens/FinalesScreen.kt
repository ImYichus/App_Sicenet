package com.example.marsphotos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marsphotos.model.CalificacionFinal

// Color Morado de tu paleta (ajusta el Hex si es necesario)
val MoradoPrincipal = Color(0xFF4A2C5D)
val MoradoSuave = Color(0xFFF3E5F5)

@Composable
fun FinalesScreen(
    finales: List<CalificacionFinal>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Calificaciones Finales",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MoradoPrincipal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(finales) { item ->
                MateriaCard(item)
            }
        }
    }
}

@Composable
fun MateriaCard(item: CalificacionFinal) {
    // Protección contra nulos (Si es nulo, ponemos "0" o texto vacío)
    val calif = item.calificacion ?: "0"
    val esReprobatoria = calif == "0" || calif.toIntOrNull()?.let { it < 70 } ?: false

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MoradoSuave),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.materia ?: "Materia Desconocida",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoradoPrincipal
                )
                Text(
                    text = item.acreditacion ?: "Pendiente",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // Badge de Calificación
            Surface(
                color = if (esReprobatoria) Color(0xFFE57373) else MoradoPrincipal,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(width = 60.dp, height = 40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = calif,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
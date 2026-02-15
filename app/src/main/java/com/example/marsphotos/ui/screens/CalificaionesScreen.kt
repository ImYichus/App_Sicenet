package com.example.marsphotos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marsphotos.model.CalificacionParcial

// BORRAMOS LAS VARIABLES DE COLOR DE AQUÍ PORQUE YA ESTÁN EN EL OTRO ARCHIVO
// Si te marca error de que no las encuentra, quítales el "private" en FinalesScreen.kt

@Composable
fun CalificaionesScreen(
    calificaciones: List<CalificacionParcial>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Calificaciones Parciales",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MoradoPrincipal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(calificaciones) { materia ->
                MateriaExpandibleCard(materia)
            }
        }
    }
}

@Composable
fun MateriaExpandibleCard(materia: CalificacionParcial) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MoradoPrincipal),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = materia.materia ?: "Sin nombre",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .background(MoradoSuave)
                        .padding(12.dp)
                ) {
                    val unidades = listOf(
                        "Unidad 1" to (materia.p1 ?: "0"),
                        "Unidad 2" to (materia.p2 ?: "0"),
                        "Unidad 3" to (materia.p3 ?: "0"),
                        "Unidad 4" to (materia.p4 ?: "0"),
                        "Unidad 5" to (materia.p5 ?: "0"),
                        "Unidad 6" to (materia.p6 ?: "0")
                    )

                    unidades.forEach { (label, calif) ->
                        UnidadRow(label, calif)
                    }
                }
            }
        }
    }
}

@Composable
fun UnidadRow(label: String, calif: String) {
    val valorNumerico = calif.toIntOrNull() ?: 0
    val esReprobado = valorNumerico < 70 || calif == "0"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MoradoPrincipal,
            fontWeight = FontWeight.Medium
        )

        Surface(
            color = if (esReprobado) Color(0xFFE57373) else Color(0xFF81C784),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = calif,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
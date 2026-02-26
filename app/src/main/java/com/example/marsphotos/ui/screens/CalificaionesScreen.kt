package com.example.marsphotos.ui.screens

import ads_mobile_sdk.p10
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

@Composable
fun CalificaionesScreen(
    calificaciones: List<CalificacionParcial>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        if (calificaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No hay calificaciones disponibles", color = Color.Gray)
            }
        } else {
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
                    text = materia.materia,
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
                    // --- LÓGICA DINÁMICA ---
                    // Creamos una lista de pares, pero filtramos las que vienen nulas o vacías de SICENET
                    val todasLasUnidades = listOf(
                        "U1" to materia.p1, "U2" to materia.p2, "U3" to materia.p3,
                        "U4" to materia.p4, "U5" to materia.p5, "U6" to materia.p6,
                        "U7" to materia.p7, "U8" to materia.p8, "U9" to materia.p9,
                        "U10" to materia.p10, "U11" to materia.p11, "U12" to materia.p12
                    )

                    val unidadesFiltradas = todasLasUnidades.filter { !it.second.isNullOrBlank() }

                    if (unidadesFiltradas.isEmpty()) {
                        Text(
                            text = "Aún no hay unidades evaluadas",
                            color = MoradoPrincipal,
                            modifier = Modifier.padding(8.dp),
                            fontSize = 14.sp
                        )
                    } else {
                        unidadesFiltradas.forEach { (label, calif) ->
                            UnidadRow(label, calif!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnidadRow(label: String, calif: String) {
    // Limpiamos espacios y validamos si es número
    val califLimpia = calif.trim()
    val valorNumerico = califLimpia.toIntOrNull() ?: 0
    val esReprobado = valorNumerico < 70

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
                text = califLimpia,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
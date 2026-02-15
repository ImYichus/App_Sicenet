package com.example.marsphotos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.marsphotos.model.MateriaCarga

@Composable
fun CargaAcademicaScreen(
    carga: List<MateriaCarga>,
    modifier: Modifier = Modifier
) {
    // Quitamos "SAB" para evitar el error de referencia inexistente
    val dias = listOf("LUN", "MAR", "MIE", "JUE", "VIE")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val colorPrimario = Color(0xFF4A2C5D)

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = colorPrimario,
            contentColor = Color.White,
            edgePadding = 16.dp,
            divider = {}
        ) {
            dias.forEachIndexed { index, dia ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(dia, color = Color.White, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Text(
            text = "Actividades del ${dias[selectedTabIndex]}",
            style = MaterialTheme.typography.titleMedium,
            color = colorPrimario,
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.ExtraBold
        )

        // Filtrado usando solo los campos que existen en tu modelo
        val materiasFiltradas = when (selectedTabIndex) {
            0 -> carga.filter { it.lunes.isNotEmpty() }
            1 -> carga.filter { it.martes.isNotEmpty() }
            2 -> carga.filter { it.miercoles.isNotEmpty() }
            3 -> carga.filter { it.jueves.isNotEmpty() }
            4 -> carga.filter { it.viernes.isNotEmpty() }
            else -> emptyList()
        }

        if (materiasFiltradas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay clases este día", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(materiasFiltradas) { materia ->
                    CargaCard(materia, selectedTabIndex)
                }
            }
        }
    }
}

@Composable
fun CargaCard(materia: MateriaCarga, diaSeleccionado: Int) {
    val colorPrimario = Color(0xFF4A2C5D)
    val colorFondoCard = Color(0xFFF3E5F5)

    val infoDelDia = when (diaSeleccionado) {
        0 -> materia.lunes
        1 -> materia.martes
        2 -> materia.miercoles
        3 -> materia.jueves
        4 -> materia.viernes
        else -> ""
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondoCard),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = materia.materia,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorPrimario
            )

            Text(text = materia.docente, fontSize = 13.sp, color = Color.DarkGray)

            // Extracción segura del horario y aula
            val horario = infoDelDia.substringBefore(" Aula:").trim()
            val aula = if (infoDelDia.contains("Aula:")) {
                infoDelDia.substringAfter("Aula: ").trim()
            } else {
                "N/A"
            }

            Text(
                text = horario,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Aula: $aula",
                fontSize = 14.sp,
                color = colorPrimario,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
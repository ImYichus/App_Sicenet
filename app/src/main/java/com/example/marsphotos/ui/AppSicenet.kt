package com.example.marsphotos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.marsphotos.ui.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSicenet(modifier: Modifier = Modifier) {
    val snViewModel: SNViewModel = viewModel(factory = SNViewModel.Factory)
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val uiState = snViewModel.snUiState

    val drawerContent = @Composable {
        ModalDrawerSheet {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "MENÚ SICENET",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Divider()

            // 1. MI PERFIL
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Mi Perfil") },
                selected = currentRoute == "profile",
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("profile")
                }
            )

            // 2. CARGA ACADÉMICA (Añadido)
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                label = { Text("Carga Académica") },
                selected = currentRoute == "carga",
                onClick = {
                    scope.launch { drawerState.close() }
                    snViewModel.consultarCargaAcademica() // Debes tener esta función en tu ViewModel
                    navController.navigate("carga")
                }
            )

            // 3. KARDEX
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = { Text("Mi Kardex") },
                selected = currentRoute == "kardex",
                onClick = {
                    scope.launch { drawerState.close() }
                    snViewModel.consultarKardex()
                    navController.navigate("kardex")
                }
            )

            // 4. CALIFICACIONES PARCIALES
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                label = { Text("Calificaciones Parciales") },
                selected = currentRoute == "parciales",
                onClick = {
                    scope.launch { drawerState.close() }
                    snViewModel.consultarCalificacionesUnidades()
                    navController.navigate("parciales")
                }
            )

            // 5. CALIFICACIONES FINALES
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                label = { Text("Calificaciones Finales") },
                selected = currentRoute == "finales",
                onClick = {
                    scope.launch { drawerState.close() }
                    snViewModel.consultarCalificacionesFinales()
                    navController.navigate("finales")
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            Divider()

            // CERRAR SESIÓN
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                label = { Text("Cerrar Sesión") },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    snViewModel.logout(context)
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }

    if (uiState is SNUiState.Success) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = drawerContent
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("SICENET") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                ContenidoNavegacion(navController, snViewModel, uiState, Modifier.padding(paddingValues))
            }
        }
    } else {
        Scaffold { paddingValues ->
            ContenidoNavegacion(navController, snViewModel, uiState, Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun ContenidoNavegacion(
    navController: androidx.navigation.NavHostController,
    snViewModel: SNViewModel,
    uiState: SNUiState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            HomeScreen(
                snUiState = uiState,
                onLoginClick = { mat, pass, tipo -> snViewModel.loginYConsultarPerfil(mat, pass, tipo) },
                onKardexClick = {},
                onLogoutClick = {}
            )
            LaunchedEffect(uiState) {
                if (uiState is SNUiState.Success) {
                    navController.navigate("profile") { popUpTo("login") { inclusive = true } }
                }
            }
        }

        composable("profile") {
            if (uiState is SNUiState.Success) {
                ProfileScreen(student = uiState.data)
            }
        }

        // PANTALLA CARGA ACADÉMICA (Añadido)
        composable("carga") {
            if (uiState is SNUiState.Success) {
                CargaAcademicaScreen(carga = uiState.cargaAcademica)
            }
        }

        composable("kardex") {
            if (uiState is SNUiState.Success) {
                KardexScreen(kardexList = uiState.kardex)
            }
        }

        composable("parciales") {
            if (uiState is SNUiState.Success) {
                CalificaionesScreen(calificaciones = uiState.califUnidades)
            }
        }

        composable("finales") {
            if (uiState is SNUiState.Success) {
                FinalesScreen(finales = uiState.califFinales)
            }
        }
    }
}
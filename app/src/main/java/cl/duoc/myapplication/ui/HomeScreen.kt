package cl.duoc.myapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.myapplication.R
import cl.duoc.myapplication.navigation.Screen
import cl.duoc.myapplication.ui.theme.BlueDuoc
import cl.duoc.myapplication.ui.theme.MyApplicationTheme
import cl.duoc.myapplication.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {

    Box() {

        Image(
            painterResource(R.drawable.images),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1F))

            LogoApp()

            Button(
                onClick = {}
            ) {
                Text("Iniciar Sesión")
            }

            Button(
                onClick = { viewModel.navigateTo(Screen.Register) }
            ) {
                Text("Registrarse")
            }

            Spacer(modifier = Modifier.weight(1F))
            Text("Todos los derechos reservados",
                modifier = Modifier.padding(20.dp))
        }
    }
}

@Composable
fun LogoApp() {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Logo App",
        modifier = Modifier.padding(40.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen(viewModel = viewModel())
    }
}
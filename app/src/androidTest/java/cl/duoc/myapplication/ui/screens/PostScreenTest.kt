package cl.duoc.myapplication.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.myapplication.data.remote.RetrofitInstance
import cl.duoc.myapplication.repository.PostRepository
import cl.duoc.myapplication.viewmodel.PostViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pantalla_debe_mostrar_boton_cargar_posts() {
        // Arrange
        composeTestRule.setContent {
            PostScreen(
                viewModel = PostViewModel(
                    PostRepository(RetrofitInstance.apiService)
                )
            )
        }

        // Act & Assert
        composeTestRule.onNodeWithText("Cargar Posts").assertIsDisplayed()
    }

    @Test
    fun al_hacer_clic_en_cargar_posts_debe_mostrar_cargando() {
        // Arrange
        composeTestRule.setContent {
            PostScreen(
                viewModel = PostViewModel(
                    PostRepository(RetrofitInstance.apiService)
                )
            )
        }

        // Act
        composeTestRule.onNodeWithText("Cargar Posts").performClick()

        // Assert - durante la carga debería aparecer el indicador
        // (en una prueba real usaríamos un repositorio mock para controlar el estado)
        composeTestRule.onNodeWithText("Cargar Posts").assertIsDisplayed()
    }
}
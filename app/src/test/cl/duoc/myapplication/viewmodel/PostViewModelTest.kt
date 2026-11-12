package cl.duoc.myapplication.viewmodel

import cl.duoc.myapplication.data.model.Post
import cl.duoc.myapplication.repository.PostRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runTest
import kotlin.test.Test

class PostViewModelTest : StringSpec({

    "debería cargar posts correctamente cuando el repositorio los retorna" {
        runTest {
            // Arrange
            val mockRepository = mockk<PostRepository>()
            val posts = listOf(
                Post(userId = 1, id = 1, title = "Título 1", body = "Cuerpo 1"),
                Post(userId = 1, id = 2, title = "Título 2", body = "Cuerpo 2")
            )
            coEvery { mockRepository.getPosts() } returns posts

            val viewModel = PostViewModel(mockRepository)

            // Act
            viewModel.loadPosts()

            // Assert - esperar a que el StateFlow emita el valor final
            val state = viewModel.uiState.first()

            state.posts shouldBe posts
            state.isLoading shouldBe false
            state.error shouldBe null
        }
    }

    "debería mostrar error cuando el repositorio falla" {
        runTest {
            // Arrange
            val mockRepository = mockk<PostRepository>()
            coEvery { mockRepository.getPosts() } throws RuntimeException("Sin conexión")

            val viewModel = PostViewModel(mockRepository)

            // Act
            viewModel.loadPosts()

            // Assert
            val state = viewModel.uiState.first()

            state.posts shouldBe emptyList()
            state.isLoading shouldBe false
            state.error shouldBe "Sin conexión"
        }
    }
})
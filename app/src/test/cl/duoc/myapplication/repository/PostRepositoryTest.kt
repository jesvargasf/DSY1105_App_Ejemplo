package cl.duoc.myapplication.repository

import cl.duoc.myapplication.data.model.Post
import cl.duoc.myapplication.data.remote.ApiService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runTest
import kotlin.test.Test

class PostRepositoryTest : StringSpec({

    "delegar getPosts al servicio de API" {
        runTest {
            // Arrange
            val mockApi = mockk<ApiService>()
            val expected = listOf(Post(userId = 1, id = 1, title = "T", body = "B"))
            coEvery { mockApi.getPosts() } returns expected

            val repository = PostRepository(mockApi)

            // Act
            val result = repository.getPosts()

            // Assert
            result shouldBe expected
        }
    }

    "delegar getPostById al servicio de API" {
        runTest {
            // Arrange
            val mockApi = mockk<ApiService>()
            val expected = Post(userId = 1, id = 42, title = "Post 42", body = "Cuerpo")
            coEvery { mockApi.getPostById(42) } returns expected

            val repository = PostRepository(mockApi)

            // Act
            val result = repository.getPostById(42)

            // Assert
            result.id shouldBe 42
            result.title shouldBe "Post 42"
        }
    }
})
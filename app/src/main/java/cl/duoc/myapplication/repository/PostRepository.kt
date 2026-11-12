package cl.duoc.myapplication.repository

import cl.duoc.myapplication.data.model.Post
import cl.duoc.myapplication.data.remote.ApiService
import cl.duoc.myapplication.data.remote.RetrofitInstance

class PostRepository(private val apiService: ApiService = RetrofitInstance.apiService) {

    suspend fun getPosts(): List<Post> = apiService.getPosts()

    suspend fun getPostById(postId: Int): Post = apiService.getPostById(postId)

    suspend fun createPost(post: Post): Post = apiService.createPost(post)
}
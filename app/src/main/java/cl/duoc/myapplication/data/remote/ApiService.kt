package cl.duoc.myapplication.data.remote

import cl.duoc.myapplication.data.model.Post
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface ApiService {

    // GET https://jsonplaceholder.typicode.com/posts
    @GET("posts")
    suspend fun getPosts(): List<Post>

    // GET https://jsonplaceholder.typicode.com/posts/{id}
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") postId: Int): Post

    // POST https://jsonplaceholder.typicode.com/posts
    @POST("posts")
    suspend fun createPost(@Body post: Post): Post
}
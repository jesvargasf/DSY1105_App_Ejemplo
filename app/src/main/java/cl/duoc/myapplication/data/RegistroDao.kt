package cl.duoc.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {
    @Insert
    suspend fun insertar(registro: Registro)

    @Query("SELECT * FROM registros ORDER BY creadoEn DESC")
    fun observar(): Flow<List<Registro>>

    @Query("DELETE FROM registros WHERE id = :id")
    suspend fun borrar(id: Long)
}

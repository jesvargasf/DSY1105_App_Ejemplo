package cl.duoc.myapplication.model

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: RegistroErrores = RegistroErrores(),
    val guardando: Boolean = false,
    val guardado: Boolean = false
)

data class RegistroErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val aceptaTerminos: String? = null
)

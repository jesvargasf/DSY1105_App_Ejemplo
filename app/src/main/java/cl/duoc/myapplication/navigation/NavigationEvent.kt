package cl.duoc.myapplication.navigation

sealed class NavigationEvent {
    data class NavigateTo(val route: Screen) : NavigationEvent()
    data object PopBackStack : NavigationEvent()
}

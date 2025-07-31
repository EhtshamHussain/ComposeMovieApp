package com.example.movieapp.Screens

sealed class Screens(val screen : String){
    data object Home : Screens("home")
    data object Location : Screens("location")
    data object Ticket : Screens("ticket")
    data object Menu : Screens("Menu")
    data object Person : Screens("person")
}

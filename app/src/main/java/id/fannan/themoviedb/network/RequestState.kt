package id.fannan.themoviedb.network

sealed class RequestState<out F> private constructor() {
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val message:String):RequestState<Nothing>()
    object Loading:RequestState<Nothing>()
}
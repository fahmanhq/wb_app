package android.template.core.data.model

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(_error: Throwable) : Resource<T>() {
        val error: Event<Throwable> = Event(_error)
    }
}
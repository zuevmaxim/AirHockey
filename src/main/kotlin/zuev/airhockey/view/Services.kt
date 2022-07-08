package zuev.airhockey.view

import kotlin.reflect.KClass

object Services {
    val storage = hashMapOf<Any, HashMap<KClass<*>, Any>>()
    inline fun <reified T> addService(key: Any, service: T) {
        storage.getOrPut(key) { hashMapOf() }[service!!::class] = service
    }

    inline fun <reified T : Any> getService(key: Any, klass: KClass<T>) = storage[key]!![klass] as T
}

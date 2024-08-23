package io.github.skydynamic.maiprober.util.singal

import io.github.skydynamic.maiprober.util.singal.interfact.Signal

class MaiproberSignal<T> : Signal<T> {
    private val listeners = mutableListOf<(T) -> Unit>()

    override fun emit(value: T) {
        listeners.forEach { it.invoke(value) }
    }

    fun connect(listener: (T) -> Unit) {
        listeners.add(listener)
    }

    fun disconnect(listener: (T) -> Unit) {
        listeners.remove(listener)
    }
}
package io.github.skydynamic.maiprober.util.singal.interfact

interface Signal<T> {
    fun emit(value: T)
}
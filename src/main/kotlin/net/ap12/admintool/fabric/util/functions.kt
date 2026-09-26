package net.ap12.admintool.fabric.util

typealias Fn<T, R> = (T) -> R

typealias Fn2<T, T2, R> = (T, T2) -> R

typealias Fn3<T, T2, T3, R> = (T, T2, T3) -> R

fun <T, T2, R> with(base: Fn2<T, T2, R>, value: T2): Fn<T, R> = { t -> base(t, value) }

fun <T, T2, T3, R> with(base: Fn3<T, T2, T3, R>, value: T3): Fn2<T, T2, R> = { t, t2 ->
    base(t, t2, value)
}

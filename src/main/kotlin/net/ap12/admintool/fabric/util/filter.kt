package net.ap12.admintool.fabric.util

typealias Filter<T> = (T) -> Boolean

fun <T> Iterable<T>.filterAndCollect(
    collector: MutableCollection<T>,
    predicate: Filter<T>,
): List<T> {
    return arrayListOf<T>().apply {
        for (element in this@filterAndCollect) if (predicate(element)) add(element)
        else collector.add(element)
    }
}

fun <T, R> Iterable<T>.mapAndCollect(collector: MutableCollection<T>, mapper: (T) -> R?): List<R> {
    return arrayListOf<R>().apply {
        for (element in this@mapAndCollect) try {
            mapper(element).let { if (it == null) collector.add(element) else add(it) }
        } catch (_: Exception) {
            collector.add(element)
        }
    }
}

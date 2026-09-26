package net.ap12.admintool.util

fun <E> List<E>.chunkAt(index: Int): Pair<List<E>, List<E>> {
    return this.subList(0, index) to this.subList(index + 1, this.size)
}

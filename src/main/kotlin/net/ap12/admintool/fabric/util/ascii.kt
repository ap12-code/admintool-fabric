package net.ap12.admintool.fabric.util

val LOWER_CHARACTERS = ('a'..'z')
val UPPER_CHARACTERS = ('A'..'Z')
const val UNDERSCORE = '_'
const val DOT = '.'

fun isAlphabet(char: Char, lower: Boolean = true, upper: Boolean = true): Boolean {
    if (lower && LOWER_CHARACTERS.contains(char)) return true
    if (upper && UPPER_CHARACTERS.contains(char)) return true
    return false
}

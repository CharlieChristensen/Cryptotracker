package com.charliechristensen.cryptotracker.data

import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * Generates random values
 */
object DataFactory {

    private const val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun randomSymbol(): String {
        return generateRandomStringOfLength(10)
    }

    fun randomUrl(): String {
        return generateRandomStringOfLength(20)
    }

    fun randomCoinName(): String {
        return generateRandomStringOfLength(10)
    }

    fun randomDouble(): Double {
        return ThreadLocalRandom.current().nextDouble(0.1, 1000.0)
    }

    fun randomInt(): Int {
        return ThreadLocalRandom.current().nextInt(0, 1000)
    }

    private fun generateRandomStringOfLength(length: Long): String {
        return Random().ints(length, 0, source.length)
            .toArray()
            .asSequence()
            .map(source::get)
            .joinToString("")
    }

}
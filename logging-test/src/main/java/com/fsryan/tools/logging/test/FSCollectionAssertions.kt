package com.fsryan.tools.logging.test

object FSCollectionAssertions {

    @JvmStatic
    @JvmOverloads
    fun <K:Any, V:Any> assertMapContents(
        desc: String? = null,
        expectedContents: Map<K, V>,
        actual: Map<K, V>,
        allowExcess: Boolean = false
    ) = MapEquivalenceEvaluation(
        expected = expectedContents,
        actual = actual
    ).makeAssertion(desc, allowExcess)

    @JvmStatic
    @JvmOverloads
    fun <K:Any, V:Any> assertMapEquals(desc: String? = null, expected: Map<K, V>?, actual: Map<K, V>?) {
        if (handledNullPossibility(desc, expected, actual)) {
            return
        }
        assertMapContents(desc, checkNotNull(expected), checkNotNull(actual))
    }

    @JvmStatic
    @JvmOverloads
    fun <K:Any, V:Any> assertMapContains(
        desc: String? = null,
        expectedKey: K,
        expectedValue: V,
        actualValues: Map<K, V>
    ) = MapEquivalenceEvaluation(
        expected = mapOf(expectedKey to expectedValue),
        actual = actualValues
    ).makeAssertion(desc, allowExcess = true)

    @JvmStatic
    @JvmOverloads
    fun <T> assertSetEquals(desc: String?= null, expected: Set<T>?, actual: Set<T>?) {
        if (handledNullPossibility(desc, expected, actual)) {
            return
        }

        val excess = HashSet(actual)
        val missing = HashSet<T>()
        for (expectedItem in expected!!) {
            if (!excess.remove(expectedItem)) {
                missing.add(expectedItem)
            }
        }

        if (missing.size == 0 && excess.size == 0) {
            return
        }

        val message = "\nexpected: $expected\nexcess:   $excess\nmissing:  $missing"
        fail(failPrepend(desc) + message)
    }

    @JvmStatic
    @JvmOverloads
    fun <T> assertListEquals(desc: String? = null, expected: List<T>, actual: List<T>) {
        if (handledNullPossibility(desc, expected, actual)) {
            return
        }
        for (i in expected.indices) {
            val expectedItem = expected[i]
            try {
                val actualItem = actual[i]
                if (expectedItem != actualItem) {
                    fail(failPrepend(desc) + "\nfirst unequal item at index " + i + "\nexpected: " + expected[i] + "\nbut was:  " + actual[i])
                }
            } catch (ioobe: IndexOutOfBoundsException) {
                throw RuntimeException("actual did not have index " + i + if (desc == null) "" else "; $desc", ioobe)
            }

        }
        val expectedSize = expected.size
        val actualSize = actual.size
        if (expectedSize != actualSize) {
            fail("${failPrepend(desc)} longer than expected; expected $expectedSize, but was $actualSize")
        }
    }

    private fun handledNullPossibility(desc: String?, expected: Any?, actual: Any?): Boolean {
        if (expected == null) {
            if (actual != null) {
                fail("expected was null, but actual was not null: $actual")
            }
            return true
        }
        if (actual == null) {
            throw NullPointerException("${failPrepend(desc)}; expected non null: $expected")
        }
        return false
    }

    private fun failPrepend(desc: String?): String = if (desc == null) "" else "$desc\n"
}

internal data class MapEquivalenceEvaluation<K:Any, V:Any>(
    val expected: Map<K, V>?,
    val actual: Map<K, V>?
) {
    private val excess: Map<K, V>
    private val missing: Map<K, V>
    private val unequal: Map<K, V>

    init {
        val tmpExpected = mutableMapOf<K, V>()
        expected?.let { tmpExpected.putAll(it) }

        val tmpExcess = mutableMapOf<K, V>()
        tmpExcess.putAll(actual ?: emptyMap())

        val tmpMissing = mutableMapOf<K, V>()
        val tmpUnequal = mutableMapOf<K, V>()

        for ((key: K, expectedV: V) in expected ?: emptyMap()) {
            val actualV: V? = tmpExcess.remove(key)
            if (actualV == null) {
                tmpMissing[key] = expectedV
            } else if (expectedV != actualV) {
                tmpUnequal[key] = actualV
            }
        }

        excess = tmpExcess.toMap()
        missing = tmpMissing.toMap()
        unequal = tmpUnequal.toMap()
    }

    fun makeAssertion(desc: String? = null, allowExcess: Boolean = false) {
        if (missing.isEmpty() && unequal.isEmpty() && (allowExcess || excess.isEmpty())) {
            return
        }

        val message = "\nexpected: $expected\nexcess:   $excess\nmissing:  $missing\nunequal:  $unequal"
        fail(desc?.let { "$it\n$message"} ?: message)
    }
}
package com.fsryan.tools.logging.test

object FSCollectionAssertions {

    @JvmStatic
    fun <K, V> assertMapEquals(expected: Map<K, V>?, actual: Map<K, V>?) {
        assertMapEquals(null, expected, actual)
    }

    @JvmStatic
    fun <K, V> assertMapEquals(desc: String?, expected: Map<K, V>?, actual: Map<K, V>?) {
        if (handledNullPossiblity(desc, expected, actual)) {
            return
        }

        val excess = HashMap(actual)
        val missing = HashMap<K, V>()
        val unequal = HashMap<K, V>()
        for ((key: K, expectedV: V) in expected!!) {
            val actualV: V? = excess.remove(key)
            if (actualV == null) {
                missing[key] = expectedV
            } else if (expectedV != actualV) {
                unequal[key] = actualV
            }
        }

        if (missing.size == 0 && unequal.size == 0 && excess.size == 0) {
            return
        }

        val message = "\nexpected: $expected\nexcess:   $excess\nmissing:  $missing\nunequal:  $unequal"
        fail(failPrepend(desc) + message)
    }

    @JvmStatic
    fun <T> assertSetEquals(expected: Set<T>?, actual: Set<T>?) {
        assertSetEquals(null, expected, actual)
    }

    @JvmStatic
    fun <T> assertSetEquals(desc: String?, expected: Set<T>?, actual: Set<T>?) {
        if (handledNullPossiblity(desc, expected, actual)) {
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
        if (handledNullPossiblity(desc, expected, actual)) {
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

    @JvmStatic
    fun <K, V> assertMapContains(desc: String?, expectedKey: K, expectedValue: V, actualValues: Map<K, V>) {
        val actualValue = actualValues[expectedKey]
        if (actualValue != expectedValue) {
            fail(failPrepend(desc) + "expected $expectedKey=$expectedValue, but was null; actual: $actualValue")
        }
    }

    private fun handledNullPossiblity(desc: String?, expected: Any?, actual: Any?): Boolean {
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
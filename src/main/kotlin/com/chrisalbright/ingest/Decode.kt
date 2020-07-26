package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.math.BigDecimal
import java.math.BigInteger

private fun String.isNumeric(): Boolean = all { it.isDigit() } ||
        (startsWith('-') && drop(1).all { it.isDigit() })


fun String.decodeCurrency(): Option<BigDecimal> {
    return if (isNumeric()) {
        Some(toBigDecimal().movePointLeft(2))
    } else {
        None
    }
}

fun String.decodeInteger(): Option<BigInteger> {
    return if (isNumeric()) {
        Some(toBigInteger())
    } else {
        None
    }
}

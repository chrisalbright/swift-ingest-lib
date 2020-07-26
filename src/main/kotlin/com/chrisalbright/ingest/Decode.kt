package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.math.BigDecimal

private fun String.isNumeric(): Boolean = all { it.isDigit() } ||
        (startsWith('-') && drop(1).all { it.isDigit() })


fun String.decodeCurrency(): Option<BigDecimal> {
    return if (isNumeric()) {
        Some(toBigDecimal().movePointLeft(2))
    } else {
        None
    }
}
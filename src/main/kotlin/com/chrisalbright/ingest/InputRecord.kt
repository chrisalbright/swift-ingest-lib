package com.chrisalbright.ingest

import java.math.BigDecimal
import java.math.BigInteger

data class InputRecord(
        val productId: BigInteger,
        val description: String,
        val regularSingularPrice: BigDecimal,
        val promotionalSingularPrice: BigDecimal,
        val regularSplitPrice: BigDecimal,
        val promotionalSplitPrice: BigDecimal,
        val regularForX: BigInteger,
        val promotionalForX: BigInteger,
        val flags: Flags,
        val productSize: String
)
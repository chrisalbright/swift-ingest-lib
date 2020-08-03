package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.extensions.fx

class SomeStoreProductCatalogTransformer : StringToInputRecord {
    override fun invoke(inputString: String): Option<InputRecord> = if (inputString.isEmpty()) {
        None
    } else {
        Option.fx {
            val (productId) = inputString.substring(0, 8).decodeInteger()
            val productDescription = inputString.substring(9, 68).decodeString()
            val (regularSingularPrice) = inputString.substring(69, 77).decodeCurrency()
            val (promotionalSingularPrice) = inputString.substring(78, 86).decodeCurrency()
            val (regularSplitPrice) = inputString.substring(87, 95).decodeCurrency()
            val (promotionalSplitPrice) = inputString.substring(96, 104).decodeCurrency()
            val (regularForX) = inputString.substring(105, 113).decodeInteger()
            val (promotionalForX) = inputString.substring(114, 122).decodeInteger()
            val flags = inputString.substring(123, 132).toFlags()
            val productSize = inputString.substring(133, 142).decodeString()
            InputRecord(productId, productDescription, regularSingularPrice, promotionalSingularPrice, regularSplitPrice, promotionalSplitPrice, regularForX, promotionalForX, flags, productSize)
        }
    }
}

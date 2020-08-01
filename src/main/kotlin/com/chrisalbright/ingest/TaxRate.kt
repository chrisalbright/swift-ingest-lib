package com.chrisalbright.ingest

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.math.BigDecimal

typealias TaxRateStrategy = (InputRecord) -> Option<BigDecimal>

class RegularTaxRateStrategy(private val rate: BigDecimal) : TaxRateStrategy {
    override fun invoke(inputRecord: InputRecord): Option<BigDecimal> =
            if (inputRecord.isTaxable()) {
                Some(rate)
            } else {
                None
            }
}

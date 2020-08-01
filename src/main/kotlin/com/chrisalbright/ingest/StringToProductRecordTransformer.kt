package com.chrisalbright.ingest

import arrow.core.Option

class StringToProductRecordTransformer(val parseCatalogRecord: StringToInputRecord, val convertToProductRecord: InputRecordToProductRecord) : StringToProductRecord {
    override fun invoke(record: String): Option<ProductRecord> =
            parseCatalogRecord(record).map { convertToProductRecord(it) }
}
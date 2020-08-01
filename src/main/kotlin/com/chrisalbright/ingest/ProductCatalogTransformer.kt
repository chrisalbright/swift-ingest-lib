package com.chrisalbright.ingest

import arrow.core.Option

interface ProductCatalogTransformer<I, O> : Function1<I, O>

typealias StringToInputRecord = ProductCatalogTransformer<String, Option<InputRecord>>
typealias InputRecordToProductRecord = ProductCatalogTransformer<InputRecord, ProductRecord>
typealias StringToProductRecord = ProductCatalogTransformer<String, Option<ProductRecord>>
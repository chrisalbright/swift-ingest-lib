package com.chrisalbright.ingest

import arrow.core.Option
import java.util.function.Function

interface ProductCatalogTransformer<I, O> : Function<I, O>
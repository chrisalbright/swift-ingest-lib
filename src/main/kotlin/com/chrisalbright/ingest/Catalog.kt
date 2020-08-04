package com.chrisalbright.ingest

import arrow.core.getOrElse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

typealias CatalogLines = Flux<String>
typealias ProductRecords = Flux<ProductRecord>

interface CatalogReader<I> : Function1<I, CatalogLines>

class FileCatalogFluxReader : CatalogReader<File> {
    override fun invoke(input: File): CatalogLines =
            Flux.create<String> { sink ->
                input.forEachLine { line ->
                    sink.next(line)
                }
                sink.complete()
            }
}

fun File.readCatalogLines(): CatalogLines = FileCatalogFluxReader().invoke(this)

fun CatalogLines.convertToProductRecords(transform: StringToProductRecord): ProductRecords =
        flatMap { s ->
            transform(s).map { Mono.just(it) }.getOrElse { Mono.empty() }
        }

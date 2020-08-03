package com.chrisalbright.ingest.cli

import com.chrisalbright.ingest.*
import java.io.File

fun main(args: Array<String>) {
    try {
        val fileName: String = args.first()
        val catalogFile = File(fileName)
        val catalogTransformer: StringToProductRecordTransformer = catalogTransformerForFile(catalogFile)

        val productRecords: ProductRecords = catalogFile
                .readCatalogLines()
                .convertToProductRecords(catalogTransformer)

        productRecords.doOnNext { println(it) }.blockLast()

    } catch (ex: NoSuchElementException) {
        System.err.println("Please specify a catalog file")
    }
}
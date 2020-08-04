package com.chrisalbright.ingest.cli

import com.chrisalbright.ingest.*
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

fun watchDirectoryForNewFiles(dir: File): Flux<File> =
        Flux.create { sink: FluxSink<File> ->
            var active = true
            val path: Path = dir.toPath()
            val watcher: WatchService = path.fileSystem.newWatchService()
            val eventsToWatch: Set<WatchEvent.Kind<Path>> = setOf(ENTRY_CREATE, ENTRY_MODIFY)
            path.register(watcher, *eventsToWatch.toTypedArray())
            while (active) {
                try {
                    val key: WatchKey = watcher.take()
                    key.pollEvents()
                            .filter { it.kind() != OVERFLOW }
                            .forEach { event: WatchEvent<*> ->
                                val ctx = event.context()
                                if (ctx is Path) {
                                    val file = File(dir, ctx.fileName.toString())
                                    if (eventsToWatch.contains(event.kind())) {
                                        sink.next(file)
                                    }
                                }
                            }
                    active = key.reset()
                } catch (ex: IOException) {
                    sink.error(ex)
                }
            }
            sink.complete()
        }

fun main(args: Array<String>) {
    try {
        val inputDirectoryName: String = args.first()
        val inputDirectory = File(inputDirectoryName)
        val newFiles: Flux<File> = watchDirectoryForNewFiles(inputDirectory)
        val productRecords: ProductRecords = newFiles.flatMap { newFile ->
            val catalogTransformer: StringToProductRecordTransformer = catalogTransformerForFile(newFile)
            newFile
                    .readCatalogLines()
                    .convertToProductRecords(catalogTransformer)
        }
        productRecords.doOnNext { println(it) }.blockLast()

    } catch (ex: NoSuchElementException) {
        System.err.println("Please specify a directory to watch")
    } catch (ex: IOException) {
        ex.printStackTrace(System.err)
    }
}
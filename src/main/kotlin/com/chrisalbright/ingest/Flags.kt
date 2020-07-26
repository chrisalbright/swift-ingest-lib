package com.chrisalbright.ingest

data class Flags(val fs: String)

fun Flags.isSoldByWeight(): Boolean = fs.drop(2).first() == 'Y'
fun Flags.isTaxable(): Boolean = fs.drop(4).first() == 'Y'
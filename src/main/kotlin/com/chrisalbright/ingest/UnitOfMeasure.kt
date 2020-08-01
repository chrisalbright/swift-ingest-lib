package com.chrisalbright.ingest

typealias UnitOfMeasureStrategy = (InputRecord) -> ProductRecord.UnitOfMeasure

class ImperialUnitOfMeasureStrategy : UnitOfMeasureStrategy {
    override fun invoke(rec: InputRecord): ProductRecord.UnitOfMeasure =
        if (rec.flags.isSoldByWeight()) {
            ProductRecord.UnitOfMeasure.Pound
        } else {
            ProductRecord.UnitOfMeasure.Each
        }


}
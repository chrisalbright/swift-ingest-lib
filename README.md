# Swiftly Product Catalog Ingester
[![Build Status](https://travis-ci.org/chrisalbright/swift-ingest-lib.svg?branch=master)](https://travis-ci.org/chrisalbright/swift-ingest-lib)

## A library for processing Store Product Catalogs

### Building
From the root of the project, run `./mvnw clean package`. Tested with java 8, 11, and 14.
For Microsoft Windows the command`./mvnw.cmd` is available, but I have no way to
test this. 

### Run the Samples
To run the single file example:
```
java -cp target/swift-ingest-lib-example.jar com.chrisalbright.ingest.cli.ConvertAndPrintSingleCatalogFileKt src/test/resources/input-sample.txt
``` 
and observe the output

To run the watched directory example:
```
java -cp target/swift-ingest-lib-example.jar com.chrisalbright.ingest.cli.ConvertAndPrintWatchedDirectoryKt src/test/resources
``` 
then in a separate terminal window, copy some sample files into `src/test/resources` and observe the output.

### Implementation Summary

The essence of this library is a transformation from a string into a `ProductRecord` 
using a composition of several `ProductCatalogTransformer`s

A Store Specific transformer takes in a string and using the rules for the particular
Store catalog file will convert it into an `InputRecord` if it can. An `InputRecord`
is an intermediate structure. The `Option` return type represents the possibility
of failure to parse.
```
┌───────────────────────────────────────────────────────────┐
│                                                           │
│  Store Specific Catalog Transformer                       │
│                                                           │
│  ┌──────────────────────┐      ┌───────────────────────┐  │
│  │                      │      │                       │  │
│  │                      │      │                       │  │
│  │        String        │─────>│  Option<InputRecord>  │  │
│  │                      │      │                       │  │
│  │                      │      │                       │  │
│  └──────────────────────┘      └───────────────────────┘  │
│                                                           │
└───────────────────────────────────────────────────────────┘
```

An Input Record transformer maps an `InputRecord` to a `ProductRecord`. It
determines pricing, unit of measure, and assigns a tax rate if necessary. 
```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│  Input Record Transformer                                │
│                                                          │
│  ┌──────────────────────┐      ┌──────────────────────┐  │
│  │                      │      │                      │  │
│  │                      │      │                      │  │
│  │     InputRecord      │─────>│    ProductRecord     │  │
│  │                      │      │                      │  │
│  │                      │      │                      │  │
│  └──────────────────────┘      └──────────────────────┘  │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

The composition of a Store Specific Catalog Transformer and an Input Record
Transformer allows us to convert a `String` record from the specific store
catalog into a `ProductRecord`. Since parsing could fail, the output type
is `Option`.
```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│  Catalog Transformer                                     │
│                                                          │
│  ┌──────────────────────┐     ┌───────────────────────┐  │
│  │                      │     │                       │  │
│  │                      │     │                       │  │
│  │        String        │────>│ Option<ProductRecord> │  │
│  │                      │     │                       │  │
│  │                      │     │                       │  │
│  └──────────────────────┘     └───────────────────────┘  │
│                                                          │
└──────────────────────────────────────────────────────────┘
```
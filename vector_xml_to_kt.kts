#!/usr/bin/env kscript
@file:MavenRepository("jcenter", "https://jcenter.bintray.com/")
@file:DependsOnMaven("com.squareup:kotlinpoet:1.5.0")
@file:Include("Icon.kt")
@file:Include("IconParser.kt")
@file:Include("PathNode.kt")
@file:Include("PathParser.kt")
@file:Include("VectorAssetGenerator.kt")
@file:Include("VectorNode.kt")

val inputFile = args.getOrNull(0) ?: error("missing input file")
check(inputFile.endsWith(".xml"))

val outputFile = args.getOrNull(1) ?: error("missing output file")
check(inputFile.endsWith(".kt"))

error("hello i $inputFile o $outputFile")
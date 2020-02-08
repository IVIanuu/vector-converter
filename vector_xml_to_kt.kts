#!/usr/bin/env kscript
@file:MavenRepository("ivianuu", "https://dl.bintray.com/ivianuu/maven/")
@file:MavenRepository("jcenter", "https://jcenter.bintray.com/")
@file:DependsOnMaven("com.squareup:kotlinpoet:1.5.0")
@file:DependsOnMaven("com.ivianuu.vectorconverter:vector-converter:0.0.1-dev1")
@file:DependsOnMaven("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
@file:DependsOnMaven("com.google.guava:guava:28.2-jre")

import com.google.common.base.CaseFormat
import com.ivianuu.vectorconverter.xmlToKotlin
import java.io.File
import java.util.regex.Pattern

val inputFilePath = args.getOrNull(0) ?: error("missing input file")
check(inputFilePath.endsWith(".xml"))

val inputFile = File(inputFilePath)
check(inputFile.exists())

val userDir = System.getProperty("user.dir")

val iconName = inputFile.name
        .removePrefix("ic_")
        .removeSuffix(".xml")
        .let { CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, it) }

val xmlIcon = inputFile.readText()

println("Convert $inputFilePath\n\n$xmlIcon")

val kotlinIcon = xmlToKotlin(
        xml = xmlIcon,
        packageName = "com.ivianuu.no.packagename",
        iconName = iconName
)

println("Created kotlin file\n\n$kotlinIcon")

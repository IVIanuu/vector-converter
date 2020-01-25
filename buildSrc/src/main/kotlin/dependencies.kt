/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("ClassName", "unused")

object Publishing {
    const val groupId = "com.ivianuu.vectorconverter"
    const val vcsUrl = "https://github.com/IVIanuu/vector-converter"
    const val version = "0.0.1-dev1"
}

object Deps {
    const val bintrayGradlePlugin =
        "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4"

    object Kotlin {
        private const val version = "1.3.61"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
    }

    const val kotlinPoet = "com.squareup:kotlinpoet:1.5.0"

    const val mavenGradlePlugin =
        "com.github.dcendents:android-maven-gradle-plugin:2.1"

    const val spotlessGradlePlugin = "com.diffplug.spotless:spotless-plugin-gradle:3.26.1"

    const val xmlPull = "xmlpull:xmlpull:1.1.3.4a"
    const val xpp3 = "xpp3:xpp3:1.1.4c"
}
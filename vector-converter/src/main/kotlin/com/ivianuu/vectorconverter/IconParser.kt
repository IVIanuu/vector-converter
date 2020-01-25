/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.vectorconverter

import com.squareup.kotlinpoet.FileSpec
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

private const val CLIP_PATH = "clip-path"
private const val GROUP = "group"
private const val PATH = "path"

private const val WIDTH = "android:width"
private const val HEIGHT = "android:height"
private const val PATH_DATA = "android:pathData"
private const val FILL_ALPHA = "android:fillAlpha"
private const val STROKE_ALPHA = "android:strokeAlpha"

internal fun Icon.toFileSpec(): FileSpec {
    val parser = XmlPullParserFactory.newInstance().newPullParser().apply {
        setInput(xml.byteInputStream(), null)
        seekToStartTag()
    }

    check(parser.name == "vector") { "The start tag must be <vector>!" }

    val width = parser.getDpValueAsFloat(WIDTH) ?: 24f
    val height = parser.getDpValueAsFloat(HEIGHT) ?: 24f

    parser.next()

    val rootNodes = mutableListOf<VectorNode>()

    var currentGroup: VectorNode.Group? = null

    while (!parser.isAtEnd()) {
        when (parser.eventType) {
            START_TAG -> {
                when (parser.name) {
                    PATH -> {
                        val pathData = parser.getAttributeValue(
                            null,
                            PATH_DATA
                        )
                        val fillAlpha = parser.getValueAsFloat(FILL_ALPHA)
                        val strokeAlpha = parser.getValueAsFloat(STROKE_ALPHA)
                        val path =
                            VectorNode.Path(
                                strokeAlpha = strokeAlpha ?: 1f,
                                fillAlpha = fillAlpha ?: 1f,
                                nodes = PathParser.parsePathString(pathData)
                            )
                        if (currentGroup != null) {
                            currentGroup.paths.add(path)
                        } else {
                            rootNodes.add(path)
                        }
                    }
                    // Material icons are simple and don't have nested groups, so this can be simple
                    GROUP -> {
                        val group =
                            VectorNode.Group()
                        currentGroup = group
                        rootNodes.add(group)
                    }
                    CLIP_PATH -> { /* TODO: b/147418351 - parse clipping paths */
                    }
                }
            }
        }
        parser.next()
    }
    return VectorAssetGenerator(icon = this, width = width, height = height, rootNodes = rootNodes).
        createFileSpec()
}

private fun XmlPullParser.getDpValueAsFloat(name: String) =
    getAttributeValue(null, name)?.replace("dp", "")?.toFloatOrNull()

private fun XmlPullParser.getValueAsFloat(name: String) =
    getAttributeValue(null, name)?.toFloatOrNull()

private fun XmlPullParser.seekToStartTag(): XmlPullParser {
    var type = next()
    while (type != START_TAG && type != END_DOCUMENT) {
        // Empty loop
        type = next()
    }
    if (type != START_TAG) {
        throw XmlPullParserException("No start tag found")
    }
    return this
}

private fun XmlPullParser.isAtEnd() =
    eventType == END_DOCUMENT || (depth < 1 && eventType == END_TAG)

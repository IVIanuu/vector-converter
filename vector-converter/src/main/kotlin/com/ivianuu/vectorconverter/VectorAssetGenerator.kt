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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock

internal class VectorAssetGenerator(
    private val icon: Icon,
    private val width: Float,
    private val height: Float,
    private val rootNodes: List<VectorNode>
) {

    private val materialIconMember = MemberName("com.ivianuu.essentials.material.icons", "lazyMaterialIcon")
    private val pathMember = MemberName("com.ivianuu.essentials.material.icons", "path")
    private val groupMember = MemberName("com.ivianuu.essentials.material.icons", "group")
    private val vectorAsset = ClassName("androidx.ui.graphics.vector", "VectorAsset")
    private val icons = ClassName("com.ivianuu.essentials.material.icons", "Icons")

    fun createFileSpec() = FileSpec.builder(
        icon.packageName,
        icon.iconName
    ).addProperty(
        PropertySpec.builder(name = icon.iconName, type = vectorAsset)
            .receiver(icons)
            .delegate(materialIconDelegate())
            .build()
    ).build()

    private fun materialIconDelegate(): CodeBlock {
        return buildCodeBlock {
            addFunctionWithLambda(functionCall = iconFunctionCall()) {
                rootNodes.forEach { node -> addRecursively(node) }
            }
        }
    }

    /**
     * Function call to create an icon.
     */
    private fun iconFunctionCall(): CodeBlock.Builder.() -> Unit = {
        val parameterList = listOfNotNull(
            "width = ${width}f".takeIf { width != 24f },
            "height = ${height}f".takeIf { height != 24f }
        )

        val parameters = if (parameterList.isNotEmpty()) {
            parameterList.joinToString(prefix = "(", postfix = ")")
        } else {
            ""
        }

        addStatement("%M$parameters {", materialIconMember)
    }

    /**
     * The function call to add a group to the builder.
     */
    private val groupFunctionCall: CodeBlock.Builder.() -> Unit
        get() = {
            addStatement("%M {", groupMember)
        }

    /**
     * The function call to add this [VectorNode.Path] to the builder.
     */
    private fun VectorNode.Path.pathFunctionCall(): CodeBlock.Builder.() -> Unit = {
        val parameterList = listOfNotNull(
            "fillAlpha = ${fillAlpha}f".takeIf { fillAlpha != 1f },
            "strokeAlpha = ${strokeAlpha}f".takeIf { strokeAlpha != 1f }
        )

        val parameters = if (parameterList.isNotEmpty()) {
            parameterList.joinToString(prefix = "(", postfix = ")")
        } else {
            ""
        }

        addStatement("%M$parameters {", pathMember)
    }

    /**
     * Generates a correctly indented lambda invocation for the given [functionCall] and
     * [lambdaBody].
     */
    private fun CodeBlock.Builder.addFunctionWithLambda(
        functionCall: CodeBlock.Builder.() -> Unit,
        lambdaBody: CodeBlock.Builder.() -> Unit
    ) {
        functionCall()
        indent()
        indent()
        lambdaBody()
        unindent()
        unindent()
        addStatement("}")
    }

    /**
     * Recursively adds function calls to construct the given [vectorNode] and its children.
     */
    private fun CodeBlock.Builder.addRecursively(vectorNode: VectorNode) {
        when (vectorNode) {
            // TODO: b/147418351 - add clip-paths once they are supported
            is VectorNode.Group -> {
                addFunctionWithLambda(groupFunctionCall) {
                    vectorNode.paths.forEach { path ->
                        addRecursively(path)
                    }
                }
            }
            is VectorNode.Path -> {
                addFunctionWithLambda(vectorNode.pathFunctionCall()) {
                    vectorNode.nodes.forEach { pathNode ->
                        addStatement(pathNode.asFunctionCall())
                    }
                }
            }
        }
    }
}

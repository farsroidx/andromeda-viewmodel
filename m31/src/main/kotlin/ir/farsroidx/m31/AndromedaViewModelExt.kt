@file:Suppress("UNCHECKED_CAST")

package ir.farsroidx.m31

import androidx.annotation.MainThread
import java.lang.reflect.ParameterizedType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

@MainThread
fun <S : Any> AndromedaStatefulViewModel<*>.instanceWithDefaults(): S {

    val type = (this.javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments[0]

    val clazz = ( type as Class<S> ).kotlin

    val constructor = clazz.primaryConstructor

    if (!clazz.isData || constructor == null) {
        throw RuntimeException("The '${clazz.simpleName}' is not a Data Class.")
    }

    val args = constructor.parameters
        .filterNot { it.isOptional }
        .associateWith { param ->
            when {
                param.type.isMarkedNullable             -> null
                param.type.jvmErasure == Char::class    -> '\u0000'
                param.type.jvmErasure == String::class  -> ""
                param.type.jvmErasure == Byte::class    -> 0.toByte()
                param.type.jvmErasure == Short::class   -> 0.toShort()
                param.type.jvmErasure == Int::class     -> 0
                param.type.jvmErasure == Float::class   -> 0F
                param.type.jvmErasure == Double::class  -> 0.0
                param.type.jvmErasure == Long::class    -> 0L
                param.type.jvmErasure == Boolean::class -> false
                param.type.jvmErasure == List::class    -> emptyList<Any>()
                param.type.jvmErasure == Set::class     -> emptySet<Any>()
                param.type.jvmErasure == Map::class     -> emptyMap<Any, Any>()
                else -> throw IllegalArgumentException(
                    """
                        Cannot create default value for parameter: ${param.name} of type $type Supported 
                        types: Char, String, Byte, Short, Int, Float, Double, Long, Boolean, List, Set, Map, and nullable types (e.g., Int?, String?).
                    """.trimIndent()
                )
            }
        }

    return constructor.callBy(args)
}
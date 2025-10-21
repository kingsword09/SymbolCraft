package io.github.kingsword09.symbolcraft.tasks.internal

import io.github.kingsword09.symbolcraft.model.IconConfig

/**
 * Utility for grouping icons by their originating library.
 *
 * The conversion pipeline writes Kotlin sources into `icons/<library>/...`. Grouping ahead of time
 * allows us to create those directories once and keep log output organised.
 */
internal object IconLibraryClassifier {

    fun groupByLibrary(config: Map<String, List<IconConfig>>): Map<String, Set<String>> {
        val libraryMap = mutableMapOf<String, MutableSet<String>>()

        config.forEach { (iconName, iconConfigs) ->
            iconConfigs.forEach { iconConfig ->
                libraryMap.getOrPut(iconConfig.libraryId) { mutableSetOf() }.add(iconName)
            }
        }

        return libraryMap
    }
}

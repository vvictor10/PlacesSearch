package com.grace.placessearch.service.local


import java.io.File

abstract class PlacesTestBase {

    protected val places: PlacesApiLocal
        get() = PLACES_API

    companion object {

        private const val RESOURCES_PATH = "/resources"
        private val PLACES_API = PlacesApiLocal(resourcesPath)

        private val resourcesPath: String
            get() = File(".").absolutePath + RESOURCES_PATH
    }
}

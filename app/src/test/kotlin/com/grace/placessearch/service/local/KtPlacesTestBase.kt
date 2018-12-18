package com.grace.placessearch.service.local


import com.grace.placessearch.service.PlacesApi

import java.io.File

abstract class KtPlacesTestBase {

    protected val places: PlacesApi
        get() = PLACES_API

    companion object {

        private val RESOURCES_PATH = "/resources"
        private val PLACES_API = PlacesApiLocal(resourcesPath)

        val resourcesPath: String
            get() = File(".").getAbsolutePath() + RESOURCES_PATH
    }
}

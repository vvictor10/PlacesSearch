package com.grace.placessearch.service.local

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.grace.placessearch.common.data.model.SuggestedVenuesResponse
import com.grace.placessearch.common.data.model.VenueResponse
import com.grace.placessearch.common.data.model.VenuesResponse
import com.grace.placessearch.service.PlacesApi
import retrofit2.Response
import retrofit2.adapter.rxjava.Result
import rx.Observable
import java.io.*

class PlacesApiLocal(private val resourcesDirectory: String) : PlacesApi {

    private val gsonInstance: Gson
        get() = GsonBuilder().create()

    init {
        println(resourcesDirectory)
    }

    override fun trendingVenues(): Observable<Result<VenuesResponse>> {
        println("getTrendingVenues: $resourcesDirectory")
        val jsonString = fetchJsonFromFile("$resourcesDirectory/trending_venues.json")
        return Observable.just(Result.response(Response.success(gsonInstance.fromJson(jsonString, VenuesResponse::class.java))))
    }

    override fun searchForVenues(searchTerm: String): Observable<Result<VenuesResponse>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchForSuggestedVenues(searchTerm: String): Observable<Result<SuggestedVenuesResponse>> {
        println("searchForSuggestedVenues: $resourcesDirectory")
        val jsonString = fetchJsonFromFile("$resourcesDirectory/suggested_venues.json")
        return Observable.just(Result.response(Response.success(gsonInstance.fromJson(jsonString, SuggestedVenuesResponse::class.java))))
    }

    override fun getVenue(venueId: String): Observable<Result<VenueResponse>> {
        println("getVenue: $resourcesDirectory")
        val jsonString = fetchJsonFromFile("$resourcesDirectory/single_venue.json")
        return Observable.just(Result.response(Response.success(gsonInstance.fromJson(jsonString, VenueResponse::class.java))))
    }

    companion object {

        fun fetchJsonFromFile(filePath: String): String? {
            println(filePath)
            val writer = StringWriter()
            var reader: Reader? = null
            try {
                reader = BufferedReader(FileReader(filePath))

                val buffer = CharArray(1024)
                var n: Int = reader.read(buffer)
                while (n != -1) {
                    writer.write(buffer, 0, n)
                    n = reader.read(buffer)
                }
                reader.close()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return null
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return null
                    }

                }
            }
            return writer.toString()
        }
    }
}

package com.grace.placessearch.service.network

import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class PlacesRetrofitException : RuntimeException {

    /**
     * The request URL which produced the error.
     */
    val url: String?
    /**
     * Response object containing status code, headers, body, etc.
     */
    val response: Response<*>?
    /**
     * The event type which triggered this error.
     */
    val type: Type?
    private val retrofit: Retrofit?

    /**
     * Use this method to get the underlying http response code.
     */
    val httpResponseCode: Int
        get() = response?.code() ?: 0

    internal constructor(message: String?, url: String?, response: Response<*>?, type: Type?, exception: Throwable, retrofit: Retrofit?) : super(message, exception) {
        this.url = url
        this.response = response
        this.type = type
        this.retrofit = retrofit
    }

    internal constructor(message: String?, url: String?, response: Response<*>?, type: Type?, retrofit: Retrofit?) : super(message) {
        this.url = url
        this.response = response
        this.type = type
        this.retrofit = retrofit
    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified `type`.
     */
    @Throws(IOException::class)
    fun <T> getErrorBodyAs(type: Class<T>): T? {
        val converter = retrofit?.responseBodyConverter<T>(type, arrayOfNulls(0))
        return converter?.convert(response?.errorBody())
    }

    /**
     * Identifies the event kind which triggered a [PlacesRetrofitException].
     */
    enum class Type {
        /**
         * An [IOException] occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNAUTHORIZED,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    companion object {

        fun httpError(url: String, response: Response<*>, retrofit: Retrofit): PlacesRetrofitException {
            val responseMessage = if (response.message() == null) "Message not available" else response.message()
            val message = response.code().toString() + " " + responseMessage
            return PlacesRetrofitException(message, url, response, Type.HTTP, retrofit)
        }

        fun unexpectedError(exception: Throwable): PlacesRetrofitException {
            return PlacesRetrofitException(exception.message, null, null, Type.UNEXPECTED, exception, null)
        }

        fun networkError(exception: IOException): PlacesRetrofitException {
            return PlacesRetrofitException(exception.message, null, null, Type.NETWORK, exception, null)
        }

        fun unauthorizedError(exception: Throwable): PlacesRetrofitException {
            return PlacesRetrofitException(exception.message, null, null, Type.UNAUTHORIZED, exception, null)
        }
    }
}
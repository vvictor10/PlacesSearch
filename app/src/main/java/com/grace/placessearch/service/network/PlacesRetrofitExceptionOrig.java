package com.grace.placessearch.service.network;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

@Deprecated
public class PlacesRetrofitExceptionOrig extends RuntimeException {

    private final String url;
    private final Response response;
    private final Type type;
    private final Retrofit retrofit;
    PlacesRetrofitExceptionOrig(String message, String url, Response response, Type type, Throwable exception, Retrofit retrofit) {
        super(message, exception);
        this.url = url;
        this.response = response;
        this.type = type;
        this.retrofit = retrofit;
    }

    PlacesRetrofitExceptionOrig(String message, String url, Response response, Type type, Retrofit retrofit) {
        super(message);
        this.url = url;
        this.response = response;
        this.type = type;
        this.retrofit = retrofit;
    }

    public static PlacesRetrofitExceptionOrig httpError(String url, Response response, Retrofit retrofit) {
        String responseMessage = (response.message()) == null ? "Message not available" : response.message();
        String message = response.code() + " " + responseMessage;
        return new PlacesRetrofitExceptionOrig(message, url, response, Type.HTTP, retrofit);
    }

    public static PlacesRetrofitExceptionOrig unexpectedError(Throwable exception) {
        return new PlacesRetrofitExceptionOrig(exception.getMessage(), null, null, Type.UNEXPECTED, exception, null);
    }

    public static PlacesRetrofitExceptionOrig networkError(IOException exception) {
        return new PlacesRetrofitExceptionOrig(exception.getMessage(), null, null, Type.NETWORK, exception, null);
    }

    public static PlacesRetrofitExceptionOrig unauthorizedError(Throwable exception) {
        return new PlacesRetrofitExceptionOrig(exception.getMessage(), null, null, Type.UNAUTHORIZED, exception, null);
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * The event type which triggered this error.
     */
    public Type getType() {
        return type;
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified {@code type}.
     */
    public <T> T getErrorBodyAs(Class<T> type) throws IOException {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(type, new Annotation[0]);
        return converter.convert(response.errorBody());
    }

    /**
     * Use this method to get the underlying http response code.
     */
    public int getHttpResponseCode() {
        if (response == null) {
            return 0;
        }
        return response.code();
    }

    /**
     * Identifies the event kind which triggered a {@link PlacesRetrofitExceptionOrig}.
     */
    public enum Type {
        /**
         * An {@link IOException} occurred while communicating to the server.
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
}
package com.grace.placessearch.service.network

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.Result
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Observable
import rx.functions.Func1
import java.io.IOException
import java.lang.reflect.Type

class PlacesRxJavaCallAdapterFactory private constructor() : CallAdapter.Factory() {
    private val original: RxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create()

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*> {
        return RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit))
    }

    private class RxCallAdapterWrapper(private val retrofit: Retrofit, private val wrapped: CallAdapter<*>) : CallAdapter<Observable<*>> {

        override fun responseType(): Type {
            return wrapped.responseType()
        }

        override fun <R> adapt(call: Call<R>): Observable<Result<R>> {

            // Lambda version
            return (wrapped.adapt(call) as Observable<Result<R>>)
                    .flatMap {
                        val response = it.response()
                        if (response != null && response.isSuccessful) {
                            Observable.just(it)
                        } else {
                            if (it.isError) {
                                if (it.error() is IOException) {
                                    Observable.error(PlacesRetrofitException.networkError(it.error() as IOException))
                                } else {
                                    Observable.error(PlacesRetrofitException.unexpectedError(it.error()))
                                }
                            } else {
                                Observable.error(PlacesRetrofitException.unexpectedError(Throwable("Something went wrong. Please try again later.")))
                            }
                        }
                    }

//            // Non-lambda version
//            return (wrapped.adapt(call) as Observable<Result<R>>)
//                    .flatMap(object : Func1<Result<R>, Observable<Result<R>>> {
//                        override fun call(result: Result<R>): Observable<Result<R>> {
//                            val response = result.response()
//                            return if (response != null && response.isSuccessful) {
//                                Observable.just(result)
//                            } else {
//                                if (result.isError) {
//                                    if (result.error() is IOException) {
//                                        Observable.error(PlacesRetrofitException.networkError(result.error() as IOException))
//                                    } else {
//                                        Observable.error(PlacesRetrofitException.unexpectedError(result.error()))
//                                    }
//                                } else {
//                                    Observable.error(PlacesRetrofitException.unexpectedError(Throwable("Something went wrong. Please try again later.")))
//                                }
//                            }
//                        }
//                    })

        }
    }

    companion object {
        fun create(): CallAdapter.Factory {
            return PlacesRxJavaCallAdapterFactory()
        }
    }
}
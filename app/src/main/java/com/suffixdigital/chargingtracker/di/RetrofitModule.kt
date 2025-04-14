package com.suffixdigital.chargingtracker.di

import com.suffixdigital.chargingtracker.BuildConfig
import com.suffixdigital.chargingtracker.interfaces.ApiService
import com.suffixdigital.chargingtracker.utils.timeout
import com.suffixdigital.chargingtracker.utils.timeout_debug
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/******************************************************************************
 * **[RetrofitModule]** class is used for to call retrofit HTTP or HTTPS API network calls
 * RetrofitModule is singleton object and it create only once when it call first time.
 *
 *      level = if (BuildConfig.DEBUG) {
 *                  BODY
 *              } else {
 *                  NONE
 *              }
 *
 * *  For debug build app are display API Log messages in logcat

 * **[InstallIn]** annotation is a core concept in Hilt. Hilt uses a hierarchical system of components to manage dependency injection.
 * @InstallIn acts as the bridge, explicitly telling Hilt which component a particular module should contribute its bindings (dependencies) to.
 *
 * The component you choose with @InstallIn determines the scope or lifespan of the dependencies provided by that module.
 * Here a module installed in **[SingletonComponent]** provides dependencies that live as long as the entire application.
 *****************************************************************************/

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    /******************************************************************************
     * [provideInterceptor] singleton function is use to display all Retrofit API calling related details in Logs. Details like
     * Retrofit API url, Body params, method [GET/POST/DELETE/PUT], response etc. Here [HttpLoggingInterceptor] class display Retrofit
     * API related log only for Debug Build.
     *****************************************************************************/
    @Singleton
    @Provides
    fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                BODY
            } else NONE
        }
    }

    /******************************************************************************
     * [getHttpLogClient] singleton class is use during Retrofit API call time. Here [OkHttpClient] class defined Retrofit API
     * connection timeout, read timeout, write timeout, required interceptor related details.
     *****************************************************************************/
    @Singleton
    @Provides
    fun getHttpLogClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                connectTimeout(timeout_debug, TimeUnit.SECONDS)
                writeTimeout(timeout_debug, TimeUnit.SECONDS)
                readTimeout(timeout_debug, TimeUnit.SECONDS)
            } else {
                connectTimeout(timeout, TimeUnit.SECONDS)
                writeTimeout(timeout, TimeUnit.SECONDS)
                readTimeout(timeout, TimeUnit.SECONDS)
            }
        }.addInterceptor(provideInterceptor())
            .cookieJar(JavaNetCookieJar(CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ALL) }))
            .build()

    }

    /******************************************************************************
     * [providesRetrofitInstance] singleton class is create a instance of Retrofit API. Here [Retrofit] class defined [BuildConfig.BASE_URL]
     * i.e which url are use to communicate with HTTP network cloud, [GsonConverterFactory] i.e communication between android app and
     * HTTP network cloud should through GSON format. So all data must be required in JSON format for input and output both.
     ******************************************************************************/
    @Singleton
    @Provides
    fun providesRetrofitInstance(): Retrofit {
        return Retrofit.Builder().baseUrl("https://jpi.nub.mybluehostin.me/data.php/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpLogClient()).build()
    }


    /******************************************************************************
     * [provideRetrofitService] singleton class is provide instance to Retrofit API repository class. Here [ApiService] class will use
     * this Retrofit function to communicate with HTTP network cloud
     *****************************************************************************/
    @Singleton
    @Provides
    fun provideRetrofitService(): ApiService {
        return providesRetrofitInstance().create(ApiService::class.java)
    }


}

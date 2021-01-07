package com.wordpress.herovickers.omup.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wordpress.herovickers.omup.api.interfaces.ApiService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.wordpress.herovickers.omup.api.AppConstants.BASE_URL;

@Module
public class HttpModule {

    @Provides
    @Singleton
    OkHttpClient provideHttpLogging() {
   HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
 logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        //logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(280, TimeUnit.SECONDS)
                .writeTimeout(280, TimeUnit.SECONDS)
                .readTimeout(280, TimeUnit.SECONDS)
                .build();
    }
    @Provides
    @Singleton
     Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL )
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }
    @Provides
    @Singleton
    ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

///////////// FOR JAVA


    public static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();

    private static Retrofit retrofit = null;
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static  Retrofit getAppClient() {
        if (retrofit == null) {


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }





}

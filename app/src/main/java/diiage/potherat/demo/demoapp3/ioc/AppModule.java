package diiage.potherat.demo.demoapp3.ioc;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import diiage.potherat.demo.demoapp3.dal.repository.QuoteRepository;
import diiage.potherat.demo.demoapp3.dal.repository.SWRepository;
import diiage.potherat.demo.demoapp3.dal.retrofit.SwRetrofit;
import diiage.potherat.demo.demoapp3.dal.retrofit.livedata.LiveDataCallAdapterFactory;
import diiage.potherat.demo.demoapp3.dal.room.Converters;
import diiage.potherat.demo.demoapp3.dal.room.QuotesDatabase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@InstallIn(ApplicationComponent.class)
@Module
public class AppModule {

    @Singleton
    @Provides
    public QuotesDatabase providesDatabase(@ApplicationContext Context context){
        return Room.inMemoryDatabaseBuilder(context, QuotesDatabase.class)
                .build();
    }

    @Provides
    public QuoteRepository quoteRepository(QuotesDatabase quotesDatabase){
        return quotesDatabase.quoteDao();
    }

    @Provides
    public ExecutorService executorService(){
        return Executors.newCachedThreadPool();
    }

    @Provides
    SWRepository swRepository(){
        // https://medium.com/@saquib3705/consuming-rest-api-using-retrofit-library-with-the-help-of-mvvm-dagger-livedata-and-rxjava2-in-67aebefe031d
        // https://github.com/pivincii/livedata_retrofit

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request request = chain.request();
            Request newRequest;

            newRequest = request.newBuilder()
                    .addHeader("Accept", "application/json")
                    .build();
            Log.d("HTTP","call "+newRequest.toString());
            return chain.proceed(newRequest);
        })
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS);

        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://swapi.dev/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(client)
                .build();

        return retrofit.create(SwRetrofit.class);
    }
}

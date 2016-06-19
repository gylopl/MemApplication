package makdroid.memapplication.dagger;

import android.app.Application;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import makdroid.memapplication.services.MemeRetrofitService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Grzecho on 14.06.2016.
 */
@Module
public class AppModule {

    Application mApplication;
    String mBaseUrl;

    public AppModule(Application application, String baseUrl) {
        mApplication = application;
        mBaseUrl = baseUrl;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public Bus provideBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mBaseUrl)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    MemeRetrofitService provideMemeRetrofitService(Retrofit retrofit) {
        return retrofit.create(MemeRetrofitService.class);
    }
}

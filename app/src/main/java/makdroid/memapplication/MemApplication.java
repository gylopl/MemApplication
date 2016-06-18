package makdroid.memapplication;

import android.app.Application;

import makdroid.memapplication.dagger.AppComponent;
import makdroid.memapplication.dagger.AppModule;
import makdroid.memapplication.dagger.DaggerAppComponent;

/**
 * Created by Grzecho on 14.06.2016.
 */
public class MemApplication extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this, "https://api.imgflip.com"))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}

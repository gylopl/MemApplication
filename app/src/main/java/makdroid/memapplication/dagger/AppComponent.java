package makdroid.memapplication.dagger;

import javax.inject.Singleton;

import dagger.Component;
import makdroid.memapplication.activities.MainActivity;

/**
 * Created by Grzecho on 14.06.2016.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(MainActivity mainActivity);

}

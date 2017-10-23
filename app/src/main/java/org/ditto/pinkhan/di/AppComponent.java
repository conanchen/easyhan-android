package org.ditto.pinkhan.di;

import android.app.Application;

import org.ditto.feature.my.di.MyModule;
import org.ditto.feature.word.di.WordModule;
import org.ditto.feature.visitor.di.VisitorModule;
import org.ditto.pinkhan.EasyhanApplication;
import org.ditto.lib.usecases.di.UsecaseModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,

        AppModule.class,

        MainActivityModule.class,
        WordModule.class,
        MyModule.class,
        VisitorModule.class,

        UsecaseModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(EasyhanApplication githubApp);
}
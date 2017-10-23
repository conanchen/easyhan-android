package org.ditto.lib.system.di;

import android.content.Context;

import org.ditto.lib.system.SystemService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Conan Chen on 29/9/17.
 */
@Singleton
@Module()
public class SystemModule {

    @Singleton
    @Provides
    public SystemService provideSystemService(Context context) {
        return new SystemService(context);
    }
}
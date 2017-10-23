package org.ditto.lib.repository.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.v4.util.Pair;

public class LiveDataAndStatus<DataType> extends MediatorLiveData<Pair<DataType, Status>> {
    public LiveDataAndStatus(LiveData<DataType> dataLiveData, LiveData<Status> errorLiveData) {
        addSource(dataLiveData, data -> setValue(Pair.create(data, errorLiveData.getValue())));
        addSource(errorLiveData, error -> setValue(Pair.create(dataLiveData.getValue(), error)));
    }
}
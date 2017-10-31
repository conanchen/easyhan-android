package org.ditto.feature.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;

import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {
    private final static String TAG = LoginViewModel.class.getSimpleName();
    @VisibleForTesting
    final MutableLiveData<String> mutableRequestWord = new MutableLiveData<String>();
    @VisibleForTesting
    final MutableLiveData<String> mutableRequestUpsertMyWord = new MutableLiveData<String>();

    private final LiveData<Pair<Word, Boolean>> liveImageIndexForUpsert;
    private final LiveData<Status> liveUpsertStatus;

    public LiveData<Pair<Word, Boolean>> getLiveImageIndexForUpsert() {
        return liveImageIndexForUpsert;
    }

    public LiveData<Status> getLiveUpsertStatus() {
        return liveUpsertStatus;
    }

    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public LoginViewModel() {
        liveImageIndexForUpsert = Transformations.switchMap(mutableRequestWord, (String requestWord) -> {
            LiveData<Word> liveData = usecaseFascade.repositoryFascade.wordRepository.find(requestWord);

            return new MediatorLiveData<Pair<Word, Boolean>>() {
                {
                    addSource(liveData, indexImage -> {
                        if (indexImage == null) {
//                            indexImage = Word
//                                    .builder()
//                                    .setUrl(Strings.isNullOrEmpty(url) ? "http://" : url)
//                                    .setInfoUrl("http://")
//                                    .setTitle("")
//                                    .setType(ImageType.SECRET.name())
//                                    .setCreated(System.currentTimeMillis())
//                                    .setLastUpdated(System.currentTimeMillis())
//                                    .setActive(false)
//                                    .setToprank(false)
//                                    .build();
                            setValue(new Pair<>(indexImage, Boolean.FALSE));
                        } else {
                            setValue(new Pair<>(indexImage, Boolean.TRUE));
                        }
                    });
                }
            };
        });

        liveUpsertStatus = Transformations.switchMap(mutableRequestUpsertMyWord, (String word) -> {
            return usecaseFascade.repositoryFascade.wordRepository.upsertMyWord(word);
        });

    }

    public void upsertMyWord(String word) {
        mutableRequestUpsertMyWord.setValue(word);
    }

}
package org.ditto.feature.my.index;

import org.ditto.lib.dbroom.index.Word;
import org.ditto.lib.repository.model.Status;

public class MyLiveExamWordHolder {
    public final Word examWord;
    public final Status checkPinyin1Status;
    public final Status checkPinyin2Status;
    public final Status checkStrokesStatus;

    private MyLiveExamWordHolder(Word examWord, Status checkPinyin1Status, Status checkPinyin2Status, Status checkStrokesStatus) {
        this.examWord = examWord;
        this.checkPinyin1Status = checkPinyin1Status;
        this.checkPinyin2Status = checkPinyin2Status;
        this.checkStrokesStatus = checkStrokesStatus;
    }

    public static MyLiveExamWordHolder create(
            Word examWord,
            Status checkPinyin1Status,
            Status checkPinyin2Status,
            Status checkStrokesStatus
    ) {
        return new MyLiveExamWordHolder(examWord, checkPinyin1Status, checkPinyin2Status, checkStrokesStatus);
    }
}

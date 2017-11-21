package org.ditto.feature.my.crawler;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mellychen on 2017/11/19.
 */

@JsonPropertyOrder(value = {"word", "pinyins", "radical", "fiveElem", "strokesNum",
        "strokes", "basicMeanings", "detailMeanings", "wordGroups", "riddles", "englishMeanings"})
public class WordBaidu {
    public String word;
    public List<String> pinyins = new ArrayList<>(0);
    public String radical;
    public String fiveElem;
    public Integer strokesNum;
    public List<String> strokes = new ArrayList<>(0);
    public List<String> basicMeanings = new ArrayList<>(0);
    public List<String> detailMeanings = new ArrayList<>(0);
    public List<String> wordGroups = new ArrayList<>(0);
    public List<String> riddles = new ArrayList<>(0);
    public List<String> englishMeanings = new ArrayList<>(0);
}

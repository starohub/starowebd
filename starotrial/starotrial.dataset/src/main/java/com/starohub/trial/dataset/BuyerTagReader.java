package com.starohub.trial.dataset;

import java.io.Reader;

public class BuyerTagReader extends com.starohub.platies.stream.TagJsonReader {
    public BuyerTagReader(String json) {
        super(json);
    }

    public BuyerTagReader(Reader in) {
        super(in);
    }

    @Override
    public Object tagValue(String s) {
        if (s.contains("__b__")) {
            return s.replaceAll("__b__", "John");
        }
        if (s.contains("__c__")) {
            return 32.56;
        }
        return s;
    }

    @Override
    public String tagField(String s) {
        if (s.contains("__a__")) {
            return "a";
        }
        return s;
    }
}

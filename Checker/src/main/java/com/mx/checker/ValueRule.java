package com.mx.checker;

import java.util.HashMap;

public abstract class ValueRule extends StringChecker.BaseRule {

    public ValueRule(HashMap<String, StringChecker.Item> englishStrings) {
        super(englishStrings);
    }

    @Override
    public boolean isLegal(String key, String value) {
        StringChecker.Item item = englishStrings.get(key);

        return check(item.value, value);
    }

    protected abstract boolean check(String english, String other);
}

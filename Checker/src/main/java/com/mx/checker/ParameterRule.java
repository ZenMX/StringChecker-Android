package com.mx.checker;

import com.mx.checker.StringChecker.Item;

import java.util.HashMap;

public class ParameterRule extends ValueRule {
    public ParameterRule(HashMap<String, Item> englishStrings) {
        super(englishStrings);
    }

    @Override
    public String desc() {
        return "parameters should be the same with english.";
    }

    @Override
    protected boolean check(String english, String other) {
        if (english.indexOf('%') == -1) {
            if (other.indexOf('%') == -1)
                return true;

            else return false;
        } else {
            return findCount(english) == findCount(other);
        }
    }

    private static int findCount(String string) {
        int count = 0;
        while (true) {
            int index = string.indexOf('%');
            if (index == -1)
                break;
            if (index == 0) {
                count++;
            } else {
                if (string.charAt(index - 1) != '\\') {
                    count++;
                }
            }

            string = string.substring(index + 1);
        }

        return count;
    }
}

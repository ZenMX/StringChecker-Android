package com.mx.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParameterDuplicateRule extends ValueRule {
    private static final String[] PARAM_SYNTAX = {"%n$d", "%n$s", "%n$f"};
    public ParameterDuplicateRule(HashMap<String, StringChecker.Item> englishStrings) {
        super(englishStrings);
    }

    @Override
    protected boolean check(String english, String other) {
        if(!CheckParameterIndex(english))
            return  false;
        if(!CheckParameterIndex(other))
            return  false;
        return true;
    }

    @Override
    public String desc() {
        return "parameter index duplicate in english or localized.";
    }

    private static boolean CheckParameterIndex(String string){
        int syntaxIndex = 0, digit = 0;
        List<Integer> indexList = new ArrayList();
        for(int j=0; j< PARAM_SYNTAX.length;j++) {
            String syntax = PARAM_SYNTAX[j];
            for (int i = 0; i < string.length(); i++) {
                if (syntax.charAt(syntaxIndex) == 'n') {
                    if (Character.isDigit(string.charAt(i))) {
                        syntaxIndex++;
                        digit = Integer.parseInt(String.valueOf(string.charAt(i)));
                    } else
                        syntaxIndex = 0;
                } else {
                    if (string.charAt(i) == syntax.charAt(syntaxIndex))
                        syntaxIndex++;
                    else
                        syntaxIndex = 0;
                }
                if (syntaxIndex == syntax.length()) {
                    if (indexList.contains(digit))
                        return false;
                    else {
                        indexList.add(digit);
                        syntaxIndex = 0;
                    }
                }
            }
        }
        return true;
    }
}

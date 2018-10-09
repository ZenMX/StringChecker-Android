package com.mx.checker;

import com.mx.checker.StringChecker.Item;

import java.util.HashMap;

public class ParameterRule extends ValueRule {
    //assume parameter less than 10
    public static final String[] PARAM_SYNTAX = {"%n$d", "%n$s", "%n$f"};
    public ParameterRule(HashMap<String, Item> englishStrings) {
        super(englishStrings);
    }

    @Override
    public String desc() {
        return "parameters should be the same with english.";
    }

    @Override
    protected boolean check(String english, String other) {
        for(int i=0; i< PARAM_SYNTAX.length;i++){
            if(findCount(english, PARAM_SYNTAX[i]) != findCount(other, PARAM_SYNTAX[i]))
                return  false;
        }
        return true;
    }

    private static int findCount(String string, String syntax){
        int syntaxIndex = 0, count = 0;
        for(int i=0; i< string.length(); i++){
            if(syntax.charAt(syntaxIndex) =='n'){
                if(Character.isDigit(string.charAt(i)))
                    syntaxIndex++;
                else
                    syntaxIndex = 0;
            }else {
                if (string.charAt(i) == syntax.charAt(syntaxIndex))
                    syntaxIndex++;
                else
                    syntaxIndex = 0;
            }
            if(syntaxIndex == syntax.length()) {
                count++;
                syntaxIndex = 0;
            }
        }
        return count;
    }

//    @Override
//    protected boolean check(String english, String other) {
//        if (english.indexOf('%') == -1) {
//            if (other.indexOf('%') == -1)
//                return true;
//
//            else return false;
//        } else {
//            return findCount(english) == findCount(other);
//        }
//    }

//    private static int findCount(String string) {
//        int count = 0;
//        while (true) {
//            int index = string.indexOf('%');
//            if (index == -1)
//                break;
//            if (index == 0) {
//                count++;
//            } else {
//                if (string.charAt(index - 1) != '\\') {
//                    count++;
//                }
//            }
//
//            string = string.substring(index + 1);
//        }
//
//        return count;
//    }
}

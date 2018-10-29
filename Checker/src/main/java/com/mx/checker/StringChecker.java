package com.mx.checker;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class StringChecker {

    public static interface Rule {
        boolean isLegal(String key, String value);
        String desc();
    }

    public static class Item {
        public final String key;
        public final String value;
        public final String file;

        public Item(String key, String value, String file) {
            this.key = key;
            this.value = value;
            this.file = file;
        }
    }

    /**
     * dir
     * @param args
     */

    public static void main(String[] args) {
        System.out.println("string checker ----------");
        try {

        StringChecker stringChecker = new StringChecker(args[0]);
//            StringChecker stringChecker = new StringChecker("/Users/lin/Documents/android/mx/StringChecker/app");
            stringChecker.check();
        }catch (Exception e) {
            System.out.println("string checker -------error");
            e.printStackTrace(System.out);
            System.out.println();
        }

        System.out.println("string checker --------end");
    }

    private String dir;
    private HashMap<String, Item> englishStrings;
    private List<Rule> rules;

    public StringChecker(String dir) throws IOException {
        this.dir = new File(dir).getCanonicalPath();
        init();
    }

    public void check() {
        File file = new File(dir);

        collectEnglishStrings(file);

//        List<File> files = findFiles(file);
//        for (File singleFile : files) {
//            checkFile(singleFile.getAbsolutePath());
//        }
        List<File> languages = findLanguages(file);
        for(File singleLanguage:languages){
            checkLanguage(singleLanguage);
        }
    }

    private List<File> findLanguages(File dir){
        File file = new File(dir, "/src/main/res");
        File[] files = file.listFiles();
        ArrayList<File> list = new ArrayList<>();
        for (File file1 : files) {
            if (file1.getName().indexOf("values-") != -1) {
                list.add(file1);
            }
        }
        return list;
    }

//    private List<File> findFiles(File dir) {
//        File file = new File(dir, "/src/main/res");
//        File[] files = file.listFiles();
//        ArrayList<File> list = new ArrayList<>();
//        for (File file1 : files) {
//            if (file1.getName().indexOf("values-") != -1) {
//
//                File[] files1 = file1.listFiles();
//                for (File file2 : files1) {
//                    list.add(file2);
//                }
//            }
//        }
//        return list;
//    }

    private void init() {
        englishStrings = new HashMap<>();
        rules = new ArrayList<>();

        //add rules
        rules.add(new ExistRule(englishStrings));
        rules.add(new ParameterRule(englishStrings));
    }

    private void collectEnglishStrings(File dir) {
        File file = new File(dir, "/src/main/res/values");
        File[] files = file.listFiles();

        for (File file1 : files) {
            Set<Item> items = parseFile(file1.getAbsolutePath());
            for (Item item : items) {
                 englishStrings.put(item.key, item);
            }
        }
    }

    private void checkLanguage(File dir){
        System.out.println("\ncheck language ---->" + dir.getAbsolutePath());
        File[] files = dir.listFiles();
        Set<Item> items = new HashSet<>();
        for(File f:files)
            items.addAll(parseFile(f.getAbsolutePath()));

        for (Item item : items) {
            for (Rule rule : rules) {
                if (!rule.isLegal(item.key, item.value)) {
                    Item item1 = englishStrings.get(item.key);
                    System.out.println("\terror:" + rule.desc() + " at string file =  " + item.file + ", key = " + item.key + ", value = " + item.value + " original: " + (item1 != null ? item1.value : ""));
//                    break;
                }
            }
        }

//        LocalizedRule localizedRule = new LocalizedRule(items);
//        for (Item item : englishStrings.values()) {
//            if(!localizedRule.isLegal(item.key, null)){
//                System.out.println("\terror:" + localizedRule.desc() + " at string file =  " + item.file + ", key = " + item.key + ", value = " + item.value);
//                break;
//            }
//
//        }
    }

//    private void checkFile(String filename) {
//        System.out.println("check file ---->" + filename);
//        Set<Item> items = parseFile(filename);
//
//        for (Item item : items) {
//            for (Rule rule : rules) {
//                if (!rule.isLegal(item.key, item.value)) {
//                    System.out.println("error:" + rule.desc() + " at string key =  " + item.key + ", value = " + item.value);
//                    break;
//                }
//            }
//        }
//
//        LocalizedRule localizedRule = new LocalizedRule(items);
//        for (String key : englishStrings.keySet()) {
//            if(!localizedRule.isLegal(key, null)){
//                System.out.println("error:" + localizedRule.desc() + " at string key =  " + key);
//                break;
//            }
//
//        }
//    }

    private Set<Item> parseFile(String filename) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document parse = documentBuilder.parse(filename);
            parse.getDocumentElement().normalize();
            NodeList strings = parse.getElementsByTagName("string");
            HashSet<Item> hashSet = new HashSet<>();
            for (int i = 0; i < strings.getLength(); i++) {
                Node item = strings.item(i);
                NamedNodeMap attributes = item.getAttributes();
                Node firstChild = item.getFirstChild();
                if (firstChild == null)
                    continue;

                String nodeValue = firstChild.getNodeValue();
                Node name = attributes.getNamedItem("name");
                String nodeName = name.getNodeValue();
                hashSet.add(new Item(nodeName, nodeValue, filename.substring(filename.lastIndexOf('/')+1)));
            }

            return hashSet;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract static class BaseRule implements Rule {
        public final HashMap<String, Item> englishStrings;

        public BaseRule(HashMap<String, Item> englishStrings) {
            this.englishStrings = englishStrings;
        }
    }

    private static class ExistRule extends BaseRule {

        public ExistRule(HashMap<String, Item> englishStrings) {
            super(englishStrings);
        }

        @Override
        public boolean isLegal(String key, String value) {
            Item item = englishStrings.get(key);
            return item != null;
        }

        @Override
        public String desc() {
            return "string should presents in english.";
        }
    }

    private static class LocalizedRule implements Rule {
        public final Set<Item> localizedStrings;

        public LocalizedRule(Set<Item> localizedStrings){
            this.localizedStrings = localizedStrings;
        }

        @Override
        public boolean isLegal(String key, String value) {
            for(Item item:localizedStrings){
                if(item.key.equals(key))
                    return true;
            }
            return false;
        }

        @Override
        public String desc() {
            return "String is not localized.";
        }
    }
}

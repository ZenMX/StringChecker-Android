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

        public Item(String key, String value) {
            this.key = key;
            this.value = value;
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

        List<File> files = findFiles(file);
        for (File singleFile : files) {
            checkFile(singleFile.getAbsolutePath());
        }
    }

    private List<File> findFiles(File dir) {
        File file = new File(dir, "/src/main/res");
        File[] files = file.listFiles();
        ArrayList<File> list = new ArrayList<>();
        for (File file1 : files) {
            if (file1.getName().indexOf("values-") != -1) {

                File[] files1 = file1.listFiles();
                for (File file2 : files1) {
                    list.add(file2);
                }
            }
        }
        return list;
    }

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

    private void checkFile(String filename) {
        System.out.println("check file ---->" + filename);
        Set<Item> items = parseFile(filename);

        for (Item item : items) {
            for (Rule rule : rules) {
                if (!rule.isLegal(item.key, item.value)) {
                    System.out.println(rule.desc() + " : " + item.key + " : " + item.value);
                }
            }
        }
    }

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
                hashSet.add(new Item(nodeName, nodeValue));
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
}

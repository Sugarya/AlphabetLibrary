package com.sugarya.example.alphabetdemo.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by demo001 on 2015/8/5.
 */
public class ParserUtils {


    public static LinkedHashMap<String, Object> parseToHashMap(InputStream stream) {


        SAXParserFactory factorys = SAXParserFactory.newInstance();
        SAXParser saxparser = null;
        try {
            saxparser = factorys.newSAXParser();
            PlistHandler plistHandler = new PlistHandler();
            saxparser.parse(stream, plistHandler);
            LinkedHashMap<String, Object> hash = plistHandler.getMapResult();

            return hash;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new LinkedHashMap<String, Object>();


    }

    public static ArrayList<String> parseToArrayList(InputStream stream) {
        SAXParserFactory factorys = SAXParserFactory.newInstance();
        SAXParser saxparser = null;
        try {
            saxparser = factorys.newSAXParser();
            PlistHandler plistHandler = new PlistHandler();
            saxparser.parse(stream, plistHandler);
            ArrayList<String> array = plistHandler.getArrayResult();

            return array;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ArrayList<String>();
    }

    public static ArrayList<String> parseToSimpleArrayList(InputStream stream) {
        SAXParserFactory factorys = SAXParserFactory.newInstance();
        SAXParser saxparser = null;
        try {
            saxparser = factorys.newSAXParser();
            PlistHandler plistHandler = new PlistHandler();
            saxparser.parse(stream, plistHandler);
            ArrayList<String> array = plistHandler.getArraySimpleResult();

            return array;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ArrayList<String>();
    }


    public static class PlistHandler extends DefaultHandler {

        private boolean isRootElement = false;

        private boolean keyElementBegin = false;

        private String key;

        Stack<Object> stack = new Stack<Object>();

        private boolean valueElementBegin = false;

        private Object root;

        @SuppressWarnings("unchecked")
        public LinkedHashMap<String, Object> getMapResult() {
            return (LinkedHashMap<String, Object>) root;
        }

        @SuppressWarnings("unchecked")
        public ArrayList<String> getArrayResult() {
            ArrayList<String> ret = new ArrayList<>();
            LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) this.root;
            Set<String> stringSet = linkedHashMap.keySet();
            for (String key : stringSet) {
                ret.add(key);
                Object obj = linkedHashMap.get(key);
                if (obj instanceof ArrayList) {
                    ArrayList<String> value = (ArrayList<String>) obj;
                    ret.addAll(value);
                } else if (obj instanceof String) {
                    String value = (String) obj;
                    ret.add(value);
                }
            }
            return ret;
        }

        public ArrayList<String> getArraySimpleResult() {
            ArrayList<String> ret = new ArrayList<>();
            LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) this.root;
            Set<String> stringSet = linkedHashMap.keySet();
            for (String key : stringSet) {
                ret.add(key);
                Object obj = linkedHashMap.get(key);
                if (obj instanceof ArrayList) {
                    ArrayList<String> value = (ArrayList<String>) obj;
                    for (String str : value) {
                        String[] strings = str.split("\\+");
                        if (strings.length > 0) {
                            ret.add(strings[0]);
                        }
                    }
                } else if (obj instanceof String) {
                    String value = (String) obj;
                    String[] strings = value.split("\\+");
                    if (strings != null) {
                        ret.add(strings[0]);
                    }
                }
            }
            return ret;
        }

        @Override
        public void startDocument() throws SAXException {
            //LogUtils.model(TAG, "开始解析");
        }

        @Override
        public void endDocument() throws SAXException {
            //LogUtils.model(TAG,"结束解析");
        }

        @SuppressWarnings("unchecked")
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            //LogUtils.model(TAG,"uri="+uri + "  startElement:" + qName);
            if ("plist".equals(qName)) {
                isRootElement = true;
            }
            if ("dict".equals(qName)) {
                if (isRootElement) {
                    stack.push(new LinkedHashMap<String, Object>());// ѹջ
                    isRootElement = !isRootElement;
                } else {
                    Object object = stack.peek();
                    LinkedHashMap<String, Object> dict = new LinkedHashMap<String, Object>();
                    if (object instanceof ArrayList)
                        ((ArrayList<Object>) object).add(dict);
                    else if (object instanceof HashMap)
                        ((LinkedHashMap<String, Object>) object).put(key, dict);
                    stack.push(dict);
                }
            }

            if ("key".equals(qName)) {
                keyElementBegin = true;
            }
            if ("true".equals(qName)) {
                LinkedHashMap<String, Object> parent = (LinkedHashMap<String, Object>) stack.peek();
                parent.put(key, true);
            }
            if ("false".equals(qName)) {
                LinkedHashMap<String, Object> parent = (LinkedHashMap<String, Object>) stack.peek();
                parent.put(key, false);
            }
            if ("array".equals(qName)) {
                if (isRootElement) {
                    ArrayList<Object> obj = new ArrayList<Object>();
                    stack.push(obj);
                    isRootElement = !isRootElement;
                } else {
                    LinkedHashMap<String, Object> parent = (LinkedHashMap<String, Object>) stack.peek();
                    ArrayList<Object> obj = new ArrayList<Object>();
                    stack.push(obj);
                    parent.put(key, obj);
                }
            }
            if ("string".equals(qName)) {
                valueElementBegin = true;
            }
        }

        /*
         * �ַ�������(non-Javadoc)
         *
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @SuppressWarnings("unchecked")
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            //LogUtils.model(TAG,"characters:");
            if (length > 0) {
                if (keyElementBegin) {
                    key = new String(ch, start, length);
                    //LogUtils.model(TAG,"key:" + key);
                }
                if (valueElementBegin) {
                    if (LinkedHashMap.class.equals(stack.peek().getClass())) {
                        LinkedHashMap<String, Object> parent = (LinkedHashMap<String, Object>) stack.peek();
                        String value = new String(ch, start, length);
                        parent.put(key, value);
                    } else if (ArrayList.class.equals(stack.peek().getClass())) {
                        ArrayList<Object> parent = (ArrayList<Object>) stack.peek();
                        String value = new String(ch, start, length);
                        parent.add(value);
                    }
                    //LogUtils.model(TAG,"value:" + new String(ch, start, length));
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("plist".equals(qName)) {

            }
            if ("key".equals(qName)) {
                keyElementBegin = false;
            }
            if ("string".equals(qName)) {
                valueElementBegin = false;
            }
            if ("array".equals(qName)) {
                root = stack.pop();
            }
            if ("dict".equals(qName)) {
                root = stack.pop();
            }
        }

    }

}

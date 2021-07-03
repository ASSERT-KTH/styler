//package com.nespresso.git.training;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
//
//public class MyClass {
//
//    public static void main(String [] args){
//        List<Integer> list = new ArrayList<Integer>();
//        Collections.addAll(list, 1, 2, 3);
//
//        //java.util.ConcurrentModificationException
////        for (Integer integer : list) {
////            list.remove(0);
////            list.add(integer);
////        }
//        //Solution:
//        Iterator<Integer> iterator = list.iterator();
//        while(iterator.hasNext()){
//            iterator.next();    //Mandatory
//            iterator.remove();
//        }
//        System.out.println(list.size());
//        //Unmodifiable returned list (or Set, Map)
//        List<Object> emptyList = Collections.emptyList();
//        Collections.frequency(list, new Integer(2));
//
//        String [] tabOfString = {"hhhh", "aaaa", "dddd", "fffff"};
//        Iterator<String> stringIterator = iterator(tabOfString);
//        while(stringIterator.hasNext()){
//        	System.out.println(stringIterator.next());
//        }
//    }
//
//    public static Iterator<String> iterator(final String [] string){
//        return new Iterator<String>() {
//            int index = 0;
//            @Override
//            public boolean hasNext() {
//                return index < string.length;
//            }
//
//            @Override
//            public String next() {
//                return string[index++];
//            }
//
//            @Override
//            public void remove() {
//                throw new UnsupportedOperationException();
//            }
//        };
//    }
//
//    public static boolean isPalindrom(String string) {
//
//        for (int i = 0, j = string.length() -1 ; i <= j ; i++, j--) {
//            if(string.charAt(i) != string.charAt(j))
//                return false;
//        }
//        return true;
//    }
//
//    public static long extractDigits(String word){
//        return Long.parseLong(word.replaceAll("\\D+", ""));
//    }
//
//    public static String firstLetterToUpperCase(String statement){
//        StringBuilder builder = new StringBuilder(statement);
//        boolean newSpace = true;
//        for (int i = 0; i < builder.length(); i++) {
//            if(builder.charAt(i) ==  ' '){
//                newSpace = true;
//            }else if(newSpace){
//                builder.setCharAt(i, ((Character.toUpperCase(builder.charAt(i)))));
//                newSpace = false;
//            }
//        }
//        return builder.toString();
//    }
//
//    public static String firstLetterToUpperCaseSplit(String statement, String seperator){
//        String [] listWords = statement.split(seperator+"+");
//        System.out.println("listWords.length : "+listWords.length);
//        StringBuilder builder = new StringBuilder();
//        for (String word : listWords) {
//            builder.append(Character.toUpperCase(word.charAt(0)) + word.substring(1));
//        }
//        return builder.toString();
//
//    }
//    public static String firstLetterToUpperCaseWithSeperator(String statement, char seperator) {
//        StringBuilder builder = new StringBuilder(statement);
//        boolean newSeperatorOccurence = true;
//        for (int i = 0; i < builder.length(); i++) {
//            if(builder.charAt(i) ==  seperator){
//                newSeperatorOccurence = true;
//            }else if(newSeperatorOccurence){
//                builder.setCharAt(i, ((Character.toUpperCase(builder.charAt(i)))));
//                newSeperatorOccurence = false;
//            }
//        }
//        return builder.toString();
//    }
//
//    public static String getBiggestHeterogeneousSubString(String string){
//        StringBuilder biggestSubString = new StringBuilder();
//        StringBuilder buffer = new StringBuilder();
//        for (int i = 0; i < string.length(); i++) {
//            if(buffer.toString().contains(string.charAt(i)+"")){
//                buffer.append(string.charAt(i));
//            }else if(buffer.length() > biggestSubString.length()){
//                biggestSubString = new StringBuilder(buffer.toString());
//                buffer.setLength(0);
//            }
//        }
//        return biggestSubString.toString();
//    }
//}

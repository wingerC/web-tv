package com.winger.webtv.util;

import java.util.HashMap;
import java.util.Map;

public class Translit {

    private final Map<Character,String> map;
    private static Translit instance;

    private Translit() {
        map = new HashMap<>();
        createMap();
    }

    public static Translit getInstance(){
        if (instance == null){
            instance = new Translit();
        }
        return instance;
    }

    private void createMap(){
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "e");
        map.put('ж', "zh");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "i");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "h");
        map.put('ц', "c");
        map.put('ч', "ch");
        map.put('ш', "sh");
        map.put('щ', "sh");
        map.put('ъ', "y");
        map.put('ы', "y");
        map.put('ь', "");
        map.put('э', "e");
        map.put('ю', "yu");
        map.put('я', "ya");
    }

    public String translate (String word) {
        String res = "";
        for (char c : word.toCharArray()){
            res += map.getOrDefault(c, String.valueOf(c));
        }
        return res;
    }
}

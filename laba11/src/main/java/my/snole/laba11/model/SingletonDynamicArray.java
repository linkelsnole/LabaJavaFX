package my.snole.laba11.model;

import my.snole.laba11.model.Ant.Ant;

import java.util.ArrayList;
import java.util.List;

public class SingletonDynamicArray {
    private static List<Ant> elements = new ArrayList<>();;
    private static SingletonDynamicArray instance = null;
    private SingletonDynamicArray() {
    }



    public static SingletonDynamicArray getInstance() {
        if(instance==null) {
            instance = new SingletonDynamicArray();
        }
        return instance;
    }

    public static List<Ant> getElements() {
        return elements;
    }

    public static void addElement(Ant element) {
        elements.add(element);
    }
    public static void clear(){
        elements.clear();
    }
}


package my.snole.laba11.model;

import my.snole.laba11.model.Ant.Ant;

import java.util.ArrayList;
import java.util.List;

public class SingletonDynamicArray {
    private static List<Ant> elements;

    private SingletonDynamicArray() {
    }

    private static class SingletonDynamicArrayHolder {
        private static final SingletonDynamicArray instance = new SingletonDynamicArray();
    }

    public static SingletonDynamicArray getInstance() {
        elements = new ArrayList<>();
        return SingletonDynamicArrayHolder.instance;
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


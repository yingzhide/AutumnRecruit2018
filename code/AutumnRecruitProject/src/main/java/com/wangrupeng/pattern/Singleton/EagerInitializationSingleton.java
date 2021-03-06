package com.wangrupeng.pattern.Singleton;

public class EagerInitializationSingleton {
    private static EagerInitializationSingleton instance = new EagerInitializationSingleton();
    private static boolean flag = true;
    private EagerInitializationSingleton() {
        if (!flag) {
            flag = !flag;
        } else {
            throw new RuntimeException("Could not create the instance, cause there's already have.");
        }
    }
    public static EagerInitializationSingleton getInstance() {
        return instance;
    }

    public static void main(String[] args) {

    }
}

package io.github.skydynamic.maiprober.main;

import io.github.skydynamic.maiprober.util.platform.windows.WindowsPlatformImpl;

import java.util.Scanner;

public class Bootstrap {
    public static void main(String[] args) {
        WindowsPlatformImpl wp = new WindowsPlatformImpl();
        System.out.println("Set system proxy to 11451");
        wp.setupSystemProxy("localhost:11451");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println("Rolling back");
        wp.rollbackSystemProxy();
    }
}

package ru.sua.fs6926;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Launcher {
    static boolean isAscending = true;
    static boolean isStrings = true;
    static String outputfile = "";
    static List<String> inputfiles = null;

    public static void main(String[] args) {
        new ParseCommandLine(args).invoke();
        log.info("РАБОТА:");
    }
}

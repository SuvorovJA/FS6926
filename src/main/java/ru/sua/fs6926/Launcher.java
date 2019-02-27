package ru.sua.fs6926;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Launcher {
    static boolean isAscending = true;
    static boolean isStrings = true;
    static String outputFileName = "";
    static String encoding = "utf-8";
    static List<String> inputFileNames = new ArrayList<>();

    public static void main(String[] args) {

        new ParseCommandLine(args).parse();

        if (isStrings) {
            try (Sorter<String> sorter = new SorterImpl<>(outputFileName, encoding);
                 WorkersHolder<String> holder = new WorkersHolder<>(sorter);) {
                holder.doWork();
            } catch (IOException e) {
                log.error("Проблема при закрытии файлов \'{}\'",e.getMessage());
            }
        } else {
            try (Sorter<Integer> sorter = new SorterImpl<>(outputFileName, encoding);
                 WorkersHolder<Integer> holder = new WorkersHolder<>(sorter);) {
                holder.doWork();
            } catch (IOException e) {
                log.error("Проблема при закрытии файлов \'{}\'",e.getMessage());
            }
        }
    }
}

package com.shrb.versionowner.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class TestDemo {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
            "yyyyMMdd");

    public Date parseDate(String value) throws ParseException {
        return SIMPLE_DATE_FORMAT.parse(value);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService exec = Executors.newFixedThreadPool(availableProcessors);
        List<Future<Date>> results = new ArrayList<>();
        final TestDemo sdf = new TestDemo();
        Callable<Date> parseDateTask = new Callable<Date>() {
            public Date call() throws Exception {
                return sdf.parseDate("20161118");
            }
        };
        for (int i = 0; i < 10; i++) {
            Thread.sleep(0);
            results.add(exec.submit(parseDateTask));
        }
        exec.shutdown();
        for (Future<Date> result : results) {
            System.out.println(result.get());
        }

    }
}

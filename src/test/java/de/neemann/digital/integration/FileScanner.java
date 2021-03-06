package de.neemann.digital.integration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author hneemann
 */
public class FileScanner {

    private Interface test;
    private ArrayList<Error> errors;

    public FileScanner(Interface test) {
        this.test = test;
    }

    public int scan(File path) throws Exception {
        errors = new ArrayList<>();
        int count;
        try {
            count = scanIntern(path);
        } catch (SkipAllException e) {
            System.out.println("all tests are skipped: " + e.getMessage());
            return -1;
        }
        System.out.println("tested " + count + " examples");
        if (errors.isEmpty())
            return count;

        System.err.println("errors: " + errors.size());
        for (Error e : errors) {
            System.err.println("----> error in: " + e.f);
            e.e.printStackTrace();
        }
        throw new Exception("errors testing files");
    }

    private int scanIntern(File path) throws IOException, SkipAllException {
        int count = 0;
        File[] files = path.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    if (f.getName().charAt(0) != '.') {
                        count += scanIntern(f);
                    }
                } else {
                    if (f.getName().endsWith(".dig")) {
                        try {
                            test.check(f);
                        } catch (SkipAllException e) {
                            throw e;
                        } catch (Throwable e) {
                            errors.add(new Error(f, e));
                        }
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public interface Interface {
        void check(File f) throws Exception;
    }

    private static class Error {

        private final File f;
        private final Throwable e;

        private Error(File f, Throwable e) {
            this.f = f;
            this.e = e;
        }
    }

    public static class SkipAllException extends Exception {
        public SkipAllException(String s) {
            super(s);
        }
    }
}

package com.skty.study.bTree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) throws IOException {
        BTree<Integer, String> tree = new BTree<>(3);
        List<Integer> addedElement = new ArrayList<>();
        generateElement(3, "tree\\src\\main\\java\\com\\skty\\study\\bTree\\lastInsert.txt", 30).forEach(e -> {
            addedElement.add(e);
            tree.insert(e, e.toString());
        });
        writeToFile(tree.printBTree() + "\n\n\n\n\n\n", "tree\\src\\main\\java\\com\\skty\\study\\bTree\\treePrint.txt", false);
        writeToFile(addedElement.stream().map(Objects::toString).collect(Collectors.joining(";")),
                "tree\\src\\main\\java\\com\\skty\\study\\bTree\\lastInsert.txt", false);
        System.out.println(tree.printBTree());
        System.out.println("==================================删除开始======================================");

        generateElement(3, "tree\\src\\main\\java\\com\\skty\\study\\bTree\\lastDelete.txt", 20).forEach(e -> {
            try {
                writeToFile(e + ";", "tree\\src\\main\\java\\com\\skty\\study\\bTree\\lastDelete.txt", true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("===================================" + e + "=====================================");
            tree.delete(e);
            System.out.println("\t|\n\t| " + e + "->" + e + "\n\t/");
            System.out.println(tree.printBTree());
            System.out.println("==================================end======================================");
        });
        writeToFile(tree.printBTree(), "tree\\src\\main\\java\\com\\skty\\study\\bTree\\treePrint.txt", true);
    }


    private static List<Integer> generateElement(int mode, String file, int num) throws IOException {
        List<Integer> els = new ArrayList<>();
        if (mode == 1) {
            for (int i = 0; i < num; i++) {
                els.add(i);
            }
        } else if (mode == 2) {
            for (int i = 0; i < num; i++) {
                els.add(new Random().nextInt(num));
            }
        } else {
            List<String> strings = Files.readAllLines(new File(file).toPath());
            strings.forEach(s -> {
                String[] split = s.split(";");
                for (String s1 : split) {
                    els.add(Integer.valueOf(s1));
                }
            });
        }
        writeToFile("", file, false);
        return els;
    }


    public static void writeToFile(String s, String fileName, boolean append) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, append))) {
            writer.print(s);
        }
    }

    private static void searchTree(int key, BTree tree) {
        long start = System.currentTimeMillis();
        Object o = tree.find(key);
        System.out.println("查找key:" + key + " 耗时:" + (System.currentTimeMillis() - start) + "  结果:" + (o == null ? 1 : 0));
    }

}

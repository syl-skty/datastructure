package com.skty.study.bTree;

public class Test {

    public static void main(String[] args) {
        BTree<Integer, String> tree = new BTree<>(4);
        for (int i = 1; i < 13; i++) {
            tree.insert(i, i + "");
            System.out.println("\t|\n\t|\n\t| "+i+"\n\t\\/");
            System.out.println(tree.printBTree());
            System.out.println("==========================================================================");
        }
    }
}

package com.skty.study.bTree;

public class Test {

    public static void main(String[] args) {
        BTree<Integer, String> tree = new BTree<>(3);
        for (int i = 1; i < 23; i++) {
            //int nextInt = new Random().nextInt(100000);
            System.out.println("===================================" + i + "=====================================");
            tree.insert(i, i + "");
            System.out.println("\t|\n\t| " + i + "->" + i + "\n\t/");
            System.out.println(tree.printBTree());
            System.out.println("==================================end======================================");
        }
        // System.out.println(tree.printBTree());
        System.out.println("==================================删除开始======================================");
        tree.delete(21);
        System.out.println(tree.printBTree());



      /*  int[] test = {41, 5, 96, 59, 52, 6, 82, 40, 1};
        for (int i : test) {
            System.out.println("===================================" + i + "=====================================");
            tree.insert(i, i + "");
            System.out.println("\t|\n\t| " + i + "->" + i + "\n\t/");
            System.out.println(tree.printBTree());
            System.out.println("==================================end======================================");
        }*/
    }


    private static void searchTree(int key, BTree tree) {
        long start = System.currentTimeMillis();
        Object o = tree.find(key);
        System.out.println("查找key:" + key + " 耗时:" + (System.currentTimeMillis() - start) + "  结果:" + (o == null ? 1 : 0));
    }

}

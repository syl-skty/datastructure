package com.skty.study.b_tree;

import java.util.Collections;
import java.util.List;

public class Node<E extends Element> {
    /**
     * 当前节点上的所有元素
     */
    private E[] elements;

    /**
     * 父节点
     */
    private Node<E> parentNode;

    /**
     * 子节点
     */
    private E[] childNode;

    /**
     * 当前节点元素数
     */
    private int elementNum;

}

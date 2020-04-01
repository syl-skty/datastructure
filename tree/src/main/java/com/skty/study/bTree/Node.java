package com.skty.study.bTree;

import java.util.Arrays;

class Node<K extends Comparable<K>, V> {

    /**
     * 节点大小，当前节点所能容纳所有元素的大小
     */
    private int nodeSize;
    /**
     * 当前节点上的所有元素
     */
    private Element<K, V>[] elements;

    /**
     * 父节点
     */
    private Node<K, V> parentNode;

    /**
     * 子节点
     */
    private Node<K, V> childNode;

    /**
     * 当前节点元素数
     */
    private int elementNum;

    /**
     * 是否为叶子节点
     */
    private boolean isLeafNode;

    Node() {
    }

    Node(Element<K, V>[] elements, int elementNum, boolean isLeafNode) {
        this.elements = elements;
        this.elementNum = elementNum;
        this.isLeafNode = isLeafNode;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    public Element<K, V>[] getElements() {
        return elements;
    }

    public void setElements(Element<K, V>[] elements) {
        this.elements = elements;
    }

    public Node<K, V> getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node<K, V> parentNode) {
        this.parentNode = parentNode;
    }

    public Node<K, V> getChildNode() {
        return childNode;
    }

    public void setChildNode(Node<K, V> childNode) {
        this.childNode = childNode;
    }

    public int getElementNum() {
        return elementNum;
    }

    public void setElementNum(int elementNum) {
        this.elementNum = elementNum;
    }

    Element<K, V> getElement(int index) {
        if (index < elementNum - 1) {
            return elements[index];
        } else {
            throw new IndexOutOfBoundsException("当前所取得节点超出节点元素大小");
        }
    }

    /**
     * 获取指定节点索引的左边节点
     *
     * @param index 目标索引
     * @return 所有左边子节点构成的数组
     */
    Element<K, V>[] getLeftElement(int index) {
        return Arrays.copyOfRange(elements, 0, index);
    }

    /**
     * 获取指定节点索引的右边节点
     *
     * @param index 目标索引
     * @return 所有右边子节点构成的数组
     */
    Element<K, V>[] getRightElement(int index) {
        return Arrays.copyOfRange(elements, index + 1, elementNum);
    }

    /**
     * 获取节点的逻辑大小
     *
     * @return 返回当前实际大小-1
     */
    int getLogicSize() {
        return nodeSize - 1;
    }

    public boolean isLeafNode() {
        return isLeafNode;
    }

    public void setLeafNode(boolean leafNode) {
        isLeafNode = leafNode;
    }

    /**
     * 判断节点是否需要进行分裂（节点元素数等于阶数减一，需要进行分裂，其他情况不需要）
     *
     * @return 是否需要分裂
     */
    boolean needDivide() {
        return elementNum > nodeSize - 1;
    }

    /**
     * 获取当前节点中的中间元素, 如果为偶数，就直接取下限
     *
     * @return 返回中间元素
     */
    Element<K, V> getMiddleElement() {
        int index = Math.floorDiv(elementNum, 2);
        return getElement(index > 1 ? index - 1 : 0);
    }


    /**
     * 在当前节点插入指定元素
     *
     * @param element 要插入的元素
     */
    void insertElement(Element<K, V> element) {
        for (Element<K, V> e : elements) {
        }
    }

}

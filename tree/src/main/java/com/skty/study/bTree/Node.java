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
     * 在当前节点插入指定元素,选择适当的顺序进行排序操作
     *
     * @param element 要插入的元素
     */
    void insertElement(Element<K, V> element) {
        for (int i = 0; i < elementNum; i++) {
            K key = element.getKey();
            int compare = key.compareTo(elements[i].getKey());
            if (compare > 0) {//值大于当前元素
                if (i == elementNum - 1) {//已经比当前节点的最后一个元素都大，直接插入到最后
                    elements[i + 1] = element;
                    element.setCurrentNode(this);
                    element.setIndex(i + 1);
                    this.elementNum++;//元素数量加一
                    return;
                } //else{}//继续对下一个元素进行判断(继续循环)
            } else if (compare < 0) {//值小于当前元素,则表示要将元素插入到当前元素的左侧，要将之前的元素以及后面的元素全部后移
                Element<K, V>[] moveElements = Arrays.copyOfRange(this.elements, i, elementNum);
                System.arraycopy(moveElements, 0, elements, i + 1, moveElements.length);
                elements[i] = element;
                element.setCurrentNode(this);
                element.setIndex(i);
                this.elementNum++;//元素数量加一
                return;
            } else {//元素相等，直接替换当前元素(当前节点元素数量保持不变)
                elements[i] = element;
                element.setCurrentNode(this);
                element.setIndex(i);
                return;
            }
        }
    }
}

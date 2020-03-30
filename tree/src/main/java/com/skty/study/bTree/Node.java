package com.skty.study.bTree;

class Node<K extends Comparable,V> {

    /**
     * 节点大小，当前节点所能容纳所有元素的大小
     */
    private int nodeSize;
    /**
     * 当前节点上的所有元素
     */
    private Element<K,V>[] elements;

    /**
     * 父节点
     */
    private Node<K,V> parentNode;

    /**
     * 子节点
     */
    private Node<K,V> childNode;

    /**
     * 当前节点元素数
     */
    private int elementNum;

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

    public Element<K, V> getElement(int index){
        if(index<elementNum-1){
            return elements[index];
        }else {
            throw new  IndexOutOfBoundsException("当前所取得节点超出节点元素大小");
        }
    }

    /**
     * 获取节点的逻辑大小
     * @return 返回当前实际大小-1
     */
    public int getLogicSize(){
        return  nodeSize-1;
    }
}

package com.skty.study.b_tree;

/**
 * 元素对象，用于表示B树种的节点元素(该元素的值必须实现了Comparable接口，用于比较元素的大小进行排序)
 * @author  skty
 */
public  class  Element<T extends Comparable> {

    /**
     * 当前元素所在的节点
     */
    private Node currentNode;

    /**
     * 当前元素的元素值
     */
    private T value;

    /**
     * 左子树（小于当前元素值的子节点）
     */
    private Node<Element> leftNode;

    /**
     *右子树（大于当前元素值得子节点）
     */
    private Node<Element> rightNode;

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Node<Element> getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node<Element> leftNode) {
        this.leftNode = leftNode;
    }

    public Node<Element> getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node<Element> rightNode) {
        this.rightNode = rightNode;
    }
}

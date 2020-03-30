package com.skty.study.bTree;

/**
 * 元素对象，用于表示B树种的节点元素(该元素的值必须实现了Comparable接口，用于比较元素的大小进行排序)
 * @author  skty
 */
class Element<K extends Comparable<K>, V> {

    /**
     * 当前元素所在的节点
     */
    private Node<K, V> currentNode;

    /**
     * 当前元素所在节点的索引位置
     */
    private int index;

    /**
     * 当前元素的元素排序值
     */
    private K key;

    /**
     * 元素实际存放的数据
     */
    private V value;

    /**
     * 左子树（小于当前元素值的子节点）
     */
    private Node<K,V> leftNode;

    /**
     *右子树（大于当前元素值得子节点）
     */
    private Node<K,V> rightNode;

    public Element() {
    }

    public Element(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Node<K, V> getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node<K, V> currentNode) {
        this.currentNode = currentNode;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Node<K, V> getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node<K, V> leftNode) {
        this.leftNode = leftNode;
    }

    public Node<K, V> getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node<K, V> rightNode) {
        this.rightNode = rightNode;
    }

    /**
     * 获取当前元素的后置元素
     * @return 当前元素的后置元素,没有时返回null
     */
    public Element<K, V> getNextElement() {
        if(index<currentNode.getLogicSize()){
            return currentNode.getElement(index+1);
        }
        return null;
    }

    /**
     * 获取当前元素的前置元素
     * @return 当前元素的前置元素，没有时返回null
     */
    public Element<K, V> getPreElement() {
        if(index!=0){
           return currentNode.getElement(index-1);
        }
        return null;
    }

    /**
     * 判断当前元素在当前节点上是否有后置元素
     * @return 是否有
     */
    private boolean hasNext(){
        return index<currentNode.getLogicSize();
    }

}

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

    Element() {
    }

    Element(K key, V value) {
        this.key = key;
        this.value = value;
    }

    Node<K, V> getCurrentNode() {
        return currentNode;
    }

    void setCurrentNode(Node<K, V> currentNode) {
        this.currentNode = currentNode;
    }

    int getIndex() {
        return index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    K getKey() {
        return key;
    }

    void setKey(K key) {
        this.key = key;
    }

    V getValue() {
        return value;
    }

    void setValue(V value) {
        this.value = value;
    }

    Node<K, V> getLeftNode() {
        return leftNode;
    }

    /**
     * 是否存在左子树
     * @return true/false
     */
    boolean hasLeftNode(){
        return leftNode!=null;
    }

    void setLeftNode(Node<K, V> leftNode) {
        this.leftNode = leftNode;
        leftNode.setSuccessorElement(this);
    }

    Node<K, V> getRightNode() {
        return rightNode;
    }

    /**
     * 是否存在右子树
     * @return true/false
     */
    boolean hasRightLeft(){
            return rightNode!=null;
    }

    void setRightNode(Node<K, V> rightNode) {
        this.rightNode = rightNode;
        rightNode.setPredecessorElement(this);
    }

    /**
     * 获取当前元素的后置元素
     * @return 当前元素的后置元素,没有时返回null
     */
    Element<K, V> getNextElement() {
        if (index < currentNode.getElementNum() - 1) {
            return currentNode.getElement(index+1);
        }
        return null;
    }

    /**
     * 获取当前元素的前置元素
     * @return 当前元素的前置元素，没有时返回null
     */
    Element<K, V> getPreElement() {
        if(index!=0){
           return currentNode.getElement(index-1);
        }
        return null;
    }


    /**
     * 判断当前元素在当前节点上是否有后置元素
     * @return 是否有
     */
     boolean hasNext() {
        return index<currentNode.getLogicSize();
    }

    /**
     * 是否是当前节点中的最后一个元素
     * @return true/false
     */
    boolean isNodeLastElement(){
         return index==currentNode.getElementNum()-1;
    }

    /**
     * 是否是当前节点中的第一个元素
     * @return true/false
     */
    boolean isNodeFirstElement(){
        return index==0;
    }

    /**
     * 当前元素的key是否大于指定的key
     * @param key 与当前元素比较的key
     * @return true/false
     */
    boolean keyGreaterThan(K key){
        return this.key.compareTo(key)>0;
    }

    /**
     * 使用当前的key与指定的key值进行比较
     * @param key 要比较的key
     * @return 正数表示大于，0表示相等，负数表示当前元素小于
     */
    int keyCompareWith(K key){
        return this.key.compareTo(key);
    }

    /**
     * 元素比较（比较key）
     * @param other 与当前元素比较的元素
     * @return 正数表示大于，0表示相等，负数表示当前元素小于
     */
    int compareWith(Element<K,V> other){
        return keyCompareWith(other.getKey());
    }

}

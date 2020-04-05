package com.skty.study.bTree;

import java.util.Arrays;
import java.util.stream.Stream;

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
     * 当前节点类型
     */
    private NodeType nodeType;

    /**
     * 节点类型
     */
    enum NodeType {
        ROOTNODE, LEAFNODE, MIDDLENODE;
    }

    @SuppressWarnings("unchecked")
    Node(int nodeSize, NodeType nodeType, Element<K, V>[] elements) {
        this.elements = new Element[nodeSize];//声名空间
        this.nodeSize = nodeSize;
        this.nodeType = nodeType;
        if (elements != null) {
            this.elementNum = elements.length;
            //将传入元素与当前节点关联
            for (int i = 0; i < elementNum; i++) {
                this.elements[i] = elements[i];
                elements[i].setCurrentNode(this);
                elements[i].setIndex(i);
            }
        }
    }

    int getNodeSize() {
        return nodeSize;
    }

    /**
     * 获取所有有值的元素
     *
     * @return elementNum数量的元素
     */
    Element<K, V>[] getElements() {
        return Arrays.copyOf(elements, elementNum);
    }

    Node<K, V> getParentNode() {
        return parentNode;
    }

    void setParentNode(Node<K, V> parentNode) {
        this.parentNode = parentNode;
    }

    Node<K, V> getChildNode() {
        return childNode;
    }

    void setChildNode(Node<K, V> childNode) {
        this.childNode = childNode;
    }

    int getElementNum() {
        return elementNum;
    }

    void setElementNum(int elementNum) {
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

    NodeType getNodeType() {
        return nodeType;
    }

    /**
     * 是否为叶子节点
     *
     * @return true/false
     */
    boolean isLeafNode() {
        return nodeType == NodeType.LEAFNODE;
    }

    boolean isRootNode() {
        return nodeType == NodeType.ROOTNODE;
    }

    boolean isMiddleNode() {
        return nodeType == NodeType.MIDDLENODE;
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
     * 在指定的位置插入元素
     *
     * @param element 要插入的元素
     * @param index   指定的位置
     */
    void insertElement(Element<K, V> element, int index) {
        if (index < nodeSize) {
            if (index >= elementNum) {//插到当前最后一个元素后面
                elements[index] = element;
            } else {
                Element<K, V>[] moveElements = Arrays.copyOfRange(elements, index, elementNum);
                Stream.of(moveElements).forEach(e -> e.setIndex(e.getIndex() + 1));//将后移的元素的索引位置也进行+1
                System.arraycopy(moveElements, 0, elements, index + 1, moveElements.length);
                elements[index] = element;
            }
            element.setIndex(index);
            element.setCurrentNode(this);
            elementNum++;
        } else {
            throw new IllegalArgumentException("向节点插入元素时，不合法的插入的位置");
        }
    }


    /**
     * 在当前节点插入指定元素,选择适当的顺序进行排序操作
     *
     * @param element 要插入的元素
     */
    void insertElement(Element<K, V> element) {
        if (elementNum == 0) {
            insertElement(element, 0);
        } else {
            for (int i = 0; i < elementNum; i++) {
                K key = element.getKey();
                int compare = element.compareWith(elements[i]);
                if (compare > 0) {//值大于当前元素
                    if (i == elementNum - 1) {//已经比当前节点的最后一个元素都大，直接插入到最后
                        insertElement(element, elementNum);
                        return;
                    } else {//中间元素，需要判断要插入的元素是不是比当前元素大，比当前元素的后面元素小，如果是则插入到当前元素的后面，否则继续对下一个元素进行判断
                        if (elements[i+1].keyGreaterThan(key)) {
                            insertElement(element, i + 1);
                            return;
                        }
                    }
                } else if (compare < 0) {//值小于当前元素,则表示要将元素插入到当前元素的左侧，要将当前元素以及后面的元素全部后移
                    insertElement(element, i - 1);
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

    /**
     * 判断当前节点是否存在元素
     * @return true/false
     */
    boolean hasElement(){
        return elementNum>0;
    }
}

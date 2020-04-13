package com.skty.study.bTree;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
     * 当前节点的前驱左侧元素（第一个小于当前节点中所有元素的元素）
     */
    private Element<K, V> predecessorElement;

    /**
     * 当前节点的后继右侧元素（第一个大于当前节点中所有元素的元素）
     */
    private Element<K, V> successorElement;

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
        if (index < elementNum) {
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
    Element<K, V>[] getLeftElementFromIndex(int index) {
        return Arrays.copyOfRange(elements, 0, index);
    }

    /**
     * 获取指定节点索引的右边节点
     *
     * @param index 目标索引
     * @return 所有右边子节点构成的数组
     */
    Element<K, V>[] getRightElementFromIndex(int index) {
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
     * 判断节点中的元素数量是否符合下限（ 阶/2 取上限-1）
     *
     * @return true-低于下限/false
     */
    boolean lowerThanEleLowestLimit() {
        //阶/2 取上限-1
        int limit = (int) (Math.ceil((double) nodeSize / 2) - 1);
        return elementNum < limit;
    }

    /**
     * 判断节点中的元素数量是否丰满（元素数量大于 阶/2 取上限-1）大于元素最小限制
     *
     * @return true-低于下限/false
     */
    boolean moreThanLowestLimit() {
        //阶/2 取上限-1
        int limit = (int) (Math.ceil((double) nodeSize / 2) - 1);
        return elementNum > limit;
    }

    /**
     * 获取当前节点中的中间元素, 如果为偶数，就直接取上限
     *
     * @return 返回中间元素
     */
    Element<K, V> getMiddleElement() {
        //上面这种是取前面的，要先除以2取上限，之后再减一，比较麻烦，用下面的取后面的将会更方便些
       /* int limit = (int) (Math.ceil((double) nodeSize / 2) - 1);
        return getElement(limit);*/
        return getElement(elementNum / 2);
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

            //复制左右子树
            if (index == 0) {//第一个元素
                if (elements[index + 1] != null) {
                    element.setRightNode(elements[index + 1].getLeftNode());
                }
            } else if (index == elementNum) {//最后一个元素
                if (elements[index - 1] != null) {
                    element.setLeftNode(elements[index - 1].getRightNode());
                }
            } else {//中间元素
                if (elements[index + 1] != null) {
                    element.setRightNode(elements[index + 1].getLeftNode());
                }
                if (elements[index - 1] != null) {
                    element.setLeftNode(elements[index - 1].getRightNode());
                }
            }

            elementNum++;
        } else {
            throw new IllegalArgumentException("向节点插入元素时，不合法的插入的位置");
        }
    }

    /**
     * 将多个元素插入到指定的位置，如果指定位置上存在元素，则会将原有元素和后面元素后移
     *
     * @param elements       要插入的元素数组
     * @param index          指定要插入的位置
     * @param useCurrentNode 对连接位置的元素是否使用当前节点的子树
     */
    void insertElement(Element<K, V>[] elements, int index, boolean useCurrentNode) {
        int length = elements.length;
        if (length + elementNum > nodeSize) {
            throw new IllegalArgumentException("插入多个元素到指定节点后，节点数据超出最大值");
        }
        if (index > elementNum) {
            throw new IllegalArgumentException("不允许间隔空值进行插入");
        }

        if (index == elementNum) {//插入到最后面
            appendElements(useCurrentNode, elements);
        } else {
            int i = index;
            for (Element<K, V> e : elements) {
                e.setCurrentNode(this);
                e.setIndex(i);
                //左右子树的父节点也进行修改
                Optional.ofNullable(e.getLeftNode()).ifPresent(left -> left.setParentNode(this));
                Optional.ofNullable(e.getRightNode()).ifPresent(right -> right.setParentNode(this));
                i++;
            }
            Element<K, V>[] moveElements = Arrays.copyOfRange(this.elements, index, elementNum);
            Stream.of(moveElements).forEach(e -> e.setIndex(e.getIndex() + length));//将后移的元素的索引位置也进行移动
            System.arraycopy(elements, 0, this.elements, index, length);//先将插入的元素进行先插入
            System.arraycopy(moveElements, 0, this.elements, index + length, moveElements.length);//将后面需要移动的元素也进行转移

            Element<K, V> endElement = elements[length - 1];//最后一个插入的元素
            Element<K, V> firstElement = elements[0];//第一个插入的元素
            if (index == 0) {//插入到最前面
                if (useCurrentNode) {
                    endElement.setRightNode(endElement.getNextElement().getLeftNode());
                } else {
                    endElement.getNextElement().setLeftNode(endElement.getRightNode());
                }
            } else {//插入到中间位置
                if (useCurrentNode) {
                    firstElement.setLeftNode(firstElement.getPreElement().getRightNode());
                    endElement.setRightNode(endElement.getNextElement().getLeftNode());
                } else {
                    firstElement.getPreElement().setRightNode(firstElement.getLeftNode());
                    endElement.getNextElement().setLeftNode(endElement.getRightNode());
                }
            }
        }
    }


    /**
     * 使用指定的元素替换指定索引位置的元素
     *
     * @param element 操作的元素
     * @param index   要替换元素的索引
     */
    void replaceElement(Element<K, V> element, int index) {
        Element<K, V> oldElement = elements[index];
        element.setLeftNode(oldElement.getLeftNode());
        element.setRightNode(oldElement.getRightNode());
        element.setIndex(index);
        element.setCurrentNode(this);
        elements[index] = element;
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
                        if (elements[i + 1].keyGreaterThan(key)) {
                            insertElement(element, i + 1);
                            return;
                        }
                    }
                } else if (compare < 0) {//值小于当前元素,则表示要将元素插入到当前元素的左侧，要将当前元素以及后面的元素全部后移(可能当前元素为第一个元素，则插入到索引为0的地方)
                    insertElement(element, Math.max(i - 1, 0));
                    return;
                } else {//元素相等，直接替换当前元素(当前节点元素数量保持不变)
                    this.replaceElement(element, i);
                    return;
                }
            }
        }
    }

    /**
     * 判断当前节点是否存在元素
     *
     * @return true/false
     */
    boolean hasElement() {
        return elementNum > 0;
    }

    /**
     * 删除当前节点中指定索引位置的元素
     *
     * @param deleteIndex 要删除的位置
     */
    void deleteElement(int deleteIndex) {
        if (deleteIndex >= 0 && deleteIndex < elementNum) {
            //删除最后一个元素,直接将最后一个元素置空；删除中间元素需要进行元素移动
            if (deleteIndex == elementNum - 1) {
                elements[deleteIndex] = null;
            } else {
                Element<K, V>[] elements = Arrays.copyOfRange(this.elements, deleteIndex + 1, elementNum);
                Stream.of(elements).forEach(element -> element.setIndex(element.getIndex() - 1));//将移动的元素索引位置进行减一操作
                System.arraycopy(elements, 0, this.elements, deleteIndex, elements.length);
                this.elements[elementNum - 1] = null;//将最后一个元素进行删除
            }
            elementNum--;
        } else {
            throw new IllegalArgumentException("删除节点的索引位置不合法,位置:" + deleteIndex + " ;当前元素数:" + elementNum);
        }
    }

    public Element<K, V> getPredecessorElement() {
        return predecessorElement;
    }

    public void setPredecessorElement(Element<K, V> predecessorElement) {
        this.predecessorElement = predecessorElement;
    }

    public Element<K, V> getSuccessorElement() {
        return successorElement;
    }

    public void setSuccessorElement(Element<K, V> successorElement) {
        this.successorElement = successorElement;
    }

    /**
     * 获取节点最小的一个元素，最左侧的元素
     *
     * @return 当前节点最小的元素
     */
    Element<K, V> getMiniElement() {
        return elements[0];
    }

    /**
     * 返回当前节点最大的一个元素（最右边的一个元素）
     *
     * @return 当前节点最大的元素
     */
    Element<K, V> getMaxElement() {
        return elements[elementNum - 1];
    }

    /**
     * 将元素追加到节点后面
     *
     * @param useCurrentNode 是否使用当前节点最后一个元素的右节点作为新加入元素列表的第一个元素的左子树,false：使用新加的元素
     * @param elements       要追加的元素
     */
    @SafeVarargs
    final void appendElements(boolean useCurrentNode, Element<K, V>... elements) {
        if (elements.length + elementNum > nodeSize) {
            throw new IllegalArgumentException("超出节点元素数量");
        }
        if (elements.length > 0) {
            int i = 0;
            for (Element<K, V> e : elements) {
                e.setCurrentNode(this);
                e.setIndex(elementNum + i);
            /*    //左右子树的父节点也进行修改
                Optional.ofNullable(e.getLeftNode()).filter(n -> !this.equals(n)).ifPresent(left -> left.setParentNode(this));
                Optional.ofNullable(e.getRightNode()).filter(n -> !this.equals(n)).ifPresent(right -> right.setParentNode(this));*/
                i++;
            }
            if (elementNum > 0) {
                if (useCurrentNode) {
                    elements[0].setLeftNode(this.elements[elementNum - 1].getRightNode());
                } else {
                    this.elements[elementNum - 1].setRightNode(elements[0].getLeftNode());
                }
            }
            System.arraycopy(elements, 0, this.elements, elementNum, elements.length);
            this.elementNum++;
        }
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String toString() {
        String collect = Stream.of(Arrays.copyOfRange(elements, 0, elementNum)).map(Element::getKey).map(Objects::toString).collect(Collectors.joining(";"));
        return collect + "[" + (predecessorElement == null ? "null" : predecessorElement.getKey()) +
                "," + (successorElement == null ? "null" : successorElement.getKey()) + "]";
    }

}

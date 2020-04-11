package com.skty.study.bTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * B树对象
 */
public class BTree<K extends Comparable<K>, V> {
    /**
     * b数的阶
     */
    private int size;

    /**
     * 树高
     */
    private int height;

    /**
     * 当前数的根节点
     */
    private Node<K, V> rootNode;

    /**
     * 创建一颗B树
     *
     * @param size 树的阶。大于2
     */
    public BTree(int size) {
        if (size > 2) {
            this.size = size;
            rootNode = new Node<>(size, Node.NodeType.ROOTNODE, null);
            height = 1;//树高为1
        } else {
            throw new IllegalArgumentException("B树的阶必须大于2");
        }
    }

    /**
     * 新增数据，这个方法暴露给外部使用
     *
     * @param key   指定key
     * @param value 指定的value
     * @return true:不存在该元素，已经新增进去  false:1.该元素已经存在树中，用新元素替换旧元素
     */
    public boolean insert(K key, V value) {
        return addElement(new Element<>(key, value));
    }

    /**
     * 删除元素
     *
     * @param key 要删除的key
     * @return true:找到元素并对其进行了删除 ，false:其他情况
     */
    public boolean delete(K key) {
        //查找元素，找到元素，进行删除操作
        Element<K, V> deleteElement = findElement(key);
        if (deleteElement != null) {
            Node<K, V> currentNode = deleteElement.getCurrentNode();
            if (currentNode.isLeafNode()) {//叶子节点删除元素
                /*叶子节点删除元素
                 *1.将当前元素从节点中移除
                 *2.判断是否需要下溢
                 * 2.1：需要下溢：判断两个兄弟节点是否丰满，如果丰满，将该兄弟节点中的符合要求的元素进行上升到父节点，同时将父元素中的元素进行下移到被删除元素的叶子节点
                 *        判断父元素是否需要下溢，继续对父元素执行下溢操作
                 * 2.2:不需要下溢，直接删除元素
                 */
                currentNode.deleteElement(deleteElement.getIndex());
                afterElementDelete(currentNode);
            } else {//内部节点

            }
        }
        return false;
    }


    /**
     * 删除叶子节点中的元素
     *
     * @param element 要进行删除的元素
     */
    private void deleteLeafElement(Element<K, V> element) {
        Node<K, V> currentNode = element.getCurrentNode();
        //从节点中删除元素
        currentNode.deleteElement(element.getIndex());
        afterElementDelete(currentNode);
    }


    /**
     * 元素删除后的操作，判断当前节点删除完元素后是否需要进行下溢操作，如果需要则进行下溢
     */
    private void afterElementDelete(Node<K, V> currentNode) {
        if (currentNode.lowerThanEleLowestLimit()) {//当前删除元素后的节点元素过少，需要进行下溢
            //获取可以借元素的兄弟节点
            SiblingNodeChooseMode siblingNodeChooseMode = chooseUpSiblingNode(currentNode);
            switch (siblingNodeChooseMode.type) {
                case SiblingNodeChooseMode.LEFT_SIBLING://从左侧借，右旋转
                    rotateRight(siblingNodeChooseMode.siblingNode, currentNode.getPredecessorElement(), currentNode);
                    break;
                case SiblingNodeChooseMode.RIGHT_SIBLING://从右侧借,左旋转
                    rotateLeft(siblingNodeChooseMode.siblingNode, currentNode.getSuccessorElement(), currentNode);
                    break;
                case SiblingNodeChooseMode.OVERFLOW_SIBLING_LEFT://无法借兄弟节点，需要进行节点合并，将父节点中的元素进行下溢(与左兄弟节点结合)
                    elementUnderflow(siblingNodeChooseMode.siblingNode, currentNode.getPredecessorElement(), currentNode);
                    break;
                case SiblingNodeChooseMode.OVERFLOW_SIBLING_RIGHT://无法借兄弟节点，需要进行节点合并，将父节点中的元素进行下溢(与右兄弟节点结合)
                    elementUnderflow(currentNode, currentNode.getSuccessorElement(), siblingNodeChooseMode.siblingNode);
                    break;
                case SiblingNodeChooseMode.ILLEGAL_MODE:
                    throw new IllegalArgumentException("当前树存在问题,无法删除元素");
            }
        }
    }

    /**
     * 执行元素下溢操作
     *
     * @param leftChildNode  元素下溢时的左侧左节点
     * @param middleElement  元素下溢时的中间元素
     * @param rightChildNode 元素下溢时的右侧子节点
     */
    private void elementUnderflow(Node<K, V> leftChildNode, Element<K, V> middleElement, Node<K, V> rightChildNode) {
        Element<K, V> preElement = middleElement.getPreElement();
        Element<K, V> nextElement = middleElement.getNextElement();
        //将中间节点从父节点删除
        Node<K, V> middleEleCurrentNode = middleElement.getCurrentNode();
        middleEleCurrentNode.deleteElement(middleElement.getIndex());

        //使用左子树作为新的节点，将其他节点新增到里面
        Element<K, V>[] rightElements = rightChildNode.getElements();
        leftChildNode.appendElements(true, middleElement);
        leftChildNode.appendElements(false, rightElements);

        //设置元素子节点关联
        preElement.setRightNode(leftChildNode);
        nextElement.setLeftNode(leftChildNode);

        //将右节点中的所有子节点的parentNode改为左节点
        changeParentNode(leftChildNode, rightElements);

        //如果当前下溢节点为根节点,则不必要再传播下去
        if (middleEleCurrentNode.isRootNode()) {
            //根节点下溢后，如果根节点没有元素，则将新生成的节点升为根节点；降低树高
            if (!middleEleCurrentNode.hasElement()) {
                leftChildNode.setParentNode(null);
                leftChildNode.setNodeType(Node.NodeType.ROOTNODE);
                this.rootNode = leftChildNode;
                height--;
            }
        } else {
            //由于执行了下溢操作，可能父节点也需要进行下溢（递归操作）
            afterElementDelete(middleEleCurrentNode);
        }
    }


    /**
     * 执行左旋转
     *
     * @param fromNode        初始节点（左侧的节点）
     * @param opElement       操作的中间元素
     * @param destinationNode 旋转目的节点(右侧的节点)
     */
    private void rotateRight(Node<K, V> fromNode, Element<K, V> opElement, Node<K, V> destinationNode) {
        Element<K, V> elementMini = fromNode.getMaxElement();//旋转元素中的最小的一个
        Node<K, V> miniRightNode = elementMini.getRightNode();//最小元素的右子节点
        Node<K, V> middleEleNode = opElement.getCurrentNode();//中间元素所在节点
        middleEleNode.replaceElement(elementMini, opElement.getIndex());//将最小元素与中间元素进行替换
        destinationNode.insertElement(opElement, 0);//将中间元素插入到目的节点的最左侧（在该节点中最小）
        opElement.setLeftNode(miniRightNode);//将最小元素的右节点作为中间元素下移后得到元素的左子树
    }

    /**
     * 执行右旋转
     *
     * @param fromNode        初始节点(右边的节点)
     * @param opElement       执行操作的中间节点
     * @param destinationNode 旋转后的目标节点（左侧节点）
     */
    private void rotateLeft(Node<K, V> fromNode, Element<K, V> opElement, Node<K, V> destinationNode) {
        Element<K, V> maxElement = fromNode.getMiniElement();
        Node<K, V> maxEleLeftNode = maxElement.getLeftNode();
        Node<K, V> middleEleNode = opElement.getCurrentNode();
        middleEleNode.replaceElement(maxElement, opElement.getIndex());
        destinationNode.insertElement(opElement, destinationNode.getElementNum());
        opElement.setRightNode(maxEleLeftNode);
    }

    /**
     * 将指定的元素从所在节点移动到指定节点的指定位置
     *
     * @param element    要移动的元素
     * @param targetNode 移动到的节点
     * @param index      指定的位置
     */
    private void moveElementToNode(Element<K, V> element, Node<K, V> targetNode, int index) {
        element.getCurrentNode().deleteElement(element.getIndex());//将目标元素先从指定节点删除
        targetNode.replaceElement(element, index);//将当前元素替换到指定节点的指定元素
    }


    /**
     * 从当前节点的左右兄弟节点中选择能够上升元素到父节的节点
     *
     * @return 从左兄弟节点开始查找, 查完左，之后查右，如果查到一个符合的就进行返回，如果都没有，就返回null
     */
    private SiblingNodeChooseMode chooseUpSiblingNode(Node<K, V> currentNode) {
        Element<K, V> predecessorElement = currentNode.getPredecessorElement();
        Element<K, V> successorElement = currentNode.getSuccessorElement();

        Node<K, V> leftSibling = null;
        Node<K, V> rightSibling = null;

        if (predecessorElement != null) {
            leftSibling = predecessorElement.getLeftNode();
            if (leftSibling != null && leftSibling.moreThanLowestLimit()) {
                return new SiblingNodeChooseMode(leftSibling, SiblingNodeChooseMode.LEFT_SIBLING);
            }
        }

        if (successorElement != null) {
            rightSibling = successorElement.getLeftNode();
            if (rightSibling != null && rightSibling.moreThanLowestLimit()) {
                return new SiblingNodeChooseMode(rightSibling, SiblingNodeChooseMode.RIGHT_SIBLING);
            }
        }

        if (leftSibling == null && rightSibling != null) {
            return new SiblingNodeChooseMode(rightSibling, SiblingNodeChooseMode.OVERFLOW_SIBLING_RIGHT);
        }
        if (rightSibling == null && leftSibling != null) {
            return new SiblingNodeChooseMode(leftSibling, SiblingNodeChooseMode.OVERFLOW_SIBLING_LEFT);
        }

        return new SiblingNodeChooseMode(null, SiblingNodeChooseMode.ILLEGAL_MODE);
    }


    /**
     * 将当前B树的数据结构生成可打印的字符串
     *
     * @return
     */
    public String printBTree() {
        List<Node<K, V>> list = new ArrayList<>(1);
        list.add(rootNode);
        return print(list);
    }

    private String print(List<Node<K, V>> nodeList) {
        String str = nodeList.stream().map(this::nodeStr).collect(Collectors.joining("\t\t"));
        List<Node<K, V>> nextNodeList = new ArrayList<>();
        for (Node<K, V> node : nodeList) {
            for (Element<K, V> e : node.getElements()) {
                if (e.hasLeftNode()) {
                    nextNodeList.add(e.getLeftNode());
                }
                if (e.isNodeLastElement() && e.hasRightLeft()) {//最后一个元素需要将左右子树同时打印
                    nextNodeList.add(e.getRightNode());
                }
            }
        }
        if (!nextNodeList.isEmpty()) {
            str = str + "\n" + print(nextNodeList);
        }
        return str;
    }

    /**
     * 生成节点打印字符
     *
     * @param node 指定的节点
     * @return 返沪打印字符
     */
    private String nodeStr(Node<K, V> node) {
        Element<K, V>[] elements = node.getElements();
        return Stream.of(elements).map(Element::getKey).map(Objects::toString).collect(Collectors.joining(";"));
    }


    /**
     * 新增元素
     *
     * @param e 要新增的元素
     * @return true:不存在该元素，已经新增进去  false:1.该元素已经存在树中，用新元素替换旧元素
     */
    private boolean addElement(Element<K, V> e) {
        boolean insertResult = false;
        //先获取可以允许插入模式，在哪个节点插入，插入在元素的哪边
        InsertMode insertMode = getInsertMode(rootNode, e.getKey());
        Element<K, V> targetElement = insertMode.targetElement;
        switch (insertMode.mode) {
            case InsertMode.LEFT_INSERT_MODE://左侧插入
                Node<K, V> currentNode = targetElement.getCurrentNode();
                currentNode.insertElement(e, targetElement.getIndex());
                afterElementInsert(currentNode);//节点分裂
                insertResult = true;
                break;
            case InsertMode.RIGHT_INSERT_MODE://右侧插入
                Node<K, V> currentNode1 = targetElement.getCurrentNode();
                currentNode1.insertElement(e, targetElement.getIndex() + 1);
                afterElementInsert(currentNode1);//节点分裂
                insertResult = true;
                break;
            case InsertMode.REPLACE_MODE://替换模式,只需要替换节点元素的值
                targetElement.setValue(e.getValue());
                insertResult = false;
                break;
            case InsertMode.FIRST_INSERT://首次插入
                rootNode.insertElement(e, 0);
                insertResult = true;
                break;
            case InsertMode.ILLEGAL_MODE://树不合法
                throw new IllegalArgumentException("树不合法，无法完成新增元素");
        }
        return insertResult;
    }


    /**
     * 查找B树，获取指定key对应的value
     *
     * @param key 指定的key
     * @return 对应的value，没有返回null
     */
    public V find(K key) {
        Element<K, V> element = findElement(key);
        if (element != null) {
            return element.getValue();
        }
        return null;
    }

    /**
     * 通过指定key查找元素
     *
     * @param key 指定的key
     * @return 返回对应的元素，不存在则返回null
     */
    private Element<K, V> findElement(K key) {
        return findElement(rootNode, key);
    }

    /**
     * 递归调用节点查询
     *
     * @param node 当前节点
     * @param key  要进行查找的key
     * @return 查询到的元素，否则返回null
     */
    private Element<K, V> findElement(Node<K, V> node, K key) {
        Element<K, V>[] elements = node.getElements();
        boolean leafNode = node.isLeafNode();
        for (int i = 0; i < elements.length; i++) {
            Element<K, V> e = elements[i];
            int compareResult = e.keyCompareWith(key);
            if (compareResult > 0) {//查找元素小于当前遍历元素，表示查找的元素可能在当前元素的左子树上
                return leafNode ? null : findElement(e.getLeftNode(), key);
            } else if (compareResult < 0) {//当前元素小于查找的key，表示查找的元素可能在右子树或者下一个元素中
                if (i == elements.length - 1) {//如果当前元素是节点最后一个元素,则只可能在当前元素的右子树中
                    return leafNode ? null : findElement(e.getRightNode(), key);
                } else {//不是最后一个元素，要判断当前元素后面的元素是否也比查找的key要小，如果要小，就必须继续往后找，如果后面元素比查找的元素要大，则在当前元素的右节点上查找
                    if (elements[i + 1].keyGreaterThan(key)) {
                        return leafNode ? null : findElement(e.getRightNode(), key);
                    }
                }
            } else {//找到了与查找的元素相等的数据
                return e;
            }
        }
        return null;//查找到叶子节点了，但是仍然没找到，返回null
    }

    /**
     * 递归查找可以允许当前元素插入的节点
     *
     * @param insertKey 要插入的元素对应的key值
     * @return 返回允许插入当前元素的节点, 如果在搜索的过程中发现元素已经存在，则返回null
     */
    private InsertMode getInsertMode(Node<K, V> startNode, K insertKey) {
        if (startNode.hasElement()) {
            Element<K, V>[] elements = startNode.getElements();
            int length = elements.length;
            //是否可以在当前节点上进行插入操作（1.当前节点是叶子节点   2.当前树高为1，且当前节点为根节点）
            boolean nodeCanInsert = startNode.isLeafNode() || (height == 1);
            for (int i = 0; i < length; i++) {
                Element<K, V> e = elements[i];
                int compareResult = e.keyCompareWith(insertKey);
                if (compareResult > 0) {//查找元素小于当前遍历元素，表示查找的元素可能在当前元素的左子树上
                    return nodeCanInsert ? leftInsertMode(e) : getInsertMode(e.getLeftNode(), insertKey);
                } else if (compareResult < 0) {//当前元素小于查找的key，表示查找的元素可能在右子树或者下一个元素中
                    if (i == elements.length - 1) {//如果当前元素是当前节点的最后一个元素，当前节点为叶子节点，则直接插入到当前节点后，不是叶子节点，则去右子树上找
                        return nodeCanInsert ? rightInsertMode(e) : getInsertMode(e.getRightNode(), insertKey);
                    } else {//不是最后一个元素，判断当前元素后面的元素是否也比查找的key要小，如果要小，就必须继续往后找，如果后面元素比查找的元素要大，则在当前元素的右节点上查找
                        if (elements[i + 1].keyGreaterThan(insertKey)) {//后置元素比插入元素大，则表示插入在当前元素的后面或者右子树
                            return nodeCanInsert ? rightInsertMode(e) : getInsertMode(e.getRightNode(), insertKey);
                        }
                    }
                } else {//找到了与查找的元素相等的数据,不需要进行插入,进行元素替换
                    return replaceMode(e);
                }
            }
            return illegalMode();//树有问题，不合法操作
        } else {//没有元素，初始状态
            return firstInsertMode();
        }
    }

    /**
     * 元素插入完成后的操作，判断元素是否达到分裂标准，达到执行分裂
     * 1.获取中间节点
     * 2.将左边元素构造成一个节点，将右边元素构造成一个节点
     * 3.将中间节点上升到父节点（如果当前节点已经是根节点了，就新建一个节点作为当前节点的父节点）
     * 4.将新生成的两个节点分别作为中间节点的左右子树
     * 5.将生成的两颗子树注册为父节点的子节点
     * 6，将生成的左右子树中的每个元素的左右子树对应的节点修改为新的父节点（到这一步就相当于完成了对旧分裂节点的删除）
     */
    private void afterElementInsert(Node<K, V> node) {
        if (node.needDivide()) {
            //中间元素
            Element<K, V> middleElement = node.getMiddleElement();
            //中间元素所在的索引数
            int middleElementIndex = middleElement.getIndex();
            final int nodeSize = node.getNodeSize();
            //左边节点构成的数组
            Element<K, V>[] leftElements = node.getLeftElementFromIndex(middleElementIndex);
            //右边节点构成的节点
            Element<K, V>[] rightElements = node.getRightElementFromIndex(middleElementIndex);

            //如果当前节点是叶子节点(或者当前数的高度为1，只有一层)，则新生成的两个节点也是在叶子节点；不是的话表示当前为中间节点或者根节点，则需要将新生成的节点作为中间节点
            Node.NodeType nodeType = node.isLeafNode() || height == 1 ? Node.NodeType.LEAFNODE : Node.NodeType.MIDDLENODE;

            //新生成的左子节点
            Node<K, V> newLeftChildNode = new Node<>(nodeSize, nodeType, leftElements);
            //新生成的右子节点
            Node<K, V> newRightChildNode = new Node<>(nodeSize, nodeType, rightElements);

            Node<K, V> parentNode;
            if (node.isRootNode()) {//当前节点是根节点，则需要生成一个新节点作为根节点
                parentNode = new Node<>(nodeSize, Node.NodeType.ROOTNODE, null);//将中间节点提升作为根节点
                rootNode = parentNode;//重置根节点
                height++;//一直分裂到了根节点，树高进行增加
            } else {
                parentNode = node.getParentNode();
            }

            //将当前节点上升到父节点
            parentNode.insertElement(middleElement);

            //将新生成的两个叶子节点放到中间元素的左右子节点,同时重新设置前后元素的子节点
            middleElement.setLeftNode(newLeftChildNode);
            Optional.ofNullable(middleElement.getPreElement()).ifPresent(pre -> pre.setRightNode(newLeftChildNode));
            middleElement.setRightNode(newRightChildNode);
            Optional.ofNullable(middleElement.getNextElement()).ifPresent(next -> next.setLeftNode(newRightChildNode));

            //将生成的两颗子树设置为父节点的子节点
            newLeftChildNode.setParentNode(parentNode);
            newRightChildNode.setParentNode(parentNode);

            //将左边元素的所有子节点的父节点修改为新生成的左子树,右边也是一样
            changeParentNode(newLeftChildNode, leftElements);
            changeParentNode(newRightChildNode, rightElements);
            node = null;//置空
            //对新增完元素的父节点进行递归处理，判断其是否需要进行节点分裂
            afterElementInsert(parentNode);
        }
    }


    /**
     * 为元素下的所有子节点(左右子节点)指定新的节点为父节点
     *
     * @param newNode 要修改到的新节点
     */
    @SafeVarargs
    private final void changeParentNode(Node<K, V> newNode, Element<K, V>... elements) {
        for (Element<K, V> e : elements) {
            Optional.ofNullable(e.getLeftNode()).ifPresent(left -> left.setParentNode(newNode));
            Optional.ofNullable(e.getRightNode()).ifPresent(right -> right.setParentNode(newNode));
        }
    }


    /**
     * 左插入模式
     *
     * @param element 操作的元素
     * @return 左插入模式
     */
    private InsertMode leftInsertMode(Element<K, V> element) {
        return new InsertMode(element, InsertMode.LEFT_INSERT_MODE);
    }

    /**
     * 右插入模式
     *
     * @param element 操作的元素
     * @return 右插入模式
     */
    private InsertMode rightInsertMode(Element<K, V> element) {
        return new InsertMode(element, InsertMode.RIGHT_INSERT_MODE);
    }

    /**
     * 替换模式
     *
     * @param element 操作的元素
     * @return 替换模式
     */
    private InsertMode replaceMode(Element<K, V> element) {
        return new InsertMode(element, InsertMode.REPLACE_MODE);
    }

    /**
     * 第一次插入模式
     *
     * @return 第一次插入模式
     */
    private InsertMode firstInsertMode() {
        return new InsertMode(null, InsertMode.FIRST_INSERT);
    }


    /**
     * 替换模式
     *
     * @return 替换模式
     */
    private InsertMode illegalMode() {
        return new InsertMode(null, InsertMode.ILLEGAL_MODE);
    }


    /**
     * 插入元素时的模式，用来指示当前要插入的元素，和插入操作的模式，插入在指定元素的左边/右边，还是不进行插入，更新查找到的元素
     */
    private class InsertMode {
        /**
         * 左侧插入模式，将当前元素插入到指定元素的左侧
         */
        static final int LEFT_INSERT_MODE = -1;

        /**
         * 替换模式，使用当前元素替换查询到的元素
         */
        static final int REPLACE_MODE = 0;

        /**
         * 右侧插入模式，将当前元素插入到指定元素的右侧
         */
        static final int RIGHT_INSERT_MODE = 1;

        /**
         * 第一次插入模式
         */
        static final int FIRST_INSERT = 9;

        /**
         * 不合法插入模式
         */
        static final int ILLEGAL_MODE = -9;

        /**
         * 目标元素
         */
        Element<K, V> targetElement;

        /**
         * 当前插入的模式
         */
        int mode;

        InsertMode(Element<K, V> targetElement, int mode) {
            this.targetElement = targetElement;
            this.mode = mode;
        }
    }


    /**
     * 兄弟节点选择模式
     */
    private class SiblingNodeChooseMode {
        /**
         * 左侧兄弟节点
         */
        static final int LEFT_SIBLING = -1;
        /**
         * 右侧兄弟节点
         */
        static final int RIGHT_SIBLING = 1;

        /**
         * 使用左兄弟节点进行下溢操作
         */
        static final int OVERFLOW_SIBLING_LEFT = -9;

        /**
         * 使用右兄弟节点进行下溢操作
         */
        static final int OVERFLOW_SIBLING_RIGHT = 9;

        /**
         * 异常
         */
        static final int ILLEGAL_MODE = -99;


        Node<K, V> siblingNode;
        int type;

        SiblingNodeChooseMode(Node<K, V> siblingNode, int type) {
            this.siblingNode = siblingNode;
            this.type = type;
        }
    }

}

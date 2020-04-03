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

    public BTree(int size) {
        this.size = size;
        rootNode = new Node<>(size, Node.NodeType.ROOTNODE, null);
        height = 1;//树高为1
    }

    /**
     * 新增数据，这个方法暴露给外部使用
     *
     * @param key   指定key
     * @param value 指定的value
     * @return true:不存在该元素，已经新增进去  false:1.该元素已经存在树中，用新元素替换旧元素
     */
    public boolean insert(K key, V value) {
        return addElement(new Element<K, V>(key, value));
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
                if (e.getLeftNode() != null) {
                    nextNodeList.add(e.getLeftNode());
                }
                if (e.getRightNode() != null) {
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
     * 执行元素插入
     *
     * @param element    要插入的元素
     * @param insertMode 插入模式
     */
    private void insertElement(Element<K, V> element, InsertMode insertMode) {
        Element<K, V> targetElement = insertMode.targetElement;
        targetElement.getCurrentNode().getLogicSize();
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
            int compareResult = e.getKey().compareTo(key);
            if (compareResult > 0) {//查找元素小于当前遍历元素，表示查找的元素可能在当前元素的左子树上
                return leafNode ? null : findElement(e.getLeftNode(), key);
            } else if (compareResult < 0) {//当前元素小于查找的key，表示查找的元素可能在右子树或者下一个元素中
                if (i == elements.length - 1) {//如果当前元素是节点最后一个元素,则只可能在当前元素的右子树中
                    return leafNode ? null : findElement(e.getRightNode(), key);
                } else {//不是最后一个元素，要判断当前元素后面的元素是否也比查找的key要小，如果要小，就必须继续往后找，如果后面元素比查找的元素要大，则在当前元素的右节点上查找
                    if (elements[i + 1].getKey().compareTo(key) > 0) {
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
        if (startNode.getElementNum() == 0) {//没有元素，初始状态
            return firstInsertMode();
        } else {
            Element<K, V>[] elements = startNode.getElements();
            int length = elements.length;
            //是否可以在当前节点上进行插入操作（1.当前节点是叶子节点   2.当前树高为1，且当前节点为根节点）
            boolean nodeCanInsert = startNode.isLeafNode() || (height == 1);
            for (int i = 0; i < length; i++) {
                Element<K, V> e = elements[i];
                int compareResult = e.getKey().compareTo(insertKey);
                if (compareResult > 0) {//查找元素小于当前遍历元素，表示查找的元素可能在当前元素的左子树上
                    return nodeCanInsert ? leftInsertMode(e) : getInsertMode(e.getLeftNode(), insertKey);
                } else if (compareResult < 0) {//当前元素小于查找的key，表示查找的元素可能在右子树或者下一个元素中
                    if (i == elements.length - 1) {//如果当前元素是当前节点的最后一个元素，当前节点为叶子节点，则直接插入到当前节点后，不是叶子节点，则去右子树上找
                        return nodeCanInsert ? rightInsertMode(e) : getInsertMode(e.getRightNode(), insertKey);
                    } else {//不是最后一个元素，判断当前元素后面的元素是否也比查找的key要小，如果要小，就必须继续往后找，如果后面元素比查找的元素要大，则在当前元素的右节点上查找
                        if (elements[i + 1].getKey().compareTo(insertKey) > 0) {//后置元素比插入元素大，则表示插入在当前元素的后面或者右子树
                            return nodeCanInsert ? rightInsertMode(e) : getInsertMode(e.getRightNode(), insertKey);
                        }
                    }
                } else {//找到了与查找的元素相等的数据,不需要进行插入,进行元素替换
                    return replaceMode(e);
                }
            }
            return illegalMode();//树有问题，不合法操作
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
            Element<K, V>[] leftElements = node.getLeftElement(middleElementIndex);
            //右边节点构成的节点
            Element<K, V>[] rightElements = node.getRightElement(middleElementIndex);

            //新生成的左子节点
            Node<K, V> newLeftChildNode = new Node<>(nodeSize, Node.NodeType.LEAFNODE, leftElements);
            //新生成的右子节点
            Node<K, V> newRightChildNode = new Node<>(nodeSize, Node.NodeType.LEAFNODE, rightElements);

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

}

package com.skty.study.bTree;

/**
 * B树对象
 */
public class BTree<K extends Comparable<K>, V> {
    /**
     * b数的阶
     */
    private int size;

    /**
     * 当前数的根节点
     */
    private Node<K, V> rootNode;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 新增数据，这个方法暴露给外部使用
     *
     * @param key   指定key
     * @param value 指定的value
     * @return true:不存在该元素，已经新增进去  false:1.该元素已经存在树中，用新元素替换旧元素
     */
    private boolean addElement(K key, V value) {
        return addElement(new Element<K, V>(key, value));
    }

    /**
     * 新增元素
     *
     * @param e 要新增的元素
     * @return true:不存在该元素，已经新增进去  false:1.该元素已经存在树中，用新元素替换旧元素
     */
    private boolean addElement(Element<K, V> e) {
        boolean insertResult = false;
        //先获取可以允许插入的节点
        InsertMode insertMode = findInsertAbleNode(rootNode, e.getKey());
        Element<K, V> targetElement = insertMode.targetElement;
        switch (insertMode.mode) {
            case InsertMode.LEFT_INSERT_MODE://左侧插入
                break;
            case InsertMode.RIGHT_INSERT_MODE://右侧插入
                break;
            case InsertMode.REPLACE_MODE://替换模式,只需要替换节点元素的值
                targetElement.setValue(e.getValue());
                insertResult = false;
                break;
            case InsertMode.ILLEGAL_MODE://树不合法
                break;
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
    public V findValue(K key) {
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

        for (int i = 0; i < elements.length; i++) {
            Element<K, V> e = elements[i];
            int compareResult = e.getKey().compareTo(key);
            if (compareResult > 0) {//查找元素小于当前遍历元素，表示查找的元素可能在当前元素的左子树上
                Node<K, V> leftNode = e.getLeftNode();
                if (leftNode == null) {//没有左子树，表示不存在
                    return null;
                } else {
                    return findElement(leftNode, key);//使用左子树作为参数，递归查找
                }
            } else if (compareResult < 0) {//当前元素小于查找的key，表示查找的元素可能在右子树或者下一个元素中
                if (i == elements.length - 1) {//如果当前元素是节点最后一个元素,则只可能在当前元素的右子树中
                    Node<K, V> rightNode = e.getRightNode();
                    if (rightNode != null) {//当前节点的右子树存在，继续对右子树进行递归查找
                        return findElement(rightNode, key);
                    } else {
                        return null;//不存在右子树，表示找不到
                    }
                } else {//不是最后一个元素，要判断当前元素后面的元素是否也比查找的key要小，如果要小，就必须继续往后找，如果后面元素比查找的元素要大，则在当前元素的右节点上查找
                    if (elements[i + 1].getKey().compareTo(key) > 0) {
                        Node<K, V> rightNode = e.getRightNode();
                        if (rightNode != null) {//当前节点的右子树存在，继续对右子树进行递归查找
                            return findElement(rightNode, key);
                        } else {
                            return null;//不存在右子树，表示找不到
                        }
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
    private InsertMode findInsertAbleNode(Node<K, V> startNode, K insertKey) {
        Element<K, V>[] elements = startNode.getElements();
        int length = elements.length;
        for (int i = 0; i < length; i++) {
            Element<K, V> e = elements[i];
            int compareResult = e.getKey().compareTo(insertKey);
            if (compareResult > 0) {//查找元素小于当前遍历元素，表示查找的元素可能在当前元素的左子树上
                Node<K, V> leftNode = e.getLeftNode();
                if (leftNode == null) {//没有左子树，表示当前元素需要插入的位置就是当前元素所在节点
                    return new InsertMode(e, InsertMode.LEFT_INSERT_MODE);
                } else {
                    return findInsertAbleNode(leftNode, insertKey);//使用左子树作为参数，递归查找
                }
            } else if (compareResult < 0) {//当前元素小于查找的key，表示查找的元素可能在右子树或者下一个元素中
                Node<K, V> rightNode = e.getRightNode();
                if (rightNode == null) {//右子树为空
                    if (i == length - 1) {//判断是否在该元素后面还有元素，如果没有，则表示当前元素为最后一个，需要插入在当前元素的右边
                        return new InsertMode(e, InsertMode.RIGHT_INSERT_MODE);
                    }//else{}//如果还有当前元素右侧还有下一个元素的话，就对下一个元素进行遍历
                } else {//当前节点的右子树存在，继续对右子树进行递归查找
                    return findInsertAbleNode(rightNode, insertKey);
                }
            } else {//找到了与查找的元素相等的数据,不需要进行插入,进行元素替换
                return new InsertMode(e, InsertMode.REPLACE_MODE);
            }
        }
        return new InsertMode(null, InsertMode.ILLEGAL_MODE);//树有问题，不合法操作
    }

    /**
     * 执行节点分裂
     * 1.获取中间节点
     * 2.将左边元素构造成一个节点，将右边元素构造成一个节点
     * 3.将新生成的两个节点分别作为中间节点的左右子树
     * 4.将中间节点上升到父节点
     * 5.
     */
    private void nodeDivide(Node<K, V> node) {
        if (node.needDivide()) {
            //中间元素
            Element<K, V> middleElement = node.getMiddleElement();
            //中间元素所在的索引数
            int middleElementIndex = middleElement.getIndex();

            //左边节点构成的数组
            Element<K, V>[] leftElement = node.getLeftElement(middleElementIndex);
            //右边节点构成的节点
            Element<K, V>[] rightElement = node.getRightElement(middleElementIndex);

            //新生成的左子节点
            Node<K, V> newLeftChildNode = new Node<>(leftElement, leftElement.length, true);
            //新生成的右子节点
            Node<K, V> newRightChildNode = new Node<>(rightElement, rightElement.length, true);

            //将新节点放到中间元素的左右子节点
            middleElement.setLeftNode(newLeftChildNode);
            middleElement.setRightNode(newRightChildNode);

            //升级中间节点到父节点
            Node<K, V> parentNode = node.getParentNode();
            parentNode.insertElement(middleElement);


        }
    }

    /**
     * 插入元素时的模式，用来指示当前要插入的元素，和插入操作的模式，插入在指定元素的左边/右边，还是不进行插入，更新查找到的元素
     */
    class InsertMode {
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

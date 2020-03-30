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
     * @param key 指定key
     * @param  value 指定的value
     * @return true:不存在该元素，已经新增进去  false:1.该元素已经存在树中，用新元素替换旧元素
     */
    private boolean addElement(K key,V value){
      return addElement(new Element<K, V>(key,value));
    }

    /**
     * 新增元素
     * @param e 要新增的元素
     * @return true:不存在该元素，已经新增进去  false:1.该元素已经存在树中，用新元素替换旧元素
     */
    private boolean addElement(Element<K,V> e){
        return false;
    }


    /**
     * 查找B树，获取指定key对应的value
     * @param key 指定的key
     * @return 对应的value，没有返回null
     */
    public V findValue(K key){
        Element<K, V> element = findElement(key);
        if(element!=null){
            return element.getValue();
        }
        return  null;
    }

    /**
     * 通过指定key查找元素
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
        for (Element<K, V> e : node.getElements()) {
            int compareResult = e.getKey().compareTo(key);
            if (compareResult > 0) {//查找元素小于当前遍历元素，表示查找的元素可能在当前元素的左子树上
                Node<K, V> leftNode = e.getLeftNode();
                if (leftNode == null) {//没有左子树，表示不存在
                    return null;
                } else {
                    return findElement(leftNode, key);//使用左子树作为参数，递归查找
                }
            } else if (compareResult < 0) {//当前元素小于查找的key，表示查找的元素可能在右子树或者下一个元素中
                Node<K, V> rightNode = e.getRightNode();
                if (rightNode != null) {//当前节点的右子树存在，继续对右子树进行递归查找
                    return findElement(rightNode, key);
                } //else{当前元素的右子树不存在，表示在当前元素的后一个元素,处理下一个元素}
            } else {//找到了与查找的元素相等的数据
                return e;
            }
        }
        return null;//查找到叶子节点了，但是仍然没找到，返回null
    }

    /**
     * 查找可以允许当前元素插入的节点
     *
     * @param insertKey 要插入的元素对应的key值
     * @return 返回允许插入当前元素的节点, 如果在搜索的过程中发现元素已经存在，则返回null
     */
    private Node<K, V> findInsertAbleNode(Node<K, V> startNode, K insertKey) {
        for (Element<K, V> e : startNode.getElements()) {
            int compareResult = e.getKey().compareTo(insertKey);
            if (compareResult > 0) {//查找元素小于当前遍历元素，表示查找的元素可能在当前元素的左子树上
                Node<K, V> leftNode = e.getLeftNode();
                if (leftNode == null) {//没有左子树，表示当前元素需要插入的位置就是当前元素所在节点
                    return e.getCurrentNode();
                } else {
                    return findInsertAbleNode(leftNode, insertKey);//使用左子树作为参数，递归查找
                }
            } else if (compareResult < 0) {//当前元素小于查找的key，表示查找的元素可能在右子树或者下一个元素中
                Node<K, V> rightNode = e.getRightNode();
                if (rightNode != null) {//当前节点的右子树存在，继续对右子树进行递归查找
                    return findInsertAbleNode(rightNode, insertKey);
                } //else{当前元素的右子树不存在，表示在当前元素的后一个元素,处理下一个元素}
            } else {//找到了与查找的元素相等的数据,不需要进行插入
                return null;
            }
        }
        return null;
    }

}

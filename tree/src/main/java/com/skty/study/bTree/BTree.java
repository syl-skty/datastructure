package com.skty.study.bTree;

/**
 * B树对象
 */
public class  BTree<K extends Comparable,V> {
    /**
     * b数的阶
     */
    private int size;

    /**
     * 当前数的根节点
     */
    private Node<K,V> root;

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
     * @param k 指定的key
     * @return 返回对应的元素，不存在则返回null
     */
    private Element<K,V> findElement(K k){
        return null;
    }

}

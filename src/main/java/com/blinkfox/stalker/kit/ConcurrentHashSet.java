package com.blinkfox.stalker.kit;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 支持高并发操作的 HashSet 集合，内部使用 {@link ConcurrentHashMap} 实现.
 *
 * @author blinkfox on 2020-05-30.
 * @since v1.2.0
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, java.io.Serializable {

    private static final long serialVersionUID = 8473385025738451022L;

    /**
     * 恒定的不可变对象，每个 key 都引用此对象，以节省空间.
     */
    private static final Object PRESENT = new Object();

    private final ConcurrentMap<E, Object> map;

    /**
     * 默认构造方法.
     */
    public ConcurrentHashSet() {
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * 基于初始容量的构造方法.
     *
     * @param capacity 初始容量
     */
    public ConcurrentHashSet(int capacity) {
        this.map = new ConcurrentHashMap<>(capacity);
    }

    /**
     * 返回此集合中元素的迭代器.
     *
     * @return 迭代器
     * @see java.util.ConcurrentModificationException
     */
    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }

    /**
     * 返回此集合中的元素个数.
     *
     * @return 元素个数
     */
    @Override
    public int size() {
        return this.map.size();
    }

    /**
     * 判断此集合是否为空.
     *
     * @return 如果为空就返回 {@code true}
     */
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * 判断此集合中是否包含指定的元素.
     *
     * @param o 元素
     * @return 布尔值
     */
    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    /**
     * 如果此元素在集合中不存在，就向此集合中添加元素.
     *
     * @param e 要添加的元素
     * @return 如果此集合中还没有包含此元素就返回 {@code true}
     */
    @Override
    public boolean add(E e) {
        return this.map.put(e, PRESENT) == null;
    }

    /**
     * 如果此集合中包含某个元素，就将该元素从集合中移除.
     *
     * @param o 要移除的对象
     * @return 如果包含了该元素，就返回 {@code true}.
     */
    @Override
    public boolean remove(Object o) {
        return this.map.remove(o) == PRESENT;
    }

    /**
     * 删除此集合中的所有元素，之后集合将为空.
     */
    @Override
    public void clear() {
        this.map.clear();
    }

}

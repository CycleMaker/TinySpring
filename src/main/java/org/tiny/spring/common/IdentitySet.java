package org.tiny.spring.common;

import java.util.*;

public class IdentitySet implements Set<Object> {
    private final Map<Object, Object> map;
    private final Object CONTAINS;

    public IdentitySet() {
        this(10);
    }

    public IdentitySet(int size) {
        this.CONTAINS = new Object();
        this.map = new IdentityHashMap(size);
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    public Iterator<Object> iterator() {
        return this.map.keySet().iterator();
    }

    public Object[] toArray() {
        return this.map.keySet().toArray();
    }

    public boolean add(Object o) {
        return this.map.put(o, this.CONTAINS) == null;
    }

    public boolean remove(Object o) {
        return this.map.remove(o) == this.CONTAINS;
    }

    public boolean addAll(Collection<? extends Object> c) {
        boolean doThing = false;

        Object o;
        for(Iterator var3 = c.iterator(); var3.hasNext(); doThing = doThing || this.add(o)) {
            o = var3.next();
        }

        return doThing;
    }

    public void clear() {
        this.map.clear();
    }

    public boolean removeAll(Collection<? extends Object> c) {
        boolean remove = false;

        Object o;
        for(Iterator var3 = c.iterator(); var3.hasNext(); remove = remove || this.remove(o)) {
            o = var3.next();
        }

        return remove;
    }

    public boolean retainAll(Collection<? extends Object> c) {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<? extends Object> c) {
        Iterator var2 = c.iterator();

        Object o;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            o = var2.next();
        } while(this.contains(o));

        return false;
    }

    public Object[] toArray(Object[] a) {
        return this.map.keySet().toArray(a);
    }

    public String toString() {
        return "IdentitySet{map=" + this.map + '}';
    }
}
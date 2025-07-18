/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests.
 */
public class SetUniqueListTest<E> extends AbstractListTest<E> {

    final class SetUniqueList307 extends SetUniqueList<E> {
        /**
         * Generated serial version ID.
         */
        private static final long serialVersionUID = 1415013031022962158L;

        SetUniqueList307(final List<E> list, final Set<E> set) {
            super(list, set);
        }
    }

    boolean extraVerify = true;

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullNonNullElements() {
        // override to avoid duplicate "One"
        return (E[]) new Object[] {
            StringUtils.EMPTY,
            "One",
            Integer.valueOf(2),
            "Three",
            Integer.valueOf(4),
            Double.valueOf(5),
            Float.valueOf(6),
            "Seven",
            "Eight",
            "Nine",
            Integer.valueOf(10),
            Short.valueOf((short) 11),
            Long.valueOf(12),
            "Thirteen",
            "14",
            "15",
            Byte.valueOf((byte) 16)
        };
    }

    @Override
    public List<E> makeObject() {
        return new SetUniqueList<>(new ArrayList<>(), new HashSet<>());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdd() {
        final SetUniqueList<E> lset = new SetUniqueList<>(new ArrayList<>(), new HashSet<>());

        // Duplicate element
        final E obj = (E) Integer.valueOf(1);
        lset.add(obj);
        lset.add(obj);
        assertEquals(1, lset.size(), "Duplicate element was added.");

        // Unique element
        lset.add((E) Integer.valueOf(2));
        assertEquals(2, lset.size(), "Unique element was not added.");
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAddAll() {
        final SetUniqueList<E> lset = new SetUniqueList<>(new ArrayList<>(), new HashSet<>());

        lset.addAll(
            Arrays.asList((E[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(1)}));

        assertEquals(1, lset.size(), "Duplicate element was added.");
    }

    @Test
    @Override
    public void testCollectionAddAll() {
        // override for set behavior
        resetEmpty();
        E[] elements = getFullElements();
        boolean r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue(r, "Empty collection should change after addAll");
        for (final E element : elements) {
            assertTrue(getCollection().contains(element),
                    "Collection should contain added element");
        }

        resetFull();
        final int size = getCollection().size();
        elements = getOtherElements();
        r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue(r, "Full collection should change after addAll");
        for (int i = 0; i < elements.length; i++) {
            assertTrue(getCollection().contains(elements[i]),
                    "Full collection should contain added element " + i);
        }
        assertEquals(size + elements.length, getCollection().size(),
                "Size should increase after addAll");
    }

    @Test
    @Override
    public void testCollectionIteratorRemove() {
        try {
            extraVerify = false;
            super.testCollectionIteratorRemove();
        } finally {
            extraVerify = true;
        }
    }
    @Test
    void testCollections304() {
        final List<String> list = new LinkedList<>();
        final SetUniqueList<String> decoratedList = SetUniqueList.setUniqueList(list);
        final String s1 = "Apple";
        final String s2 = "Lemon";
        final String s3 = "Orange";
        final String s4 = "Strawberry";

        decoratedList.add(s1);
        decoratedList.add(s2);
        decoratedList.add(s3);
        assertEquals(3, decoratedList.size());

        decoratedList.set(1, s4);
        assertEquals(3, decoratedList.size());

        decoratedList.add(1, s4);
        assertEquals(3, decoratedList.size());

        decoratedList.add(1, s2);
        assertEquals(4, decoratedList.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCollections307() {
        List<E> list = new ArrayList<>();
        List<E> uniqueList = SetUniqueList.setUniqueList(list);

        final String hello = "Hello";
        final String world = "World";
        uniqueList.add((E) hello);
        uniqueList.add((E) world);

        List<E> subList = list.subList(0, 0);
        List<E> subUniqueList = uniqueList.subList(0, 0);

        assertFalse(subList.contains(world)); // passes
        assertFalse(subUniqueList.contains(world)); // fails

        List<E> worldList = new ArrayList<>();
        worldList.add((E) world);
        assertFalse(subList.contains("World")); // passes
        assertFalse(subUniqueList.contains("World")); // fails

        // repeat the test with a different class than HashSet;
        // which means subclassing SetUniqueList below
        list = new ArrayList<>();
        uniqueList = new SetUniqueList307(list, new TreeSet<>());

        uniqueList.add((E) hello);
        uniqueList.add((E) world);

        subList = list.subList(0, 0);
        subUniqueList = uniqueList.subList(0, 0);

        assertFalse(subList.contains(world)); // passes
        assertFalse(subUniqueList.contains(world)); // fails

        worldList = new ArrayList<>();
        worldList.add((E) world);
        assertFalse(subList.contains("World")); // passes
        assertFalse(subUniqueList.contains("World")); // fails
    }

    @Test
    void testCollections701() {
        final SetUniqueList<Object> uniqueList = new SetUniqueList<>(new ArrayList<>(), new HashSet<>());
        final Integer obj1 = Integer.valueOf(1);
        final Integer obj2 = Integer.valueOf(2);
        uniqueList.add(obj1);
        uniqueList.add(obj2);
        assertEquals(2, uniqueList.size());
        uniqueList.add(uniqueList);
        assertEquals(3, uniqueList.size());
        final List<Object> list = new LinkedList<>();
        final SetUniqueList<Object> decoratedList = SetUniqueList.setUniqueList(list);
        final String s1 = "Apple";
        final String s2 = "Lemon";
        final String s3 = "Orange";
        final String s4 = "Strawberry";
        decoratedList.add(s1);
        decoratedList.add(s2);
        decoratedList.add(s3);
        assertEquals(3, decoratedList.size());
        decoratedList.set(1, s4);
        assertEquals(3, decoratedList.size());
        decoratedList.add(decoratedList);
        assertEquals(4, decoratedList.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCreateSetBasedOnList() {
        final List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        @SuppressWarnings("rawtypes") final SetUniqueList setUniqueList = (SetUniqueList) makeObject();

        // Standard case with HashSet
        final Set<String> setBasedOnList = setUniqueList.createSetBasedOnList(new HashSet<>(), list);
        assertEquals(list.size(), setBasedOnList.size());
        list.forEach(item -> assertTrue(setBasedOnList.contains(item)));

        // Use different Set than HashSet
        final Set<String> setBasedOnList1 = setUniqueList.createSetBasedOnList(new TreeSet<>(), list);
        assertEquals(list.size(), setBasedOnList1.size());
        list.forEach(item -> assertTrue(setBasedOnList1.contains(item)));

        // throws internally NoSuchMethodException --> results in HashSet
        final Set<String> setBasedOnList2 = setUniqueList.createSetBasedOnList(UnmodifiableSet.unmodifiableSet(new HashSet<>()), list);
        assertEquals(list.size(), setBasedOnList2.size());
        list.forEach(item -> assertTrue(setBasedOnList2.contains(item)));

        // provide null values as Parameter
        assertThrows(NullPointerException.class, () -> setUniqueList.createSetBasedOnList(null, list));
        assertThrows(NullPointerException.class, () -> setUniqueList.createSetBasedOnList(new HashSet<>(), null));
    }

    @Test
    void testFactory() {
        final Integer[] array = { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(1) };
        final ArrayList<Integer> list = new ArrayList<>(Arrays.asList(array));
        final SetUniqueList<Integer> lset = SetUniqueList.setUniqueList(list);

        assertEquals(2, lset.size(), "Duplicate element was added.");
        assertEquals(Integer.valueOf(1), lset.get(0));
        assertEquals(Integer.valueOf(2), lset.get(1));
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
    }

    @Test
    void testIntCollectionAddAll() {
        // make a SetUniqueList with one element
        final List<Integer> list = new SetUniqueList<>(new ArrayList<>(), new HashSet<>());
        final Integer existingElement = Integer.valueOf(1);
        list.add(existingElement);

        // add two new unique elements at index 0
        final Integer firstNewElement = Integer.valueOf(2);
        final Integer secondNewElement = Integer.valueOf(3);
        Collection<Integer> collection = Arrays.asList(firstNewElement, secondNewElement);
        list.addAll(0, collection);
        assertEquals(3, list.size(), "Unique elements should be added.");
        assertEquals(firstNewElement, list.get(0), "First new element should be at index 0");
        assertEquals(secondNewElement, list.get(1), "Second new element should be at index 1");
        assertEquals(existingElement, list.get(2), "Existing element should shift to index 2");

        // add a duplicate element and a unique element at index 0
        final Integer thirdNewElement = Integer.valueOf(4);
        collection = Arrays.asList(existingElement, thirdNewElement);
        list.addAll(0, collection);
        assertEquals(4, list.size(),
                "Duplicate element should not be added, unique element should be added.");
        assertEquals(thirdNewElement, list.get(0), "Third new element should be at index 0");
    }

    @Test
    @SuppressWarnings("unchecked")
    void testListIterator() {
        final SetUniqueList<E> lset = new SetUniqueList<>(new ArrayList<>(), new HashSet<>());

        final E obj1 = (E) Integer.valueOf(1);
        final E obj2 = (E) Integer.valueOf(2);
        lset.add(obj1);
        lset.add(obj2);

        // Attempts to add a duplicate object
        for (final ListIterator<E> it = lset.listIterator(); it.hasNext();) {
            it.next();

            if (!it.hasNext()) {
                it.add(obj1);
                break;
            }
        }

        assertEquals(2, lset.size(), "Duplicate element was added");
    }

    @Test
    @Override
    public void testListIteratorAdd() {
        // override to cope with Set behavior
        resetEmpty();
        final List<E> list1 = getCollection();
        final List<E> list2 = getConfirmed();

        final E[] elements = getOtherElements();  // changed here
        ListIterator<E> iter1 = list1.listIterator();
        ListIterator<E> iter2 = list2.listIterator();

        for (final E element : elements) {
            iter1.add(element);
            iter2.add(element);
            super.verify();  // changed here
        }

        resetFull();
        iter1 = getCollection().listIterator();
        iter2 = getConfirmed().listIterator();
        for (final E element : elements) {
            iter1.next();
            iter2.next();
            iter1.add(element);
            iter2.add(element);
            super.verify();  // changed here
        }
    }

    @Test
    @Override
    public void testListIteratorSet() {
        // override to block
        resetFull();
        final ListIterator<E> it = getCollection().listIterator();
        it.next();

        assertThrows(UnsupportedOperationException.class, () -> it.set(null));
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void testListSetByIndex() {
        // override for set behavior
        resetFull();
        final int size = getCollection().size();
        getCollection().set(0, (E) Long.valueOf(1000));
        assertEquals(size, getCollection().size());

        getCollection().set(2, (E) Long.valueOf(1000));
        assertEquals(size - 1, getCollection().size());
        assertEquals(Long.valueOf(1000), getCollection().get(1));  // set into 2, but shifted down to 1
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRetainAll() {
        final List<E> list = new ArrayList<>(10);
        final SetUniqueList<E> uniqueList = SetUniqueList.setUniqueList(list);
        for (int i = 0; i < 10; ++i) {
            uniqueList.add((E) Integer.valueOf(i));
        }

        final Collection<E> retained = new ArrayList<>(5);
        for (int i = 0; i < 5; ++i) {
            retained.add((E) Integer.valueOf(i * 2));
        }

        assertTrue(uniqueList.retainAll(retained));
        assertEquals(5, uniqueList.size());
        assertTrue(uniqueList.contains(Integer.valueOf(0)));
        assertTrue(uniqueList.contains(Integer.valueOf(2)));
        assertTrue(uniqueList.contains(Integer.valueOf(4)));
        assertTrue(uniqueList.contains(Integer.valueOf(6)));
        assertTrue(uniqueList.contains(Integer.valueOf(8)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRetainAllWithInitialList() {
        // initialized with empty list
        final List<E> list = new ArrayList<>(10);
        for (int i = 0; i < 5; ++i) {
            list.add((E) Integer.valueOf(i));
        }
        final SetUniqueList<E> uniqueList = SetUniqueList.setUniqueList(list);
        for (int i = 5; i < 10; ++i) {
            uniqueList.add((E) Integer.valueOf(i));
        }

        final Collection<E> retained = new ArrayList<>(5);
        for (int i = 0; i < 5; ++i) {
            retained.add((E) Integer.valueOf(i * 2));
        }

        assertTrue(uniqueList.retainAll(retained));
        assertEquals(5, uniqueList.size());
        assertTrue(uniqueList.contains(Integer.valueOf(0)));
        assertTrue(uniqueList.contains(Integer.valueOf(2)));
        assertTrue(uniqueList.contains(Integer.valueOf(4)));
        assertTrue(uniqueList.contains(Integer.valueOf(6)));
        assertTrue(uniqueList.contains(Integer.valueOf(8)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSet() {
        final SetUniqueList<E> lset = new SetUniqueList<>(new ArrayList<>(), new HashSet<>());

        // Duplicate element
        final E obj1 = (E) Integer.valueOf(1);
        final E obj2 = (E) Integer.valueOf(2);
        final E obj3 = (E) Integer.valueOf(3);

        lset.add(obj1);
        lset.add(obj2);
        lset.set(0, obj1);
        assertEquals(2, lset.size());
        assertSame(obj1, lset.get(0));
        assertSame(obj2, lset.get(1));

        lset.clear();
        lset.add(obj1);
        lset.add(obj2);
        lset.set(0, obj2);
        assertEquals(1, lset.size());
        assertSame(obj2, lset.get(0));

        lset.clear();
        lset.add(obj1);
        lset.add(obj2);
        lset.set(0, obj3);
        assertEquals(2, lset.size());
        assertSame(obj3, lset.get(0));
        assertSame(obj2, lset.get(1));

        lset.clear();
        lset.add(obj1);
        lset.add(obj2);
        lset.set(1, obj1);
        assertEquals(1, lset.size());
        assertSame(obj1, lset.get(0));
    }

    @Test
    void testSetCollections444() {
        final SetUniqueList<Integer> lset = new SetUniqueList<>(new ArrayList<>(), new HashSet<>());

        // Duplicate element
        final Integer obj1 = Integer.valueOf(1);
        final Integer obj2 = Integer.valueOf(2);

        lset.add(obj1);
        lset.add(obj2);
        lset.set(0, obj1);
        assertEquals(2, lset.size());
        assertSame(obj1, lset.get(0));
        assertSame(obj2, lset.get(1));

        assertTrue(lset.contains(obj1));
        assertTrue(lset.contains(obj2));
    }
    @Test
    @SuppressWarnings("unchecked")
    void testSetDownwardsInList() {
        /*
         * Checks the following semantics
         * [a,b]
         * set(0,b): [b]->a
         * So UniqList contains [b] and a is returned
         */
        final ArrayList<E> l = new ArrayList<>();
        final HashSet<E> s = new HashSet<>();
        final SetUniqueList<E> ul = new SetUniqueList<>(l, s);

        final E a = (E) new Object();
        final E b = (E) new Object();
        ul.add(a);
        ul.add(b);
        assertEquals(a, l.get(0));
        assertEquals(b, l.get(1));
        assertTrue(s.contains(a));
        assertTrue(s.contains(b));

        assertEquals(a, ul.set(0, b));
        assertEquals(1, s.size());
        assertEquals(1, l.size());
        assertEquals(b, l.get(0));
        assertTrue(s.contains(b));
        assertFalse(s.contains(a));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetInBiggerList() {
        /*
         * Checks the following semantics
         * [a,b,c]
         * set(0,b): [b,c]->a
         * So UniqList contains [b,c] and a is returned
         */
        final ArrayList<E> l = new ArrayList<>();
        final HashSet<E> s = new HashSet<>();
        final SetUniqueList<E> ul = new SetUniqueList<>(l, s);

        final E a = (E) new Object();
        final E b = (E) new Object();
        final E c = (E) new Object();

        ul.add(a);
        ul.add(b);
        ul.add(c);
        assertEquals(a, l.get(0));
        assertEquals(b, l.get(1));
        assertEquals(c, l.get(2));
        assertTrue(s.contains(a));
        assertTrue(s.contains(b));
        assertTrue(s.contains(c));

        assertEquals(a, ul.set(0, b));
        assertEquals(2, s.size());
        assertEquals(2, l.size());
        assertEquals(b, l.get(0));
        assertEquals(c, l.get(1));
        assertFalse(s.contains(a));
        assertTrue(s.contains(b));
        assertTrue(s.contains(c));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetUpwardsInList() {
        /*
         * Checks the following semantics
         * [a,b,c]
         * set(1,a): [a,c]->b
         * So UniqList contains [a,c] and b is returned
         */
        final ArrayList<E> l = new ArrayList<>();
        final HashSet<E> s = new HashSet<>();
        final SetUniqueList<E> ul = new SetUniqueList<>(l, s);

        final E a = (E) "A";
        final E b = (E) "B";
        final E c = (E) "C";

        ul.add(a);
        ul.add(b);
        ul.add(c);
        assertEquals(a, l.get(0));
        assertEquals(b, l.get(1));
        assertEquals(c, l.get(2));
        assertTrue(s.contains(a));
        assertTrue(s.contains(b));
        assertTrue(s.contains(c));

        assertEquals(b, ul.set(1, a));
        assertEquals(2, s.size());
        assertEquals(2, l.size());
        assertEquals(a, l.get(0));
        assertEquals(c, l.get(1));
        assertTrue(s.contains(a));
        assertFalse(s.contains(b));
        assertTrue(s.contains(c));
    }

    @Test
    void testSubListIsUnmodifiable() {
        resetFull();
        final List<E> subList = getCollection().subList(1, 3);
        assertEquals(2, subList.size());
        assertThrows(UnsupportedOperationException.class, () -> subList.remove(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testUniqueListDoubleInsert() {
        final List<E> l = SetUniqueList.setUniqueList(new LinkedList<>());
        l.add((E) new Object());
        l.add((E) new Object());

        // duplicate is removed
        l.set(0, l.get(1));
        assertEquals(1, l.size());

        // duplicate should be removed again
        l.add(1, l.get(0));
        assertEquals(1, l.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testUniqueListReInsert() {
        final List<E> l = SetUniqueList.setUniqueList(new LinkedList<>());
        l.add((E) new Object());
        l.add((E) new Object());

        final E a = l.get(0);

        // duplicate is removed
        l.set(0, l.get(1));
        assertEquals(1, l.size());

        // old object is added back in
        l.add(1, a);
        assertEquals(2, l.size());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void verify() {
        super.verify();

        if (extraVerify) {
            final int size = getCollection().size();
            getCollection().add((E) Long.valueOf(1000));
            assertEquals(size + 1, getCollection().size());

            getCollection().add((E) Long.valueOf(1000));
            assertEquals(size + 1, getCollection().size());
            assertEquals(Long.valueOf(1000), getCollection().get(size));

            getCollection().remove(size);
        }
    }

}

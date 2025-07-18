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
package org.apache.commons.collections4.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MultiMap;
import org.junit.jupiter.api.Test;

/**
 * TestMultiValueMap.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
@Deprecated
public class MultiValueMapTest<K, V> extends AbstractObjectTest {

    @SuppressWarnings("unchecked")
    private MultiValueMap<K, V> createTestMap() {
        return createTestMap(ArrayList.class);
    }

    @SuppressWarnings("unchecked")
    private <C extends Collection<V>> MultiValueMap<K, V> createTestMap(final Class<C> collectionClass) {
        final MultiValueMap<K, V> map = MultiValueMap.multiValueMap(new HashMap<>(), collectionClass);
        map.put((K) "one", (V) "uno");
        map.put((K) "one", (V) "un");
        map.put((K) "two", (V) "dos");
        map.put((K) "two", (V) "deux");
        map.put((K) "three", (V) "tres");
        map.put((K) "three", (V) "trois");
        return map;
    }

    private Object deserialize(final byte[] data) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final ObjectInputStream iis = new ObjectInputStream(bais);

        return iis.readObject();
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @SuppressWarnings("rawtypes")
    private Map makeEmptyMap() {
        return new MultiValueMap();
    }

    @Override
    public Object makeObject() {
        @SuppressWarnings("unchecked")
        final Map<String, String> m = makeEmptyMap();
        m.put("a", "1");
        m.put("a", "1b");
        m.put("b", "2");
        m.put("c", "3");
        m.put("c", "3b");
        m.put("d", "4");
        return m;
    }

    private byte[] serialize(final Object object) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
        }
        return baos.toByteArray();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testContainsValue() {
        final MultiValueMap<K, V> map = createTestMap(HashSet.class);
        assertTrue(map.containsValue("uno"));
        assertTrue(map.containsValue("un"));
        assertTrue(map.containsValue("dos"));
        assertTrue(map.containsValue("deux"));
        assertTrue(map.containsValue("tres"));
        assertTrue(map.containsValue("trois"));
        assertFalse(map.containsValue("quatro"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testContainsValue_Key() {
        final MultiValueMap<K, V> map = new MultiValueMap<>();
        assertFalse(map.containsValue("A", "AA"));
        assertFalse(map.containsValue("B", "BB"));
        map.put((K) "A", "AA");
        assertTrue(map.containsValue("A", "AA"));
        assertFalse(map.containsValue("A", "AB"));
    }

    @Test
    void testEmptyMapCompatibility() throws Exception {
        final Map<?, ?> map = makeEmptyMap();
        final Map<?, ?> map2 = (Map<?, ?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
        assertEquals(0, map2.size(), "Map is empty");
    }

    @Test
    void testFullMapCompatibility() throws Exception {
        final Map<?, ?> map = (Map<?, ?>) makeObject();
        final Map<?, ?> map2 = (Map<?, ?>) readExternalFormFromDisk(getCanonicalFullCollectionName(map));
        assertEquals(map.size(), map2.size(), "Map is the right size");
        for (final Object key : map.keySet()) {
            assertEquals(map.get(key), map2.get(key), "Map had unequal elements");
            map2.remove(key);
        }
        assertEquals(0, map2.size(), "Map had extra values");
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetCollection() {
        final MultiValueMap<K, V> map = new MultiValueMap<>();
        map.put((K) "A", "AA");
        assertSame(map.get("A"), map.getCollection("A"));
    }

    @Test
    void testIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        @SuppressWarnings("unchecked")
        final Collection<V> values = new ArrayList<>((Collection<V>) map.values());
        final Iterator<Map.Entry<K, V>> iterator = map.iterator();
        while (iterator.hasNext()) {
            final Map.Entry<K, V> entry = iterator.next();
            assertTrue(map.containsValue(entry.getKey(), entry.getValue()));
            assertTrue(values.contains(entry.getValue()));
            assertTrue(values.remove(entry.getValue()));
        }
        assertTrue(values.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIterator_Key() {
        final MultiValueMap<K, V> map = new MultiValueMap<>();
        assertFalse(map.iterator("A").hasNext());
        map.put((K) "A", "AA");
        final Iterator<?> it = map.iterator("A");
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testKeyContainsValue() {
        final MultiValueMap<K, V> map = createTestMap(HashSet.class);
        assertTrue(map.containsValue("one", "uno"));
        assertTrue(map.containsValue("one", "un"));
        assertTrue(map.containsValue("two", "dos"));
        assertTrue(map.containsValue("two", "deux"));
        assertTrue(map.containsValue("three", "tres"));
        assertTrue(map.containsValue("three", "trois"));
        assertFalse(map.containsValue("four", "quatro"));
    }

    @Test
    void testKeyedIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        final ArrayList<Object> actual = new ArrayList<>(IteratorUtils.toList(map.iterator("one")));
        final ArrayList<Object> expected = new ArrayList<>(Arrays.asList("uno", "un"));
        assertEquals(expected, actual);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testMapEquals() {
        final MultiValueMap<K, V> one = new MultiValueMap<>();
        final Integer value = Integer.valueOf(1);
        one.put((K) "One", value);
        one.removeMapping("One", value);

        final MultiValueMap<K, V> two = new MultiValueMap<>();
        assertEquals(two, one);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testMultipleValues() {
        final MultiValueMap<K, V> map = createTestMap(HashSet.class);
        final HashSet<V> expected = new HashSet<>();
        expected.add((V) "uno");
        expected.add((V) "un");
        assertEquals(expected, map.get("one"));
    }

    @Test
    void testNoMappingReturnsNull() {
        final MultiValueMap<K, V> map = createTestMap();
        assertNull(map.get("whatever"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPutAll_KeyCollection() {
        final MultiValueMap<K, V> map = new MultiValueMap<>();
        Collection<V> coll = (Collection<V>) Arrays.asList("X", "Y", "Z");

        assertTrue(map.putAll((K) "A", coll));
        assertEquals(3, map.size("A"));
        assertTrue(map.containsValue("A", "X"));
        assertTrue(map.containsValue("A", "Y"));
        assertTrue(map.containsValue("A", "Z"));

        assertFalse(map.putAll((K) "A", null));
        assertEquals(3, map.size("A"));
        assertTrue(map.containsValue("A", "X"));
        assertTrue(map.containsValue("A", "Y"));
        assertTrue(map.containsValue("A", "Z"));

        assertFalse(map.putAll((K) "A", new ArrayList<>()));
        assertEquals(3, map.size("A"));
        assertTrue(map.containsValue("A", "X"));
        assertTrue(map.containsValue("A", "Y"));
        assertTrue(map.containsValue("A", "Z"));

        coll = (Collection<V>) Arrays.asList("M");
        assertTrue(map.putAll((K) "A", coll));
        assertEquals(4, map.size("A"));
        assertTrue(map.containsValue("A", "X"));
        assertTrue(map.containsValue("A", "Y"));
        assertTrue(map.containsValue("A", "Z"));
        assertTrue(map.containsValue("A", "M"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPutAll_Map1() {
        final MultiMap<K, V> original = new MultiValueMap<>();
        original.put((K) "key", "object1");
        original.put((K) "key", "object2");

        final MultiValueMap<K, V> test = new MultiValueMap<>();
        test.put((K) "keyA", "objectA");
        test.put((K) "key", "object0");
        test.putAll(original);

        assertEquals(2, test.size());
        assertEquals(4, test.totalSize());
        assertEquals(1, test.getCollection("keyA").size());
        assertEquals(3, test.getCollection("key").size());
        assertTrue(test.containsValue("objectA"));
        assertTrue(test.containsValue("object0"));
        assertTrue(test.containsValue("object1"));
        assertTrue(test.containsValue("object2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPutAll_Map2() {
        final Map<K, V> original = new HashMap<>();
        original.put((K) "keyX", (V) "object1");
        original.put((K) "keyY", (V) "object2");

        final MultiValueMap<K, V> test = new MultiValueMap<>();
        test.put((K) "keyA", "objectA");
        test.put((K) "keyX", "object0");
        test.putAll(original);

        assertEquals(3, test.size());
        assertEquals(4, test.totalSize());
        assertEquals(1, test.getCollection("keyA").size());
        assertEquals(2, test.getCollection("keyX").size());
        assertEquals(1, test.getCollection("keyY").size());
        assertTrue(test.containsValue("objectA"));
        assertTrue(test.containsValue("object0"));
        assertTrue(test.containsValue("object1"));
        assertTrue(test.containsValue("object2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPutWithList() {
        final MultiValueMap<K, V> test = MultiValueMap.multiValueMap(new HashMap<>(), ArrayList.class);
        assertEquals("a", test.put((K) "A", "a"));
        assertEquals("b", test.put((K) "A", "b"));
        assertEquals(1, test.size());
        assertEquals(2, test.size("A"));
        assertEquals(2, test.totalSize());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPutWithSet() {
        final MultiValueMap<K, V> test = MultiValueMap.multiValueMap(new HashMap<>(), HashSet.class);
        assertEquals("a", test.put((K) "A", "a"));
        assertEquals("b", test.put((K) "A", "b"));
        assertNull(test.put((K) "A", "a"));
        assertEquals(1, test.size());
        assertEquals(2, test.size("A"));
        assertEquals(2, test.totalSize());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemove_KeyItem() {
        final MultiValueMap<K, V> map = new MultiValueMap<>();
        map.put((K) "A", "AA");
        map.put((K) "A", "AB");
        map.put((K) "A", "AC");
        assertFalse(map.removeMapping("C", "CA"));
        assertFalse(map.removeMapping("A", "AD"));
        assertTrue(map.removeMapping("A", "AC"));
        assertTrue(map.removeMapping("A", "AB"));
        assertTrue(map.removeMapping("A", "AA"));
        assertEquals(new MultiValueMap<>(), map);
    }

    @Test
    void testRemoveAllViaEntryIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        for (final Iterator<?> i = map.iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertEquals(0, map.totalSize());
    }

    @Test
    void testRemoveAllViaIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        for (final Iterator<?> i = map.values().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertTrue(map.isEmpty());
    }

    @Test
    void testRemoveAllViaKeyedIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        for (final Iterator<?> i = map.iterator("one"); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertEquals(4, map.totalSize());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSize() {
        final MultiValueMap<K, V> map = new MultiValueMap<>();
        assertEquals(0, map.size());
        map.put((K) "A", "AA");
        assertEquals(1, map.size());
        map.put((K) "B", "BA");
        assertEquals(2, map.size());
        map.put((K) "B", "BB");
        assertEquals(2, map.size());
        map.put((K) "B", "BC");
        assertEquals(2, map.size());
        map.remove("A");
        assertEquals(1, map.size());
        map.removeMapping("B", "BC");
        assertEquals(1, map.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSize_Key() {
        final MultiValueMap<K, V> map = new MultiValueMap<>();
        assertEquals(0, map.size("A"));
        assertEquals(0, map.size("B"));
        map.put((K) "A", "AA");
        assertEquals(1, map.size("A"));
        assertEquals(0, map.size("B"));
        map.put((K) "B", "BA");
        assertEquals(1, map.size("A"));
        assertEquals(1, map.size("B"));
        map.put((K) "B", "BB");
        assertEquals(1, map.size("A"));
        assertEquals(2, map.size("B"));
        map.put((K) "B", "BC");
        assertEquals(1, map.size("A"));
        assertEquals(3, map.size("B"));
        map.remove("A");
        assertEquals(0, map.size("A"));
        assertEquals(3, map.size("B"));
        map.removeMapping("B", "BC");
        assertEquals(0, map.size("A"));
        assertEquals(2, map.size("B"));
    }

    // Manual serialization testing as this class cannot easily
    // extend the AbstractTestMap

    @Test
    @SuppressWarnings("unchecked")
    void testTotalSize() {
        final MultiValueMap<K, V> map = new MultiValueMap<>();
        assertEquals(0, map.totalSize());
        map.put((K) "A", "AA");
        assertEquals(1, map.totalSize());
        map.put((K) "B", "BA");
        assertEquals(2, map.totalSize());
        map.put((K) "B", "BB");
        assertEquals(3, map.totalSize());
        map.put((K) "B", "BC");
        assertEquals(4, map.totalSize());
        map.remove("A");
        assertEquals(3, map.totalSize());
        map.removeMapping("B", "BC");
        assertEquals(2, map.totalSize());
    }

    @Test
    void testTotalSizeA() {
        assertEquals(6, createTestMap().totalSize());
    }

    @Test
    void testUnsafeDeSerialization() throws Exception {
        final MultiValueMap map1 = MultiValueMap.multiValueMap(new HashMap(), ArrayList.class);
        byte[] bytes = serialize(map1);
        final Object result = deserialize(bytes);
        assertEquals(map1, result);

        final MultiValueMap map2 = MultiValueMap.multiValueMap(new HashMap(), (Class) String.class);
        bytes = serialize(map2);

        final byte[] finalBytes = bytes;
        assertThrows(UnsupportedOperationException.class, () -> deserialize(finalBytes));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testValueCollectionType() {
        final MultiValueMap<K, V> map = createTestMap(LinkedList.class);
        assertTrue(map.get("one") instanceof LinkedList);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testValues() {
        final MultiValueMap<K, V> map = createTestMap(HashSet.class);
        final HashSet<V> expected = new HashSet<>();
        expected.add((V) "uno");
        expected.add((V) "dos");
        expected.add((V) "tres");
        expected.add((V) "un");
        expected.add((V) "deux");
        expected.add((V) "trois");
        final Collection<Object> c = map.values();
        assertEquals(6, c.size());
        assertEquals(expected, new HashSet<>(c));
    }

//    void testCreate() throws Exception {
//        writeExternalFormToDisk(
//            (java.io.Serializable) makeEmptyMap(),
//            "src/test/resources/data/test/MultiValueMap.emptyCollection.version4.obj");
//
//        writeExternalFormToDisk(
//            (java.io.Serializable) makeObject(),
//            "src/test/resources/data/test/MultiValueMap.fullCollection.version4.obj");
//    }

}

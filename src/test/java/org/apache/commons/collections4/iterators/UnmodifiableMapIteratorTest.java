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
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.junit.jupiter.api.Test;

/**
 * Tests the UnmodifiableMapIterator.
 *
 * @param <K> the type of the keys in the maps tested.
 * @param <V> the type of the values in the maps tested.
 */
public class UnmodifiableMapIteratorTest<K, V> extends AbstractMapIteratorTest<K, V> {

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, V> getConfirmedMap() {
        final Map<K, V> testMap = new HashMap<>();
        testMap.put((K) "A", (V) "a");
        testMap.put((K) "B", (V) "b");
        testMap.put((K) "C", (V) "c");
        return testMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IterableMap<K, V> getMap() {
        final IterableMap<K, V> testMap = new DualHashBidiMap<>();
        testMap.put((K) "A", (V) "a");
        testMap.put((K) "B", (V) "b");
        testMap.put((K) "C", (V) "c");
        return testMap;
    }

    @Override
    public MapIterator<K, V> makeEmptyIterator() {
        return UnmodifiableMapIterator.unmodifiableMapIterator(new DualHashBidiMap<K, V>().mapIterator());
    }

    @Override
    public MapIterator<K, V> makeObject() {
        return UnmodifiableMapIterator.unmodifiableMapIterator(getMap().mapIterator());
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Override
    public boolean supportsSetValue() {
        return false;
    }

    @Test
    void testDecorateFactory() {
        MapIterator<K, V> it = makeObject();
        assertSame(it, UnmodifiableMapIterator.unmodifiableMapIterator(it));

        it = getMap().mapIterator();
        assertNotSame(it, UnmodifiableMapIterator.unmodifiableMapIterator(it));

        assertThrows(NullPointerException.class, () -> UnmodifiableMapIterator.unmodifiableMapIterator(null));
    }

    @Test
    void testMapIterator() {
        assertTrue(makeEmptyIterator() instanceof Unmodifiable);
    }

}

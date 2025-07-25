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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public abstract class AbstractHasherTest extends AbstractIndexExtractorTest {

    @Override
    protected IndexExtractor createEmptyExtractor() {
        return createEmptyHasher().indices(getTestShape());
    }

    protected abstract Hasher createEmptyHasher();

    @Override
    protected IndexExtractor createExtractor() {
        return createHasher().indices(getTestShape());
    }

    protected abstract Hasher createHasher();

    /**
     * A method to get the number of items in a hasher. Mostly applies to
     * Collections of hashers.
     * @param hasher the hasher to check.
     * @return the number of hashers in the hasher
     */
    protected abstract int getHasherSize(Hasher hasher);

    /**
     * The shape of the Hashers filters for testing.
     * <ul>
     *  <li>Hash functions (k) = 17
     *  <li>Number of bits (m) = 72
     * </ul>
     * @return the testing shape.
     */
    protected final Shape getTestShape() {
        return Shape.fromKM(17, 72);
    }

    @ParameterizedTest
    @CsvSource({
        "17, 72",
        "3, 14",
        "5, 67868",
        "75, 10"
    })
    void testHashing(final int k, final int m) {
        final int[] count = {0};
        final Hasher hasher = createHasher();
        hasher.indices(Shape.fromKM(k, m)).processIndices(i -> {
            assertTrue(i >= 0 && i < m, () -> "Out of range: " + i + ", m=" + m);
            count[0]++;
            return true;
        });
        assertEquals(k * getHasherSize(hasher), count[0],
                () -> String.format("Did not produce k=%d * m=%d indices", k, getHasherSize(hasher)));

        // test early exit
        count[0] = 0;
        hasher.indices(Shape.fromKM(k, m)).processIndices(i -> {
            assertTrue(i >= 0 && i < m, () -> "Out of range: " + i + ", m=" + m);
            count[0]++;
            return false;
        });
        assertEquals(1, count[0], "did not exit early");
    }
}

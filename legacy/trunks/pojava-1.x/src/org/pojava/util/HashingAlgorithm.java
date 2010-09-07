package org.pojava.util;

/*
 Copyright 2008 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.util.Iterator;

import org.pojava.ordinals.Ordinal;
import org.pojava.ordinals.OrdinalSet;

/**
 * A HashingAlgorithm ordinal represents a hashing algorithm supported by HashingTool.
 * 
 * @author John Pile
 * 
 */
public class HashingAlgorithm extends Ordinal {

    private static final OrdinalSet ordinals = new OrdinalSet();

    public static final HashingAlgorithm MD5 = new HashingAlgorithm("MD5");
    public static final HashingAlgorithm SHA = new HashingAlgorithm("SHA");
    public static final HashingAlgorithm SHA_256 = new HashingAlgorithm("SHA-256");

    /*
     * public static final HashingAlgorithm SHA_384 = new HashingAlgorithm("SHA-384"); public
     * static final HashingAlgorithm SHA_512 = new HashingAlgorithm("SHA-512");
     */

    /**
     * Private constructor used internally
     */
    private HashingAlgorithm(String name) {
        register(ordinals, name, this);
    }

    /**
     * Iterate over list of supported HashingAlgorithms
     */
    public Iterator iterator() {
        return ordinals.iterator();
    }

    /**
     * Access a HashingAlgorithm reference by its name
     * 
     * @param name
     * @return Named HashingAlgorithm
     */
    public static HashingAlgorithm valueOf(String name) {
        Ordinal located = ordinals.get(name);
        if (located == null) {
            throw new IllegalArgumentException("No ordinal class "
                    + HashingAlgorithm.class.getName() + "." + name);
        }
        return (HashingAlgorithm) located;
    }

}

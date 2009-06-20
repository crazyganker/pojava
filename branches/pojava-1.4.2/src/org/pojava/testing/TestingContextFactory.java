package org.pojava.testing;

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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Factory class for instantiating an InitialContext for unit testing.
 * 
 * @author John Pile
 * 
 */
public class TestingContextFactory implements InitialContextFactory {

    private static Context ctx;

    public Context getInitialContext(Hashtable map) throws NamingException {
        if (ctx == null) {
            ctx = new TestingContext();
        }
        return ctx;
    }

}
package org.pojava.transformation;

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

/**
 * A TypedAdaptor is a two-way data transformer used for translating a single typed value with
 * an external representation.
 * 
 * It is responsible for translating both the data and the expected class in both directions,
 * even for null values.
 * 
 * It adds a specifier for inbound and outbound class types to the more generalized
 * BindingAdaptor.
 * 
 * @author John Pile
 * 
 */
public interface TypedAdaptor extends BindingAdaptor {

    /**
     * Return the Class that the inbound Binding will provide.
     * 
     * @return Class after transformation.
     */
    Class inboundType();

    /**
     * Return the Class that the outbound Binding will provide.
     * 
     * @return Class after transformation.
     */
    Class outboundType();

}

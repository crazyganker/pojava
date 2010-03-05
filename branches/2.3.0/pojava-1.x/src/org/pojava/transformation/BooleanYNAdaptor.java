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

import org.pojava.lang.Binding;

/*
 * The BooleanYNAdaptor transforms a Boolean true/false value to a
 * character Y/N value.
 */
public class BooleanYNAdaptor implements TypedAdaptor {

    private static final Class INBOUND_TYPE = Boolean.class;
    private static final Class OUTBOUND_TYPE = String.class;

    public Class inboundType() {
        return INBOUND_TYPE;
    }

    public Class outboundType() {
        return OUTBOUND_TYPE;
    }

    public Binding inbound(Binding fromBinding) {
        Binding toBinding = new Binding(INBOUND_TYPE, null);
        if (fromBinding == null || fromBinding.getObj() == null) {
            return toBinding;
        }
        String fromStr = fromBinding.getObj().toString();
        if (fromStr.length() == 0) {
            return toBinding;
        }
        char fromChar = fromStr.charAt(0);
        if (Character.toUpperCase(fromChar) == 'Y') {
            toBinding.setObj(Boolean.TRUE);
        } else if (Character.toUpperCase(fromChar) == 'N') {
            toBinding.setObj(Boolean.FALSE);
        }
        return toBinding;
    }

    public Binding outbound(Binding fromBinding) {
        Binding toBinding = new Binding(OUTBOUND_TYPE, null);
        if (fromBinding == null || fromBinding.getObj() == null) {
            return toBinding;
        }
        if (fromBinding.getObj().getClass().equals(INBOUND_TYPE)) {
            toBinding.setObj(((Boolean) fromBinding.getObj()).booleanValue() ? "Y" : "N");
        } else {
            throw new IllegalStateException(
                    "BooleanIntegerAdaptor.outbound cannot interpret binding of type "
                            + fromBinding.getObj().getClass().getName() + ".");
        }
        return toBinding;
    }

}

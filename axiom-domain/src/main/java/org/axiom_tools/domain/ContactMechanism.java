/**
 * Copyright 2015 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.axiom_tools.domain;

import java.io.Serializable;
import javax.xml.bind.annotation.*;

/**
 * A generic contact mechanism.
 *
 * @author nik
 */
@XmlRootElement
@XmlSeeAlso({MailAddress.class, EmailAddress.class, PhoneNumber.class})
public class ContactMechanism<MechanismType> implements Serializable {

    private static final long serialVersionUID = 1001001L;

    private Contact.Kind type;
    private MechanismType mechanism;

    public static <MechanismType> ContactMechanism<MechanismType> with(Contact.Kind type, MechanismType mechanism) {
        ContactMechanism result = new ContactMechanism();
        result.mechanism = mechanism;
        result.type = type;
        return result;
    }

    /**
     * A mechanism type.
     *
     * @return a type name
     */
    public String getType() {
        return type.name();
    }

    /**
     * A specific contact mechanism.
     *
     * @return depends on type
     */
    @XmlElements(value = {
        @XmlElement(name = "address", type = MailAddress.class),
        @XmlElement(name = "email", type = EmailAddress.class),
        @XmlElement(name = "phone", type = PhoneNumber.class)
    })
    public MechanismType getMechanism() {
        return mechanism;
    }

} // ContactMechanism<Mechanism>

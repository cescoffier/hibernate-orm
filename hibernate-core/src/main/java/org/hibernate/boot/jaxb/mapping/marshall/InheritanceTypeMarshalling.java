/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.jaxb.mapping.marshall;

import jakarta.persistence.InheritanceType;

/**
 * JAXB marshalling for {@link InheritanceType}
 *
 * @author Steve Ebersole
 */
public class InheritanceTypeMarshalling {
	public static InheritanceType fromXml(String name) {
		return InheritanceType.valueOf( name );
	}

	public static String toXml(InheritanceType accessType) {
		return accessType.name();
	}
}

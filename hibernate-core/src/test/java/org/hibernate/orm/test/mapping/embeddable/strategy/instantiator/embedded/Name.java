/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.mapping.embeddable.strategy.instantiator.embedded;

//tag::embeddable-instantiator-property[]
public class Name {
	private final String first;
	private final String last;

	private Name() {
		throw new UnsupportedOperationException();
	}

	public Name(String first, String last) {
		this.first = first;
		this.last = last;
	}

	public String getFirstName() {
		return first;
	}

	public String getLastName() {
		return last;
	}
}
//end::embeddable-instantiator-property[]
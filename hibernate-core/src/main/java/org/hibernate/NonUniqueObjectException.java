/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate;

import org.hibernate.pretty.MessageHelper;

/**
 * This exception is thrown when an operation would break session-scoped identity.
 * This occurs if the user tries to associate two different instances of the same
 * Java class with a particular identifier, in the scope of a single Session.
 *
 * @author Gavin King
 */
public class NonUniqueObjectException extends HibernateException {
	private final Object identifier;
	private final String entityName;

	/**
	 * Constructs a {@code NonUniqueObjectException} using the given information.
	 *
	 * @param message A message explaining the exception condition
	 * @param entityId The identifier of the entity
	 * @param entityName The name of the entity
	 */
	public NonUniqueObjectException(String message, Object entityId, String entityName) {
		super( message );
		this.entityName = entityName;
		this.identifier = entityId;
	}

	/**
	 * Constructs a {@code NonUniqueObjectException} using the given information,
	 * and using a standard message.
	 *
	 * @param entityId The identifier of the entity
	 * @param entityName The name of the entity
	 */
	public NonUniqueObjectException(Object entityId, String entityName) {
		this(
				"A different object with the same identifier value was already associated with the session",
				entityId,
				entityName
		);
	}

	public String getEntityName() {
		return entityName;
	}

	public Object getIdentifier() {
		return identifier;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " : " + MessageHelper.infoString( entityName, identifier );
	}
}

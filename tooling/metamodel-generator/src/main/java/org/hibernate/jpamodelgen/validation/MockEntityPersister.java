/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.validation;

import jakarta.persistence.AccessType;
import org.hibernate.QueryException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.DiscriminatorMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.hibernate.jpamodelgen.validation.MockSessionFactory.typeConfiguration;

/**
 * @author Gavin King
 */
@SuppressWarnings("nullness")
public abstract class MockEntityPersister implements EntityPersister, Queryable, DiscriminatorMetadata {

	private static final String[] ID_COLUMN = {"id"};

	private final String entityName;
	private final MockSessionFactory factory;
	private final List<MockEntityPersister> subclassPersisters = new ArrayList<>();
	final AccessType defaultAccessType;
	private final Map<String,Type> propertyTypesByName = new HashMap<>();

	public MockEntityPersister(String entityName, AccessType defaultAccessType, MockSessionFactory factory) {
		this.entityName = entityName;
		this.factory = factory;
		this.defaultAccessType = defaultAccessType;
	}

	void initSubclassPersisters() {
		for (MockEntityPersister other: factory.getMockEntityPersisters()) {
			other.addPersister(this);
			this.addPersister(other);
		}
	}

	private void addPersister(MockEntityPersister entityPersister) {
		if (isSubclassPersister(entityPersister)) {
			subclassPersisters.add(entityPersister);
		}
	}

	private Type getSubclassPropertyType(String propertyPath) {
		return subclassPersisters.stream()
				.map(sp -> sp.getPropertyType(propertyPath))
				.filter(Objects::nonNull)
				.findAny()
				.orElse(null);
	}

	abstract boolean isSubclassPersister(MockEntityPersister entityPersister);

	@Override
	public boolean isSubclassEntityName(String name) {
		return isSubclassPersister(subclassPersisters.stream()
				.filter(persister -> persister.entityName.equals(name))
				.findFirst().orElseThrow());
	}

	@Override
	public SessionFactoryImplementor getFactory() {
		return factory;
	}

	@Override
	public EntityMetamodel getEntityMetamodel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEntityName() {
		return entityName;
	}

	@Override
	public String getName() {
		return entityName;
	}

	@Override
	public final Type getPropertyType(String propertyPath) {
		Type result = propertyTypesByName.get(propertyPath);
		if (result!=null) {
			return result;
		}

		result = createPropertyType(propertyPath);
		if (result == null) {
			//check subclasses, needed for treat()
			result = getSubclassPropertyType(propertyPath);
		}

		if (result!=null) {
			propertyTypesByName.put(propertyPath, result);
		}
		return result;
	}

	abstract Type createPropertyType(String propertyPath);

	@Override
	public Type getIdentifierType() {
		//TODO: propertyType(getIdentifierPropertyName())
		return typeConfiguration.getBasicTypeForJavaType(Long.class);
	}

	@Override
	public String getIdentifierPropertyName() {
		//TODO: return the correct @Id property name
		return "id";
	}

	@Override
	public Type toType(String propertyName) throws QueryException {
		Type type = getPropertyType(propertyName);
		if (type == null) {
			throw new QueryException(getEntityName()
					+ " has no mapped "
					+ propertyName);
		}
		return type;
	}

	@Override
	public String getRootEntityName() {
		for (MockEntityPersister persister : factory.getMockEntityPersisters()) {
			if (this != persister && persister.isSubclassPersister(this)) {
				return persister.getRootEntityName();
			}
		}
		return entityName;
	}

	@Override
	public Set<String> getSubclassEntityNames() {
		Set<String> names = new HashSet<>();
		names.add( entityName );
		for (MockEntityPersister persister : factory.getMockEntityPersisters()) {
			if (persister.isSubclassPersister(this)) {
				names.add(persister.entityName);
			}
		}
		return names;
	}

	@Override
	public Declarer getSubclassPropertyDeclarer(String s) {
		return Declarer.CLASS;
	}

	@Override
	public String[] toColumns(String propertyName) {
		return new String[] { "" };
	}

	@Override
	public Serializable[] getPropertySpaces() {
		return new Serializable[] {entityName};
	}

	@Override
	public Serializable[] getQuerySpaces() {
		return new Serializable[] {entityName};
	}

	@Override
	public EntityPersister getEntityPersister() {
		return this;
	}

	@Override
	public String[] getKeyColumnNames() {
		return getIdentifierColumnNames();
	}

	@Override
	public String[] getIdentifierColumnNames() {
		return ID_COLUMN;
	}

	@Override
	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
		return this;
	}

	@Override
	public Type getResolutionType() {
		return typeConfiguration.getBasicTypeForJavaType(Class.class);
	}

	@Override
	public String getTableName() {
		return entityName;
	}

	@Override
	public String toString() {
		return "MockEntityPersister[" + entityName + "]";
	}

	@Override
	public int getVersionProperty() {
		return -66;
	}

	@Override
	public String getMappedSuperclass() {
		return null;
	}

	@Override
	public boolean consumesEntityAlias() {
		return true;
	}

	@Override
	public Type getDiscriminatorType() {
		return typeConfiguration.getBasicTypeForJavaType(String.class);
	}
}

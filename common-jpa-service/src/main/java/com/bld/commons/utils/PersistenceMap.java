/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.utils.PersistenceMap.java
 */
package com.bld.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.bld.commons.exception.PropertiesException;
import com.bld.commons.reflection.utils.ReflectionCommons;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

/**
 * An ordered, map-like container backed by a {@code List} of {@link java.util.Map.Entry} pairs.
 *
 * <p><strong>IMPORTANT — this class does NOT implement {@link java.util.Map}.</strong>
 * You cannot pass it where a {@code Map} is expected. Use the explicit API described below.
 *
 * <h2>Why it exists</h2>
 * <p>Standard {@code HashMap}/{@code LinkedHashMap} compare keys with {@code hashCode/equals}.
 * JPA entities often override neither, or define equality only in terms of the database identity
 * ({@code @Id} / {@code @EmbeddedId} fields). {@code PersistenceMap} uses reflection-based
 * key comparison that is aware of JPA annotations:
 * <ul>
 *   <li>{@code @Entity} keys: equality is determined by comparing only the {@code @Id} /
 *       {@code @EmbeddedId} fields.</li>
 *   <li>{@code @Embeddable} keys: equality uses all non-{@code @Transient} fields.</li>
 *   <li>All other types: delegates to the standard {@code equals} method.</li>
 * </ul>
 *
 * <h2>Basic usage</h2>
 * <pre>{@code
 * PersistenceMap<Long, Product> map = new PersistenceMap<>();
 * map.put(1L, product);
 *
 * // retrieve
 * Product p = map.get(1L);
 *
 * // check presence
 * boolean exists = map.containsKey(1L);
 *
 * // iterate entries — entrySet() returns List<Map.Entry<K,V>>
 * for (Map.Entry<Long, Product> entry : map.entrySet()) {
 *     Long key   = entry.getKey();
 *     Product val = entry.getValue();
 * }
 *
 * // iterate keys — keySet() returns List<K> (not Set)
 * for (Long key : map.keySet()) { ... }
 *
 * // iterate values
 * for (Product val : map.values()) { ... }
 * }</pre>
 *
 * <h2>Returned by JpaService map methods</h2>
 * <p>{@code PersistenceMap} is the return type of the following service methods:
 * <ul>
 *   <li>{@code mapKeyFindByFilter(queryParameter, classKey, key)} — results indexed by a
 *       dot-notation field path; if two entities share the same key the last one wins.</li>
 *   <li>{@code mapKeyListFindByFilter(queryParameter, classKey, key)} — results grouped into
 *       lists indexed by the same dot-notation key.</li>
 * </ul>
 * Example:
 * <pre>{@code
 * QueryParameter<Product, Long> qp = new QueryParameter<>();
 * qp.addParameter("active", true);
 *
 * // PersistenceMap<Long, Product>: one product per category (last wins)
 * PersistenceMap<Long, Product> byCategory =
 *     productService.mapKeyFindByFilter(qp, Long.class, "category.categoryId");
 *
 * // PersistenceMap<Long, List<Product>>: all products grouped by category
 * PersistenceMap<Long, List<Product>> grouped =
 *     productService.mapKeyListFindByFilter(qp, Long.class, "category.categoryId");
 *
 * // consume the grouped result
 * for (Map.Entry<Long, List<Product>> entry : grouped.entrySet()) {
 *     Long categoryId      = entry.getKey();
 *     List<Product> products = entry.getValue();
 * }
 * }</pre>
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class PersistenceMap<K, V> {

	/** The table. */
	private List<Entry<K, V>> table;

	/** The map fields. */
	private Map<Class<?>, List<Field>> mapFields;

	/**
	 * Instantiates a new persistence map.
	 */
	public PersistenceMap() {
		this.table = new ArrayList<>();
		this.mapFields = new HashMap<>();
	}

	/**
	 * Returns the number of key-value pairs currently in this map.
	 *
	 * @return the number of entries
	 */
	public int size() {
		return this.table.size();
	}

	/**
	 * Returns {@code true} if this map contains no entries.
	 *
	 * @return {@code true} if empty
	 */
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(this.table);
	}

	/**
	 * Gets the entry.
	 *
	 * @param key the key
	 * @return the entry
	 */
	private Pair<Integer, Entry<K, V>> getEntry(K key) {
		Pair<Integer, Entry<K, V>> entry = null;
		Integer i = 0;
		if (key != null) {
			for (Entry<K, V> persistenceEntry : this.table) {
				if (equals(key, persistenceEntry.getKey())) {
					entry = Pair.of(i, persistenceEntry);
					break;
				}
				i++;
			}
		}
		return entry;
	}

	/**
	 * Returns {@code true} if this map contains an entry for the given key.
	 * Key comparison uses JPA-aware reflection (see class Javadoc).
	 *
	 * @param key the key to look up; {@code null} always returns {@code false}
	 * @return {@code true} if a matching entry exists
	 */
	public boolean containsKey(K key) {
		return getEntry(key) != null;
	}

	/**
	 * Returns the value associated with the given key, or {@code null} if not found.
	 * Key comparison uses JPA-aware reflection (see class Javadoc).
	 *
	 * @param key the key to look up; returns {@code null} if key is {@code null}
	 * @return the value, or {@code null} if the key is absent
	 */
	public V get(K key) {
		if (key == null) {
			return null;
		}
		Pair<Integer, Entry<K, V>> pair = getEntry(key);
		V value = null;
		if (pair != null)
			value = pair.getValue().getValue();
		return value;
	}

	/**
	 * Fields.
	 *
	 * @param key the key
	 * @return the list
	 */
	private List<Field> fields(Object key) {
		List<Field> listField = null;
		if (!mapFields.containsKey(key.getClass())) {
			Set<Field> fields = ReflectionCommons.fields(key.getClass());
			listField = new ArrayList<>();
			if (key.getClass().isAnnotationPresent(Entity.class)) {
				for (Field field : fields) {
					if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class))
						listField.add(field);
				}
			} else if (key.getClass().isAnnotationPresent(Embeddable.class)) {
				for (Field field : fields) {
					if (!field.isAnnotationPresent(Transient.class))
						listField.add(field);
				}
			}
			mapFields.put(key.getClass(), listField);
		} else
			listField = mapFields.get(key.getClass());

		return listField;
	}

	/**
	 * Equals.
	 *
	 * @param key  the key
	 * @param key1 the key 1
	 * @return true, if successful
	 */
	private boolean equals(Object key, Object key1) {
		if (key == null)
			return false;
		if (key.getClass().getName() != key1.getClass().getName())
			return false;
		boolean equals = true;
		if (key.getClass().isAnnotationPresent(Entity.class) || key.getClass().isAnnotationPresent(Embeddable.class)) {
			List<Field> fields = fields(key);
			for (Field field : fields) {
				Object value = null, value1 = null;
				try {
					value = PropertyUtils.getProperty(key, field.getName());
					value1 = PropertyUtils.getProperty(key1, field.getName());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					new PropertiesException(e);
				}
				if (value.getClass().isAnnotationPresent(Embeddable.class))
					equals = equals(value, value1);
				else if (value.getClass().isPrimitive())
					equals = (value == value1);
				else
					equals = value.equals(value1);
				if (!equals)
					return false;
			}
		} else
			equals = key.equals(key1);

		return equals;
	}

	/**
	 * Adds or replaces an entry.
	 * If an entry with an equal key (JPA-aware comparison) already exists its value is replaced;
	 * otherwise a new entry is appended in insertion order.
	 *
	 * @param key   the key; must not be {@code null}
	 * @param value the value to associate with the key
	 * @throws RuntimeException if {@code key} is {@code null}
	 */
	public void put(K key, V value) {
		if (key == null) {
			throw new RuntimeException("Illegal key: " + key);
		}

		Pair<Integer, Entry<K, V>> entry = this.getEntry(key);
		if (entry != null)
			entry.getRight().setValue(value);
		else
			this.table.add(new PersistenceEntry(key, value));
	}

	/**
	 * Removes the entry with the given key, if present.
	 * Does nothing if the key is not found.
	 *
	 * @param key the key to remove
	 */
	public void remove(K key) {
		Pair<Integer, Entry<K, V>> entry = this.getEntry(key);
		if (entry != null)
			this.table.remove(entry.getKey().intValue());
	}

	/**
	 * Copies all entries from a standard {@link java.util.Map} into this map.
	 * Existing keys are updated; new keys are appended in the iteration order of {@code map}.
	 *
	 * @param map the source map; must not be {@code null}
	 */
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Copies all entries from another {@code PersistenceMap} into this map.
	 * Existing keys are updated; new keys are appended in the source map's insertion order.
	 *
	 * @param map the source map; must not be {@code null}
	 */
	public void putAll(PersistenceMap<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Removes all entries from this map.
	 */
	public void clear() {
		this.table.clear();
	}

	/**
	 * Returns an ordered list of all keys in insertion order.
	 * Note: unlike {@link java.util.Map#keySet()}, this returns a {@link List}, not a {@link Set}.
	 * Null keys are excluded.
	 *
	 * @return mutable {@code List} of keys; never {@code null}
	 */
	public List<K> keySet() {
		List<K> keys = new ArrayList<>();
		for (Entry<K, V> entry : this.table) {
			if (entry.getKey() != null)
				keys.add(entry.getKey());
		}
		return keys;
	}

	/**
	 * Returns all values in insertion order.
	 * Null values are excluded.
	 *
	 * @return {@code Collection} of values; never {@code null}
	 */
	public Collection<V> values() {
		List<V> values = new ArrayList<>();

		for (Entry<K, V> entry : this.table) {
			if (entry.getValue() != null)
				values.add(entry.getValue());
		}

		return values;
	}

	/**
	 * Returns the backing list of entries in insertion order.
	 * Note: unlike {@link java.util.Map#entrySet()}, this returns a {@link List}, not a {@link Set}.
	 * This is the primary iteration method:
	 * <pre>{@code
	 * for (Map.Entry<K, V> entry : map.entrySet()) {
	 *     K key   = entry.getKey();
	 *     V value = entry.getValue();
	 * }
	 * }</pre>
	 *
	 * @return the live backing {@code List} of entries; never {@code null}
	 */
	public List<Entry<K, V>> entrySet() {
		return this.table;
	}

	/**
	 * The Class PersistenceEntry.
	 */
	private class PersistenceEntry implements Entry<K, V> {

		/** The key. */
		private K key;

		/** The value. */
		private V value;

		/**
		 * Instantiates a new persistence entry.
		 *
		 * @param key   the key
		 * @param value the value
		 */
		private PersistenceEntry(K key, V value) {
			super();
			this.key = key;
			this.value = value;

		}

		/**
		 * Gets the key.
		 *
		 * @return the key
		 */
		@Override
		public K getKey() {
			return this.key;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		@Override
		public V getValue() {
			return this.value;
		}

		/**
		 * Sets the value.
		 *
		 * @param value the value
		 * @return the v
		 */
		@Override
		public V setValue(V value) {
			this.value = value;
			return this.value;
		}

	}

}

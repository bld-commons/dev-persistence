/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.utils.PersistenceMap.java
 */
package bld.commons.utils;

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

import bld.commons.exception.PropertiesException;
import bld.commons.reflection.utils.ReflectionCommons;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

/**
 * The Class PersistenceMap.
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
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return this.table.size();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
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
	 * Contains key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsKey(K key) {
		return getEntry(key) != null;
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the v
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
	 * Put.
	 *
	 * @param key   the key
	 * @param value the value
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
	 * Removes the.
	 *
	 * @param key the key
	 */
	public void remove(K key) {
		Pair<Integer, Entry<K, V>> entry = this.getEntry(key);
		if (entry != null)
			this.table.remove(entry.getKey().intValue());
	}

	/**
	 * Put all.
	 *
	 * @param map the map
	 */
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Put all.
	 *
	 * @param map the map
	 */
	public void putAll(PersistenceMap<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Clear.
	 */
	public void clear() {
		this.table.clear();
	}

	/**
	 * Key set.
	 *
	 * @return the list
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
	 * Values.
	 *
	 * @return the collection
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
	 * Entry set.
	 *
	 * @return the list
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

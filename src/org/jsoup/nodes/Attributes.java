package org.jsoup.nodes;

import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Attributes implements Iterable<Attribute>, Cloneable {

	protected static final String dataPrefix = "data-";
	private LinkedHashMap<String, Attribute> attributes = null;

	public String get(String key) {
		Validate.notEmpty(key);

		if (attributes == null)
			return "";

		Attribute attr = attributes.get(key.toLowerCase());
		return attr != null ? attr.getValue().toString() : "";
	}

	public void put(String key, String value) {
		Attribute attr = new Attribute(key, value);
		put(attr);
	}

	public void put(String key, boolean value) {
		if (value)
			put(new BooleanAttribute(key));
		else
			remove(key);
	}

	public void put(Attribute attribute) {
		Validate.notNull(attribute);
		if (attributes == null)
			attributes = new LinkedHashMap<String, Attribute>(2);
		attributes.put(attribute.getKey().toString(), attribute);
	}

	public void remove(String key) {
		Validate.notEmpty(key);
		if (attributes == null)
			return;
		attributes.remove(key.toLowerCase());
	}

	public boolean hasKey(String key) {
		return attributes != null && attributes.containsKey(key.toLowerCase());
	}

	public int size() {
		if (attributes == null)
			return 0;
		return attributes.size();
	}

	public void addAll(Attributes incoming) {
		if (incoming.size() == 0)
			return;
		if (attributes == null)
			attributes = new LinkedHashMap<String, Attribute>(incoming.size());
		attributes.putAll(incoming.attributes);
	}

	public Iterator<Attribute> iterator() {
		return asList().iterator();
	}

	public List<Attribute> asList() {
		if (attributes == null)
			return Collections.emptyList();

		List<Attribute> list = new ArrayList<Attribute>(attributes.size());
		for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
			list.add(entry.getValue());
		}
		return Collections.unmodifiableList(list);
	}

	public Map<Object, Object> dataset() {
		return new Dataset();
	}

	public String html() {
		StringBuilder accum = new StringBuilder();
		try {
			html(accum, (new Document("")).outputSettings());

		} catch (IOException e) { // ought never happen
			throw new SerializationException(e);
		}
		return accum.toString();
	}

	void html(Appendable accum, Document.OutputSettings out) throws IOException {
		if (attributes == null)
			return;

		for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
			Attribute attribute = entry.getValue();
			accum.append(" ");
			attribute.html(accum, out);
		}
	}

	@Override
	public String toString() {
		return html();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Attributes))
			return false;

		Attributes that = (Attributes) o;

		return !(attributes != null ? !attributes.equals(that.attributes)
				: that.attributes != null);
	}

	@Override
	public int hashCode() {
		return attributes != null ? attributes.hashCode() : 0;
	}

	@Override
	public Attributes clone() {
		if (attributes == null)
			return new Attributes();

		Attributes clone;
		try {
			clone = (Attributes) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		clone.attributes = new LinkedHashMap<String, Attribute>(
				attributes.size());
		for (Attribute attribute : this)
			clone.attributes.put(attribute.getKey().toString(),
					attribute.clone());
		return clone;
	}

	public class Dataset extends HashMap<Object, Object> {

		private Dataset() {
			if (attributes == null)
				attributes = new LinkedHashMap<String, Attribute>(2);
		}

		
		
		
		@Override
		public Set<Map.Entry<Object, Object>> entrySet() {
			return new EntrySet();
		}
		

		@Override
		public String put(Object key, Object value) {
			String dataKey = dataKey(key.toString());
			String oldValue = hasKey(dataKey) ? attributes.get(dataKey)
					.getValue().toString() : null;
			Attribute attr = new Attribute(dataKey, value.toString());
			attributes.put(dataKey, attr);
			return oldValue;
		}

		public class EntrySet extends AbstractSet<Map.Entry<Object, Object>> {

			@Override
			public Iterator<java.util.Map.Entry<Object, Object>> iterator() {
				return new DatasetIterator();
			}

			@Override
			public int size() {
				int count = 0;
				Iterator iter = new DatasetIterator();
				while (iter.hasNext())
					count++;
				return count;
			}
		}

		public class DatasetIterator implements
				Iterator<Map.Entry<Object, Object>> {

			public Iterator<Attribute> attrIter = attributes.values()
					.iterator();

			public Attribute attr;

			public boolean hasNext() {
				while (attrIter.hasNext()) {
					attr = (Attribute)attrIter.next();
					if (attr.isDataAttribute())
						return true;
				}
				return false;
			}

			@Override
			public Map.Entry<Object, Object> next() {
				return new Attribute(attr.getKey().toString()
						.substring(dataPrefix.length()), attr.getValue());
			}

			public void remove() {
				attributes.remove(attr.getKey());
			}
		}

	}

	public static String dataKey(String key) {
		return dataPrefix + key;
	}

}

package org.jsoup.nodes;

import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Attribute extends java.lang.Object implements Map.Entry<Object, Object>, Cloneable   
{
    private static final String[] booleanAttributes = {
            "allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled",
            "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize",
            "noshade", "novalidate", "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected",
            "sortable", "truespeed", "typemustmatch"
    };

    private String key;
    private String value;


    public Attribute(Object key, Object value) {
        Validate.notEmpty(key.toString());
        Validate.notNull(value.toString());
        this.key = key.toString().trim().toLowerCase();
        this.value = value.toString();
    }

  
   /* public String getKey() {
        return key;
    }*/
    
    /*public String getValue() {
    return value;
}*/

/*public String setValue(String value) {
Validate.notNull(value);
String old = this.value;
this.value = value;
return old;
}*/

    
    @Override
    public Object getKey() {
    
    	return key;
    }
    

	@Override
	public Object setValue(Object value) {
		Validate.notNull(value.toString());
        String old = this.value;
        this.value = value.toString();
        return old;
	}
	
	
	@Override
	public Object getValue() {
		return value;
	}


    public void setKey(String key) {
        Validate.notEmpty(key);
        this.key = key.trim().toLowerCase();
    }
    
  
    public String html() {
        StringBuilder accum = new StringBuilder();
        
        try {
        	html(accum, (new Document("")).outputSettings());
        } catch(IOException exception) {
        	throw new SerializationException(exception);
        }
        return accum.toString();
    }
    
    public void html(Appendable accum, Document.OutputSettings out) throws IOException {
        accum.append(key);
        if (!shouldCollapseAttribute(out)) {
            accum.append("=\"");
            Entities.escape(accum, value, out, true, false, false);
            accum.append('"');
        }
    }


    @Override
    public String toString() {
        return html();
    }


    public static Attribute createFromEncoded(String unencodedKey, String encodedValue) {
        String value = Entities.unescape(encodedValue, true);
        return new Attribute(unencodedKey, value);
    }

    protected boolean isDataAttribute() {
        return key.startsWith(Attributes.dataPrefix) && key.length() > Attributes.dataPrefix.length();
    }


    public final boolean shouldCollapseAttribute(Document.OutputSettings out) {
        return ("".equals(value) || value.equalsIgnoreCase(key))
                && out.syntax() == Document.OutputSettings.Syntax.html
                && isBooleanAttribute();
    }

    public boolean isBooleanAttribute() {
        return Arrays.binarySearch(booleanAttributes, key) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;

        Attribute attribute = (Attribute) o;

        if (key != null ? !key.equals(attribute.key) : attribute.key != null) return false;
        return !(value != null ? !value.equals(attribute.value) : attribute.value != null);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public Attribute clone() {
        try {
            return (Attribute) super.clone(); // only fields are immutable strings key and value, so no more deep copy required
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

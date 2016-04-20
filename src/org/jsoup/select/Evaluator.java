package org.jsoup.select;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Evaluates that an element matches the selector.
 */
public class Evaluator  {
   
	public Evaluator() {
    }


    /**
     * Evaluator for tag name
     */
    public static class Tag extends Evaluator implements EvalutorInterface {
        private String tagName;

        public Tag(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (element.tagName().equals(tagName));
        }

        @Override
        public String toString() {
            return String.format("%s", tagName);
        }
    }

    /**
     * Evaluator for element id
     */
    public static class Id extends Evaluator implements EvalutorInterface {
        private String id;

        public Id(String id) {
            this.id = id;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (id.equals(element.id()));
        }

        @Override
        public String toString() {
            return String.format("#%s", id);
        }

    }

    /**
     * Evaluator for element class
     */
    public static class Class extends Evaluator implements EvalutorInterface {
        private String className;

        public Class(String className) {
            this.className = className;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (element.hasClass(className));
        }

        @Override
        public String toString() {
            return String.format(".%s", className);
        }

    }

    /**
     * Evaluator for attribute name matching
     */
    public static class Attribute extends Evaluator implements EvalutorInterface {
        private String key;

        public Attribute(String key) {
            this.key = key;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key);
        }

        @Override
        public String toString() {
            return String.format("[%s]", key);
        }

    }

    /**
     * Evaluator for attribute name prefix matching
     */
    public static class AttributeStarting extends Evaluator implements EvalutorInterface {
        private String keyPrefix;

        public AttributeStarting(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        @Override
        public boolean matches(Element root, Element element) {
            List<org.jsoup.nodes.Attribute> values = element.attributes().asList();
            for (org.jsoup.nodes.Attribute attribute : values) {
                if (attribute.getKey().toString().startsWith(keyPrefix))
                    return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("[^%s]", keyPrefix);
        }

    }

    /**
     * Evaluator for attribute name/value matching
     */
    public static class AttributeWithValue extends AttributeKeyPair {
        public AttributeWithValue(String key, String value) {
            super(key, value);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && value.equalsIgnoreCase(element.attr(key).trim());
        }

        @Override
        public String toString() {
            return String.format("[%s=%s]", key, value);
        }

    }

    /**
     * Evaluator for attribute name != value matching
     */
    public static class AttributeWithValueNot extends AttributeKeyPair {
        public AttributeWithValueNot(String key, String value) {
            super(key, value);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return !value.equalsIgnoreCase(element.attr(key));
        }

        @Override
        public String toString() {
            return String.format("[%s!=%s]", key, value);
        }

    }

    /**
     * Evaluator for attribute name/value matching (value prefix)
     */
    public static class AttributeWithValueStarting extends AttributeKeyPair {
        public AttributeWithValueStarting(String key, String value) {
            super(key, value);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && element.attr(key).toLowerCase().startsWith(value); // value is lower case already
        }

        @Override
        public String toString() {
            return String.format("[%s^=%s]", key, value);
        }

    }

    /**
     * Evaluator for attribute name/value matching (value ending)
     */
    public static class AttributeWithValueEnding extends AttributeKeyPair {
        public AttributeWithValueEnding(String key, String value) {
            super(key, value);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && element.attr(key).toLowerCase().endsWith(value); // value is lower case
        }

        @Override
        public String toString() {
            return String.format("[%s$=%s]", key, value);
        }

    }

    /**
     * Evaluator for attribute name/value matching (value containing)
     */
    public static class AttributeWithValueContaining extends AttributeKeyPair {
        public AttributeWithValueContaining(String key, String value) {
            super(key, value);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && element.attr(key).toLowerCase().contains(value); // value is lower case
        }

        @Override
        public String toString() {
            return String.format("[%s*=%s]", key, value);
        }

    }

    /**
     * Evaluator for attribute name/value matching (value regex matching)
     */
    public static class AttributeWithValueMatching extends Evaluator implements EvalutorInterface {
        String key;
        Pattern pattern;

        public AttributeWithValueMatching(String key, Pattern pattern) {
            this.key = key.trim().toLowerCase();
            this.pattern = pattern;
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.hasAttr(key) && pattern.matcher(element.attr(key)).find();
        }

        @Override
        public String toString() {
            return String.format("[%s~=%s]", key, pattern.toString());
        }

    }

    /**
     * Abstract evaluator for attribute name/value matching
     */
    public abstract static class AttributeKeyPair extends Evaluator implements EvalutorInterface {
        String key;
        String value;

        public AttributeKeyPair(String key, String value) {
            Validate.notEmpty(key);
            Validate.notEmpty(value);

            this.key = key.trim().toLowerCase();
            if (value.startsWith("\"") && value.endsWith("\"")
                    || value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length()-1);
            }
            this.value = value.trim().toLowerCase();
        }
    }

    /**
     * Evaluator for any / all element matching
     */
    public static class AllElements extends Evaluator implements EvalutorInterface {

        @Override
        public boolean matches(Element root, Element element) {
            return true;
        }

        @Override
        public String toString() {
            return "*";
        }
    }

    /**
     * Evaluator for matching by sibling index number (e {@literal <} idx)
     */
    public static class IndexLessThan extends IndexEvaluator {
        public IndexLessThan(int index) {
            super(index);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.elementSiblingIndex() < index;
        }

        @Override
        public String toString() {
            return String.format(":lt(%d)", index);
        }

    }

    /**
     * Evaluator for matching by sibling index number (e {@literal >} idx)
     */
    public static class IndexGreaterThan extends IndexEvaluator {
        public IndexGreaterThan(int index) {
            super(index);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.elementSiblingIndex() > index;
        }

        @Override
        public String toString() {
            return String.format(":gt(%d)", index);
        }

    }

    /**
     * Evaluator for matching by sibling index number (e = idx)
     */
    public static class IndexEquals extends IndexEvaluator {
        public IndexEquals(int index) {
            super(index);
        }

        @Override
        public boolean matches(Element root, Element element) {
            return element.elementSiblingIndex() == index;
        }

        @Override
        public String toString() {
            return String.format(":eq(%d)", index);
        }

    }
    
    /**
     * Evaluator for matching the last sibling (css :last-child)
     */
    public static class IsLastChild extends Evaluator implements EvalutorInterface {
		@Override
		public boolean matches(Element root, Element element) {
			final Element p = element.parent();
			return p != null && !(p instanceof Document) && element.elementSiblingIndex() == p.children().size()-1;
		}
    	
		@Override
		public String toString() {
			return ":last-child";
		}
    }
    
    public static class IsFirstOfType extends IsNthOfType {
		public IsFirstOfType() {
			super(0,1);
		}
		@Override
		public String toString() {
			return ":first-of-type";
		}
    }
    
    public static class IsLastOfType extends IsNthLastOfType {
		public IsLastOfType() {
			super(0,1);
		}
		@Override
		public String toString() {
			return ":last-of-type";
		}
    }

    
    public static abstract class CssNthEvaluator extends Evaluator implements EvalutorInterface {
    	protected final int a, b;
    	
    	public CssNthEvaluator(int a, int b) {
    		this.a = a;
    		this.b = b;
    	}
    	public CssNthEvaluator(int b) {
    		this(0,b);
    	}
    	
    	@Override
    	public boolean matches(Element root, Element element) {
    		final Element p = element.parent();
    		if (p == null || (p instanceof Document)) return false;
    		
    		final int pos = calculatePosition(root, element);
    		if (a == 0) return pos == b;
    		
    		return (pos-b)*a >= 0 && (pos-b)%a==0;
    	}
    	
		@Override
		public String toString() {
			if (a == 0)
				return String.format(":%s(%d)",getPseudoClass(), b);
			if (b == 0)
				return String.format(":%s(%dn)",getPseudoClass(), a);
			return String.format(":%s(%dn%+d)", getPseudoClass(),a, b);
		}
    	
		protected abstract String getPseudoClass();
		protected abstract int calculatePosition(Element root, Element element);
    }
    
    
    /**
     * css-compatible Evaluator for :eq (css :nth-child)
     * 
     * @see IndexEquals
     */
    public static class IsNthChild extends CssNthEvaluator {

    	public IsNthChild(int a, int b) {
    		super(a,b);
		}

		protected int calculatePosition(Element root, Element element) {
			return element.elementSiblingIndex()+1;
		}

		
		protected String getPseudoClass() {
			return "nth-child";
		}
    }
    
    /**
     * css pseudo class :nth-last-child)
     * 
     * @see IndexEquals
     */
    public static class IsNthLastChild extends CssNthEvaluator {
    	public IsNthLastChild(int a, int b) {
    		super(a,b);
    	}

        @Override
        protected int calculatePosition(Element root, Element element) {
        	return element.parent().children().size() - element.elementSiblingIndex();
        }
        
		@Override
		protected String getPseudoClass() {
			return "nth-last-child";
		}
    }
    
    /**
     * css pseudo class nth-of-type
     * 
     */
    public static class IsNthOfType extends CssNthEvaluator {
    	public IsNthOfType(int a, int b) {
    		super(a,b);
    	}

		protected int calculatePosition(Element root, Element element) {
			int pos = 0;
        	Elements family = element.parent().children();
        	for (int i = 0; i < family.size(); i++) {
        		if (family.get(i).tag().equals(element.tag())) pos++;
        		if (family.get(i) == element) break;
        	}
			return pos;
		}

		@Override
		protected String getPseudoClass() {
			return "nth-of-type";
		}
    }
    
    public static class IsNthLastOfType extends CssNthEvaluator {

		public IsNthLastOfType(int a, int b) {
			super(a, b);
		}
		
		@Override
		protected int calculatePosition(Element root, Element element) {
			int pos = 0;
        	Elements family = element.parent().children();
        	for (int i = element.elementSiblingIndex(); i < family.size(); i++) {
        		if (family.get(i).tag().equals(element.tag())) pos++;
        	}
			return pos;
		}

		@Override
		protected String getPseudoClass() {
			return "nth-last-of-type";
		}
    }

    /**
     * Evaluator for matching the first sibling (css :first-child)
     */
    public static class IsFirstChild extends Evaluator implements EvalutorInterface {
    	@Override
    	public boolean matches(Element root, Element element) {
    		final Element p = element.parent();
    		return p != null && !(p instanceof Document) && element.elementSiblingIndex() == 0;
    	}
    	
    	@Override
    	public String toString() {
    		return ":first-child";
    	}
    }
    
    /**
     * css3 pseudo-class :root
     * @see <a href="http://www.w3.org/TR/selectors/#root-pseudo">:root selector</a>
     *
     */
    public static class IsRoot extends Evaluator implements EvalutorInterface {
    	@Override
    	public boolean matches(Element root, Element element) {
    		final Element r = root instanceof Document?root.child(0):root;
    		return element == r;
    	}
    	@Override
    	public String toString() {
    		return ":root";
    	}
    }

    public static class IsOnlyChild extends Evaluator implements EvalutorInterface {
		@Override
		public boolean matches(Element root, Element element) {
			final Element p = element.parent();
			return p!=null && !(p instanceof Document) && element.siblingElements().size() == 0;
		}
    	@Override
    	public String toString() {
    		return ":only-child";
    	}
    }

    public static class IsOnlyOfType extends Evaluator implements EvalutorInterface {
		@Override
		public boolean matches(Element root, Element element) {
			final Element p = element.parent();
			if (p==null || p instanceof Document) return false;
			
			int pos = 0;
        	Elements family = p.children();
        	for (int i = 0; i < family.size(); i++) {
        		if (family.get(i).tag().equals(element.tag())) pos++;
        	}
        	return pos == 1;
		}
    	@Override
    	public String toString() {
    		return ":only-of-type";
    	}
    }

    public static class IsEmpty extends Evaluator implements EvalutorInterface {
		@Override
		public boolean matches(Element root, Element element) {
        	List<Node> family = element.childNodes();
        	for (int i = 0; i < family.size(); i++) {
        		Node n = family.get(i);
        		if (!(n instanceof Comment || n instanceof XmlDeclaration || n instanceof DocumentType)) return false; 
        	}
        	return true;
		}
    	@Override
    	public String toString() {
    		return ":empty";
    	}
    }

    /**
     * Abstract evaluator for sibling index matching
     *
     * @author ant
     */
    public abstract static class IndexEvaluator extends Evaluator implements EvalutorInterface {
        int index;

        public IndexEvaluator(int index) {
            this.index = index;
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) text
     */
    public static class ContainsText extends Evaluator implements EvalutorInterface {
        private String searchText;

        public ContainsText(String searchText) {
            this.searchText = searchText.toLowerCase();
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (element.text().toLowerCase().contains(searchText));
        }

        @Override
        public String toString() {
            return String.format(":contains(%s", searchText);
        }
    }

    /**
     * Evaluator for matching Element's own text
     */
    public static class ContainsOwnText extends Evaluator implements EvalutorInterface {
        private String searchText;

        public ContainsOwnText(String searchText) {
            this.searchText = searchText.toLowerCase();
        }

        @Override
        public boolean matches(Element root, Element element) {
            return (element.ownText().toLowerCase().contains(searchText));
        }

        @Override
        public String toString() {
            return String.format(":containsOwn(%s", searchText);
        }
    }

    /**
     * Evaluator for matching Element (and its descendants) text with regex
     */
    public static class Match extends Evaluator implements EvalutorInterface {
        private Pattern pattern;

        public Match(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean matches(Element root, Element element) {
            Matcher m = pattern.matcher(element.text());
            return m.find();
        }

        @Override
        public String toString() {
            return String.format(":matches(%s", pattern);
        }
    }

    /**
     * Evaluator for matching Element's own text with regex
     */
    public static class MatchesOwn extends Evaluator implements EvalutorInterface {
        private Pattern pattern;

        public MatchesOwn(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean matches(Element root, Element element) {
            Matcher m = pattern.matcher(element.ownText());
            return m.find();
        }

        @Override
        public String toString() {
            return String.format(":matchesOwn(%s", pattern);
        }
    }


}

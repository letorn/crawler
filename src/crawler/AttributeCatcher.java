package crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Matcher;

public class AttributeCatcher {

	private static final String textRegex = "^[\\w\\W]*\\s*\\+\\s*text$";
	private static final String textReplaceRegex = "\\s*\\+\\s*text$";
	private static final String subtextRegex = "^[\\w\\W]*\\s*\\>\\s*text$";
	private static final String subtextReplaceRegex = "\\s*\\>\\s*text$";
	private static final String htmlRegex = "^[\\w\\W]*#html$";
	private static final String htmlReplaceRegex = "#html$";
	private static final String ptextRegex = "^[\\w\\W]*#ptext$";
	private static final String ptextReplaceRegex = "#ptext$";
	private static final String attrRegex = "^[\\w\\W]*#attr\\([\\w\\W]*\\)$";
	private static final String attrReplaceRegex = "#attr\\([\\w\\W]*\\)$";
	private static final String attrNameReplaceRegex = "(^[\\w\\W]*#attr\\()|(\\)$)";

	private static HtmlToPlainText plainTextFormatter = new HtmlToPlainText();

	private Map<String, Matcher> matchers = new HashMap<String, Matcher>();
	private Map<String, Matcher> textMatchers = new HashMap<String, Matcher>();
	private Map<String, Matcher> subtextMatchers = new HashMap<String, Matcher>();
	private Map<String, Matcher> htmlMatchers = new HashMap<String, Matcher>();
	private Map<String, Matcher> ptextMatchers = new HashMap<String, Matcher>();
	private Map<String, Object[]> attrMatchers = new HashMap<String, Object[]>();

	public AttributeCatcher(Document document, Map<String, String> attribute) {
		for (Entry<String, String> entry : attribute.entrySet()) {
			String selector = entry.getValue();
			if (selector.matches(textRegex)) {
				textMatchers.put(entry.getKey(), new Matcher(document, selector.replaceAll(textReplaceRegex, "")));
			} else if (selector.matches(subtextRegex)) {
				subtextMatchers.put(entry.getKey(), new Matcher(document, selector.replaceAll(subtextReplaceRegex, "")));
			} else if (selector.matches(htmlRegex)) {
				htmlMatchers.put(entry.getKey(), new Matcher(document, selector.replaceAll(htmlReplaceRegex, "")));
			} else if (selector.matches(ptextRegex)) {
				ptextMatchers.put(entry.getKey(), new Matcher(document, selector.replaceAll(ptextReplaceRegex, "")));
			} else if (selector.matches(attrRegex)) {
				attrMatchers.put(entry.getKey(), new Object[] { new Matcher(document, selector.replaceAll(attrReplaceRegex, "")), selector.replaceAll(attrNameReplaceRegex, "") });
			} else {
				matchers.put(entry.getKey(), new Matcher(document, selector));
			}
		}

	}

	public Map<String, String> attempt(Element element) {
		Map<String, String> attributes = new HashMap<String, String>();
		for (Entry<String, Matcher> entry : matchers.entrySet()) {
			if (entry.getValue().test(element)) {
				attributes.put(entry.getKey(), decode(element.text()));
			}
		}

		for (Entry<String, Matcher> entry : textMatchers.entrySet()) {
			if (entry.getValue().test(element)) {
				Node textNode = element.nextSibling();
				if (null != textNode) {
					attributes.put(entry.getKey(), decode(textNode.outerHtml()));
				}
			}
		}

		for (Entry<String, Matcher> entry : subtextMatchers.entrySet()) {
			if (entry.getValue().test(element)) {
				TextNode textNode = element.textNodes().get(0);
				if (null != textNode) {
					attributes.put(entry.getKey(), decode(textNode.outerHtml()));
				}
			}
		}

		for (Entry<String, Matcher> entry : htmlMatchers.entrySet()) {
			if (entry.getValue().test(element)) {
				attributes.put(entry.getKey(), element.html());
			}
		}

		for (Entry<String, Matcher> entry : ptextMatchers.entrySet()) {
			if (entry.getValue().test(element)) {
				attributes.put(entry.getKey(), plainTextFormatter.getPlainText(element));
			}
		}

		for (Entry<String, Object[]> entry : attrMatchers.entrySet()) {
			Object[] objects = entry.getValue();
			Matcher matcher = (Matcher) objects[0];
			String attr = (String) objects[1];
			if (matcher.test(element)) {
				attributes.put(entry.getKey(), element.attr(attr));
			}
		}
		return attributes;
	}

	private static String decode(String value) {
		return value.replaceAll("(&nbsp;)+", " ").trim();
	}

}

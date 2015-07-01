package org.jsoup.select;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Matcher {

	private Document document;
	private Evaluator evaluator;

	public Matcher(Document document, String selector) {
		this.document = document;
		this.evaluator = QueryParser.parse(selector);
	}

	public boolean test(Element element) {
		return evaluator.matches(document, element);
	}
}

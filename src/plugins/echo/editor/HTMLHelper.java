package plugins.echo.editor;

import plugins.echo.i18n.I18n;

import nu.xom.Node;
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Text;

public class HTMLHelper {

/**
*
*	Need to be rewrited !!!
*
*/

	public static Element element(Element parent, String name, Node content) {
		
		Element e = new Element(name);

		if(content != null)
			e.appendChild(content);
		
		if(parent != null)
			parent.appendChild(e);
		
		return e;
	}

	public static Element element(Element parent, String name, String content) {
		
		return element(parent, name, new Text(content));

	}

	public static Element i18nElement(Element parent, String name, String key) {
		
		return element(parent, name, I18n.getString(key));

	}

	public static Element link(Element parent, String href, String text) {

		Element link = new Element("a");
		link.addAttribute(new Attribute("href", href));
		link.appendChild(text);

		if(parent != null)
			parent.appendChild(link);

		return link;

	}

	public static Element link(String href, String text) {
	
		return link(null, href, text);

	}

	public static Element i18nLink(Element parent, String href, String key) {

		return link(parent, href, I18n.getString(key));

	}

	public static Element i18nLink(String href, String key) {

		return link(null, href, I18n.getString(key));

	}

	public static Element input(Element parent, String type, String name) {

		Element input = new Element("input");
		input.addAttribute(new Attribute("type", type));
		input.addAttribute(new Attribute("name", name));
		input.addAttribute(new Attribute("id", name));

		if(parent != null)
			parent.appendChild(input);

		return input;
	}

	public static Element option(Element parent, String value) {
		
		Element option = element(parent, "option", value);
		option.addAttribute(new Attribute("value",value));

		return option;

	}

	public static Element option(Element parent, String value, boolean selected) {
		
		Element option = option(parent, value);
		if(selected)
			option.addAttribute(new Attribute("selected", "selected"));

		return option;

	}

	public static Element form(String action, String password) {

		Element form = new Element("form");
		form.addAttribute(new Attribute("method", "POST"));
		form.addAttribute(new Attribute("action", action));
		
		Element pswInput = input(form, "hidden", "formPassword");
		pswInput.addAttribute(new Attribute("value", password));

		return form;

	}

	public static Element label(Element parent, String forInput, String text) {

		Element label = new Element("label");
		label.addAttribute(new Attribute("for", forInput));
		label.appendChild(text);

		if(parent != null)
			parent.appendChild(label);

		return label;
	}

	public static Element i18nLabel(Element parent, String forInput, String key) {
		
		return label(parent, forInput, I18n.getString(key));
		
	}

} 

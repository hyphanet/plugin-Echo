package plugins.echo.editor;

import freenet.support.api.HTTPRequest;

import java.util.List;
import java.util.Vector;

import nu.xom.Node;
import nu.xom.Element;
import nu.xom.Attribute;

/**
*	This abstract class represents an editor page
*/
public abstract class Page {

	private Element content;
	private String title;
	private List<String> errors;

	/**
	*	Class constructor
	*/
	protected Page() {

		content = new Element("content");
		errors = new Vector<String>();

	}

	/**
	*	Class constructor specifying page title
	*/
	protected Page(String title) {

		this();
		setTitle(title);

	}

	// REDFLAG: ensure that only safe operations are allowed if !isPost
	public abstract void handleHTTPRequest(HTTPRequest request, boolean isPost);

	/**
	*	Appends XML content to this page
	*	@param n the XML content node to append
	*/
	protected void appendContent(Node n) {

		content.appendChild(n);

	}

	/**
	*	Appends text content to this page
	*	@param text the content to append
	*/
	protected void appendContent(String text) {

		content.appendChild(text);

	}

	/**
	*	Appends an error to this page
	*	@param desc the description of the errror
	*/
	protected void appendError(String desc) {
		
		errors.add(desc);

	}
	
	/**
	*	Appends an error to this page
	*	@param t the throwable object to append
	*/
	protected void appendError(Throwable t){

		errors.add(t.toString());

	}

	/**
	*	Returns the number of errors
	*	@return the number of errors
	*/
	public int countErrors() {

		return errors.size();

	}

	/**
	*	Set the title of this page
	*	@param title the new title
	*/
	protected void setTitle(String title) {

		this.title = title;

	}
	
	/**
	*	Removes the content of this page and clears the errors
	*/
	protected void clear() {

		content.removeChildren();
		errors.clear();

	}

	/**
	*	Generates an XML representation of this page
	*	@return an XML representation of this page
	*/
	public Element toXML() {

		content.detach();

		Element page = new Element("page");
		page.addAttribute(new Attribute("title", title));
		page.appendChild(content);
		
		if(errors.size() != 0) {
			Element errorsElement = new Element("errors");
			for(String desc : errors) {
				Element error = new Element("error");
				error.appendChild(desc);
				errorsElement.appendChild(error);
			}

			page.appendChild(errorsElement);
		}

		return page;
	}

} 

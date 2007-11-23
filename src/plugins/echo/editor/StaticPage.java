package plugins.echo.editor;

import plugins.echo.Echo;
import plugins.echo.i18n.I18n;
import freenet.support.api.HTTPRequest;

import nu.xom.Builder;
import nu.xom.Document;

/**
*	A static editor page
*/
public class StaticPage extends Page {

	protected StaticPage() {
		
		super();

	}

	/**
	*	Creates a new static page from a content file
	*	@param title the title of this page
	*	@param fileName the content file to load 
	*	@return an instance of StaticPage
	*/
	public static StaticPage createFromContentFile(String title, String fileName) {

		StaticPage page = new StaticPage();
		page.setTitle(title);
		
		try {
			Builder parser = new Builder();
			Document doc = parser.build(Echo.class.getClassLoader().getResourceAsStream("/xml/" + fileName));

			I18n.translateXML(doc);
			page.appendContent(doc.getRootElement().copy());

		} catch (Exception e) {
			page.appendError(e);
		}

		return page;

	}

	public void handleHTTPRequest(HTTPRequest request) {	}

}
 

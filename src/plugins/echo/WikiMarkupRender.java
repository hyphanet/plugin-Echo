package plugins.echo;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import nu.xom.Nodes;
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Text;

public class WikiMarkupRender {

/**
	This class provides a simple wiki markup render.
	Atm the code is very crapy, this class need to be fully rewrited  !!

Formatting tricks :
Heading
	== level 1 ==
	=== level 2 ===
	==== level 3 ====
	===== level 4 =====
			
Links
	http://www.example.org				external link
	[http://www.example.org | Description] 		external link with description
	SSK@4yx...file 					link to a freenet URI
	[SSK@4yx...file | Description]			link to a freenet URI with description
	[[Nodeid]]					link to a node
	[[Nodeid | Different title]]			link to a node with a different title

*/

	private final static int flags = Pattern.DOTALL | Pattern.MULTILINE;
	private final Pattern headingPattern = Pattern.compile("[^=]*(={2,5})\\s*(.+?)\\s*\\1", flags);
	private final Pattern nodeLinkPattern = Pattern.compile("[^\\[]*(\\[{2}\\s*([0-9]{4})\\s*(\\|\\s*(\\S+)\\s*)?\\]{2})", flags);
	private final Pattern freenetLinkPattern = Pattern.compile("[^\\[US]*(\\[?((?:U|S)SK@[\\w-~,]{69,100}/[\\S&&[^\\]]]+) (?:\\|([^\\]]+)\\])?)", flags);


	
	/*
		tests


	public static void main(String[] args) {

		WikiMarkupRender test = new WikiMarkupRender();

		String txt = 	"";
		txt += 		"===   Title ===\n\n";
 		txt +=		"\n[[ 0002  ]]\n";
		txt += 		"====Bleh====\n";
// 		txt +=		"\nSSK@GB3wuHmtxN2wLc7g4y1ZVydkK6sOT-DuOsUo-eHK35w,c63EzO7uBEN0piUbHPkMcJYW7i7cOvG42CM3YDduXDs,AQABAAE/testinserts-3/bleh.html";
// 		txt +=		" http://iug65fg.bleh.com/fyfuyf.html dff dftgg";
// 		txt += "\n igi  SSK@4yxtkac-scho~6w-unv6pl-uxibfugngrzo3bjh0ck,4n48yl8e4rh9uppv26ev1zgrrrgegotgw1voka6lk4g,aqacaae/frost-announce|2007.7.19-0.xml fef";
// 		txt +=		"\nabc";
		
		Element xml = new Element("xml");
		xml.appendChild(txt);
		
// 		Matcher matcher = test.externalLinkPattern.matcher(txt);
// 		if (matcher.lookingAt()) {
// 			System.out.println("count : " +  matcher.groupCount());
// 			for(int i=0; i <= matcher.groupCount(); i++) {
// 				System.out.println(i + "[" + matcher.start(i) + ";" + matcher.end(i) +"]");
// 				System.out.println("	'" + matcher.group(i) + "'\n");
// 			}
// 		}

		long start = System.nanoTime();
		test.render(xml);
		System.out.println("\nTime : " + ((System.nanoTime()-start)/1000) + " µs");
	}
*/
	
	public void render(Element xml) {

		nu.xom.Nodes result;
		for(int i=0; i < xml.getChildCount(); i++) {
			if(xml.getChild(i) instanceof Text) {
				result = render((xml.getChild(i).getValue()));
				xml.removeChild(i);
				for(int j=result.size()-1; j >= 0 ; j--)
					xml.insertChild(result.get(j), i);
			}
		}
		
	}

	private nu.xom.Nodes render(String text) {

		if(text.equals(""))
			return null;
		
		nu.xom.Nodes xml = new Nodes();
		
		Matcher matcher = headingPattern.matcher(text);
		if (matcher.lookingAt() && matcher.groupCount() == 2) {
			int level = matcher.group(1).length() -1;

			append(render(text.substring(0, matcher.start(1))), xml);
			
			Element heading = new Element("h3");
			heading.addAttribute(new Attribute("class", "heading-" + level));
			heading.appendChild(matcher.group(2));
			xml.append(heading);
			
			append(render(text.substring(matcher.end(2) + level +2, text.length())), xml);

			return xml;
		}

		matcher = nodeLinkPattern.matcher(text);
		if (matcher.lookingAt() && matcher.groupCount() == 4) {

			append(render(text.substring(0, matcher.start(1))), xml);

			String txt = matcher.group(4);
			if(txt == null)
				txt = matcher.group(2);
			
			xml.append(link(matcher.group(2) + ".html", "nodeLink", txt));

			append(render(text.substring(matcher.end(1), text.length())), xml);

			return xml;
		}

		matcher = freenetLinkPattern.matcher(text);
		if (matcher.lookingAt() && matcher.groupCount() == 3) {

			append(render(text.substring(0, matcher.start(1))), xml);

			String txt = matcher.group(3);
			if(txt == null)
				txt = matcher.group(2);
				
			xml.append(link("/" + matcher.group(2), "freenetLink", txt));

			append(render(text.substring(matcher.end(1), text.length())), xml);
			
			return xml;
		}
		

		return new Nodes(new Text(text));

	}

	private Element link(String href, String cssClass, String text) {

		Element link = new Element("a");
		link.addAttribute(new Attribute("href", href));
		link.addAttribute(new Attribute("class", cssClass));
		link.appendChild(text);

		return link;
	}

	private void append(nu.xom.Nodes nodes, nu.xom.Nodes container) {

		if(nodes == null)	//
			return;		//	FIXME : Suxxxx ??
			
		for(int i=0; i< nodes.size(); i++)
			container.append(nodes.get(i));
		
	}
	

}

<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="1.0">
<!--
	TODO : 	* encoding
		* links
		* other elements
-->
<xsl:output
	method="xml"
	encoding="ISO-8859-1"
	media-type="text/xml"
	indent="yes" />

	<xsl:template match="/index" >
		<rss version="2.0">
   			<channel>
   				<title>My blog</title>
				<link>./</link>
   				<description>Just a blog</description>
   				<generator>Echo 0.1-alpha</generator>
   			</channel>
   			
			<xsl:for-each select="node">
   			<item>
   				<title><xsl:value-of select="title/text()" /></title>
   				<link><xsl:value-of select="@id" />.html</link>
   				<description><xsl:copy-of select="content" /></description>
   			</item>
			</xsl:for-each>
		</rss>
	</xsl:template>	

</xsl:stylesheet>
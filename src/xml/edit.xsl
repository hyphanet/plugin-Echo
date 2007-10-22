<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="1.0">
        
<xsl:output
	method="xml"
	encoding="UTF-8"
	media-type="text/html"
	omit-xml-declaration="yes"
	doctype-public= "-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	indent="yes" />

	<xsl:template match="/page">
		<html>
			<head>
			<link rel="stylesheet" href="./edit.css" type="text/css" media="screen" />
				<title>Echo - Editor</title>
			</head>
			<body>
				<div id="container">
					<div id="header">
						<img src="./echo-logo-small-0.1.png" />
					</div>
					
					<div id="left">
						<ul id="menu">
							<li><a href="write"><i18n key="echo.action.write" /></a></li>
							<li><a href="manage"><i18n key="echo.action.manage" /></a></li>
							<li><a href="publish"><i18n key="echo.action.publish" /></a></li>
<!-- 							<li><a href="configure"><i18n key="echo.action.configure" /></a></li> -->
						</ul>
					</div>

					<div id="main">
						<h1><xsl:value-of select="@title" /></h1>
						<xsl:apply-templates select="errors" />
						<xsl:apply-templates select="content" />	
					</div>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="content">
		<xsl:copy-of select="./node()" />
	</xsl:template> 

	<xsl:template match="errors">
		<div class="edit-error">
<!-- 			<h3 class="edit-error">Error :</h3> -->
		<xsl:choose>
			<xsl:when test="1 = 1">
				<ul class="edit-error">
				<xsl:for-each select="error">
					<li><xsl:value-of select="text()" /></li>
				</xsl:for-each>
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error/text()" />
			</xsl:otherwise>
		</xsl:choose>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
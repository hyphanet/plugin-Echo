<?xml version="1.0" encoding="UTF-8" ?>
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
	
	<xsl:param name="basedir" />
	<xsl:param name="project-title" />
	<xsl:variable name="nodesdir"><xsl:value-of select="$basedir" />/nodes/</xsl:variable>
	<xsl:variable name="blocksdir"><xsl:value-of select="$basedir" />/blocks/</xsl:variable>
	<xsl:variable name="categoriesFile"><xsl:value-of select="$nodesdir" />/categories.xml</xsl:variable>

	<xsl:template match="/">
		<html>
			<head>
			<link rel="stylesheet" href="style.css" type="text/css" media="screen" />
				<title><xsl:value-of select="$project-title" /> - <xsl:call-template name="page-title" /></title>
			</head>
			<body>
				<div id="container">
					<div id="header">
						<h1 id="blog-title"><a href="index.html"><xsl:value-of select="$project-title" /></a></h1>
					</div>

					<!--<div id="left">
					<xsl:call-template name="blocks">
						<xsl:with-param name="align">left</xsl:with-param>
					</xsl:call-template>
					</div>-->

					<div id="main">
						<h2 id="page-title"><xsl:call-template name="page-title" /></h2>
						<xsl:call-template name="content" />
					</div>

					<div id="right">
					<xsl:call-template name="blocks">
						<xsl:with-param name="position">right</xsl:with-param>
					</xsl:call-template>
					</div>

					<div id="footer">
						Powered by Echo
					</div>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="blocks">
		<xsl:param name="position" />
		
		<xsl:for-each select="/page/blocks/block[@position=$position]">
 			<xsl:sort order="ascending" select ="@weight" />
			<xsl:apply-templates select="document(concat($blocksdir, @id, '.xml'))/block" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="block">
		<xsl:choose>
			<!--<xsl:when test="@type='recent-posts'">
				<h3 class="block">Recent posts</h3>
				<ul class="block,recent-posts">
					<xsl:for-each select="document('nodes.xml')//node">
						<li><a href="{@id}.html"><xsl:value-of select="document(concat(@id, '.xml'))//title/text()" /></a></li>
					</xsl:for-each>
				</ul>
			</xsl:when>
-->
			<xsl:when test="@type='categories'">
				<h3 class="block">Categories</h3>
				<ul class="block categories">
				<xsl:for-each select="document($categoriesFile)//category">
					<li>
						<a href="category-{@id}.html"><xsl:value-of select="text()"/></a>
					</li>
				</xsl:for-each>
				</ul>
			</xsl:when>

			<xsl:when test="@type='blog-roll'">
				<h3 class="block">Blogroll</h3>
				<ul class="block blogroll">
				<xsl:for-each select="blog">
					<li>
						<a>
						<xsl:attribute name="href">
							<xsl:if test="@url">
								<xsl:value-of select="@url" />
							</xsl:if>
							<xsl:if test="@key">
								<xsl:value-of select="concat('/', @key)" />
							</xsl:if>
						</xsl:attribute>
 						<xsl:value-of select="text()" />
						</a>
					</li>
				</xsl:for-each>
				</ul>
			</xsl:when>

			<xsl:otherwise>
<!-- 				<xsl:copy-of select="." /> -->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="content">
		<xsl:choose>
			<xsl:when test="/page/node">
				<xsl:copy-of select="/page/node/content/node()" />
			</xsl:when>
			<xsl:when test="/page/index">
				<xsl:for-each select="/page/index/node">
					<div class="post" id="post-{@id}">
						<h2 class="post-title"><a href="{@id}.html"><xsl:value-of select="title/text()"/></a></h2>
						<span class="date"><xsl:value-of select="created/text()" /></span>
						<div class="entry">
						<xsl:copy-of select="content/node()" />
						</div>
						<xsl:if test="count(categories/category) > 0">
						<div class="meta">
							Posted in <ul class="categories">
							<xsl:for-each select="categories/category">
								<li>
									<a href="category-{@id}.html">
									<xsl:call-template name="categoryName">
 										<xsl:with-param name="id" select="@id" /> 
									</xsl:call-template>
									</a>
								</li>
							</xsl:for-each>
							</ul>
						</div>
						</xsl:if>
						
					</div>
				</xsl:for-each>
			</xsl:when>
		</xsl:choose>
<!-- 		<xsl:copy-of select="/" /> -->
	</xsl:template>

	<xsl:template name="page-title">
		<xsl:choose>
			<xsl:when test="/page/node">
				<xsl:value-of select="/page/node/title/text()" />
			</xsl:when>
			<xsl:when test="/page/index">
				<xsl:choose>
					<xsl:when test="/page/index/@category">
						<xsl:call-template name="categoryName">
 							<xsl:with-param name="id" select="/page/index/@category" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						Last posts
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="categoryName">
		<xsl:param name="id" />
		
		<xsl:value-of select="document($categoriesFile)//category[@id=$id]/text()" />
	</xsl:template>
	
</xsl:stylesheet>

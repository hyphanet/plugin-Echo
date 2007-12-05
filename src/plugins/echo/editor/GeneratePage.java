package plugins.echo.editor;

import plugins.echo.Project;
import plugins.echo.ProjectManager;
import plugins.echo.SiteGenerator;

import freenet.support.api.HTTPRequest;

public class GeneratePage extends Page {

	private ProjectManager projectManager;
	private Project project;
	private SiteGenerator generator;
	
	public GeneratePage(ProjectManager projectManager){
	
		super("Generate");
		this.projectManager = projectManager;
		
	}

	public void handleHTTPRequest(HTTPRequest request, boolean isPost) {

		clear();
		project = projectManager.getCurrentProject();

		
		try {
			generator = new SiteGenerator(project);
			generator.generate();

		} catch (Exception e) {
			appendError(e);
		}	
					
		if(countErrors() == 0) {
			
			String path = generator.getOutDir().getAbsolutePath();
			
			appendContent("See the result : ");
			appendContent(HTMLHelper.link("file://" + path, path));
		}
	}

}

package plugins.echo;

import freenet.keys.InsertableClientSSK;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import nu.xom.ParsingException;

/**
*	This class provides methods to manage projects
*/
public class ProjectManager {
	private HashMap<String,File> projects;
	private Project currentProject;
	private InsertableClientSSK currentProjectKeys;

	/**
	*	Class constructor specifying the projects base dir and the random source used to generate the projects keys.
	*/
	public ProjectManager() {
		this.projects = new HashMap<String,File>();

		File[] files = Echo.BASE_DIR.listFiles();
		for(File f : files) {
			if(f.isDirectory() && f.getName().matches("[0-9]{" + Echo.PROJECT_ID_LENGTH + "}"))
				projects.put(f.getName(), f);
		}	
		
	}
	/**
	*	Loads a project
	*	@param projectId the id of the project to load
	*	@return an instance of this project or null if the project does not exist
	*	@see #getCurrentProject()
	*/
	public Project loadProject(String projectId) throws FileNotFoundException, ParsingException, IOException {
	
		if(projects.containsKey(projectId)) {
		
			currentProject = new Project(projects.get(projectId));
			return getCurrentProject();
		
		}
		
		return null;
	}

	/**
	*	Gets an instance of the current project
	*	@return an instance of the current project
	*/
	public Project getCurrentProject() {

		return currentProject;

	}

	/**
	*	Creates a new project
	*	@param projectTitle the title of the project to create
	*	@return an instance of this new project
	*/
	public Project newProject(String projectTitle) throws IOException, ParsingException {
	
		String id = "";
		for(int i=1; i < Math.pow(10, Echo.PROJECT_ID_LENGTH); i++) {
			id = String.valueOf(i);
			while(id.length() < Echo.PROJECT_ID_LENGTH)
				id = "0" + id;
			
			if(! projects.containsKey(id))
				break;
		}
		Project project = new Project(Echo.BASE_DIR, projectTitle, id);
		projects.put(id, project.getProjectDir());
		project.getBlockManager().createDefaultBlocks();
			
		return project;
	}

	/**
	*	Removes the project referenced by this id
	*	@param projectId the id of the project to remove
	*/
	public void removeProject(String projectId) {
	
		if(projects.containsKey(projectId)) {
			if(Util.deleteDirectory(projects.get(projectId)))
				projects.remove(projectId);
		
		}		
	}

	/**
	*	Counts the number of projects
	*	@return the number of projects
	*/
	public int countProjects() {
	
		return projects.size();
	
	}

	/**
	*	Returns the ids of all the referenced projects
	*	@return the ids of all the referenced projects
	*/
	public String[] getProjectsIds() {
	
		return projects.keySet().toArray(new String[0]);
	
	
	}
	
	public InsertableClientSSK getProjectKeys() {
		
		return currentProjectKeys;
	}
}
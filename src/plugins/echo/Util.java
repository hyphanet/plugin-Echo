package plugins.echo;

import java.io.File;

/**
*	Provides utility methods that can be used to perform common operations.
*/
public class Util {

	
	/**
	*	Recursively delete a directory
	*	@param path The directory to delete
	*	@return true if and only if the directory is successfully deleted; false otherwise
	*/
	public static boolean deleteDirectory (File path) {

		boolean success = true;
		File[] files = path.listFiles();
		for(File f : files) {
			if(f.isDirectory())
				success &= deleteDirectory(f);
			else
				success &= f.delete();
		}

		return success & path.delete();
		
	}
}
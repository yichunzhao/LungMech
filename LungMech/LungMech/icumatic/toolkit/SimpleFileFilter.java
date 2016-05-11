package icumatic.toolkit;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class SimpleFileFilter extends FileFilter {

	String[] extensions;
	String description;

	
	public SimpleFileFilter(String ext)
	{
		this(new String[] 
		{ext
		},null);
	}
	
	public SimpleFileFilter(String[] exts, String descr)
	{
		//clone and lowercase the extensions
		extensions = new String[exts.length];
		for ( int i = exts.length -1 ;i>=0;i--)
		{
			extensions[i] = exts[i].toLowerCase();
		}
		
		//make sure we have the simplest description
		description = (descr == null ? exts[0] + " files" : descr);
	}
    
    
    public boolean accept(File f) {
		// Accept all directories and script files.
	
        if (f.isDirectory()) {
            return true;
        }

		//it's a regular file, then check its extension
		
        String name = f.getName().toLowerCase();
		
		for( int i = extensions.length-1;i>=0;i--)
		{
			if ( name.endsWith(extensions[i]))
			{
				 return true;
			}
			
		}
        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return description;
    }
}

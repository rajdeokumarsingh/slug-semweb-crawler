package com.ldodds.slug.http.storage;

import java.util.logging.Level;
import java.io.*;

import com.hp.hpl.jena.rdf.model.*;

import com.ldodds.slug.http.Response;
import com.ldodds.slug.vocabulary.CONFIG;

/**
 * Storage component that simply writes out the retrieved content to disk.
 * 
 * The URL path is used to generate a directory tree into which the files 
 * will be saved. A cache directory should be configured in the Scutter 
 * configuration using the slug:cache property. If not present then data 
 * will be saved into a directory called "slug-cache" under the current 
 * users home directory.
 * 
 * As the class does not parse or otherwise process the content, it is useful 
 * for capturing data in the file system for later processing. The class updates 
 * the Scutter memory to add a scutter:localcopy triple to record the path where 
 * the file has been saved.
 *  
 * @author ldodds
 */
public class ResponseStorer extends AbstractResponseStorer
{
    private FileStorer storer;
    
	public ResponseStorer()
	{
		super();
	}
	
	public ResponseStorer(File cache)
	{
		super();	    
		storer = new FileStorer( cache );
		getLogger().log(Level.INFO, "Cache directory:", cache);
	}
	
	void store(Resource representation, Response response) throws Exception
	{
		final Response resp = response;
		storer.store(getMemory(), representation, response.getRequestURL(), 
				new DataSource() {

					public String getData() {
						
						return resp.getContentType().toString();
					}
			
		});
	}
	
    protected boolean doConfig(Resource self) 
    {
    	//if we don't already have a cache, then look for one
    	//in the config.
    	File base = null;
    	if (self.hasProperty(CONFIG.cache))
    	{
    		String directory = self.getProperty(CONFIG.cache).getString();
    		base = new File(directory);
    	}    	
    	else
    	{
    		base = getDefaultCacheDir();
    	}
    	storer = new FileStorer(base);
    	return true;
    }
    
	/**
     * Get the directory into which offline copies of documents will be written
     */
    public static File getDefaultCacheDir()
    {
        File cache = new File(System.getProperty("user.home"), "slug-cache");
		
		if (!cache.exists())
		{
		    cache.mkdir();
		}
		return cache;
    }

    
}
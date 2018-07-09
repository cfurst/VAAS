/**
 * 
 */
package com.seefurst.VAAS.poc;

import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.Oak;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import javax.jcr.version.Version;
import javax.jcr.Workspace;
import javax.jcr.PathNotFoundException;





/**
 * @author furst
 *
 */
public class VAASPoc {

    /**
     * @param args
     * @throws RepositoryException 
     * @throws LoginException 
     */
    public static void main(String[] args) throws LoginException, RepositoryException {
        // TODO Auto-generated method stub
    	Repository repo = new Jcr(new Oak()).createRepository();
    	Session sess = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
    	Node root = sess.getRootNode();
    	root.addNode("1234").addNode("5678").setProperty("content", "The quick brown fox jumped over the lazy dog");
    	sess.save();
    	
    	//retreval
    	Node newContent = root.getNode("1234/5678");
    	System.out.println(newContent.getProperty("content").getString());
    	System.out.println("is stored at path: " + newContent.getPath());
    	
    	//add versioning
    	newContent.addMixin("mix:versionable");
    	sess.save();
    	//set a version
    	System.out.println("getting workspace...");
    	Workspace ws = sess.getWorkspace();
    	System.out.println("getting version mamager....");
    	VersionManager vm = ws.getVersionManager();
    	System.out.println("attempting checkpoint...");
    	vm.checkpoint(newContent.getPath());
    	System.out.println("saving session...");
    	sess.save();
    	//System.out.print("done...");
    	//list versions.
    	System.out.println("retreiving version.");
    	VersionHistory vh = vm.getVersionHistory(newContent.getPath());
    	VersionIterator vi = vh.getAllVersions();
    	
    	while(vi.hasNext()) {
    		Version v = vi.nextVersion();
    		System.out.println("Found Version.." + v.getName() );
    		try {
    			System.out.println("content at version.. " + v.getProperty("content").getString());
    		} catch (PathNotFoundException a) {
    			System.out.println("no content found...");
    			
    		}
    		
    	}
    	
    	//retreive a version
    	

    	
    }

}

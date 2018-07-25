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

import java.util.Arrays;
import java.util.List;
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
    	System.out.println("simple: " + Repository.OPTION_SIMPLE_VERSIONING_SUPPORTED); System.out.println("Full: " + Repository.OPTION_VERSIONING_SUPPORTED);
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
    	//retreive a version
    	System.out.println("retreiving version.");
    	VersionHistory vh = vm.getVersionHistory(newContent.getPath());
    	VersionIterator vi = vh.getAllVersions();
    	
    	while(vi.hasNext()) {
    		Version v = vi.nextVersion();
    		System.out.println("Found Version.." + v.getName() );
    		try {
    			//System.out.println("content at version.. " + v.getProperty("content").getString());
    			Node frozenNode = v.getFrozenNode();
    			System.out.println("content at version.. " + frozenNode.getProperty("content").getString());
    		} catch (PathNotFoundException a) {
    			System.out.println("no content found...");
    			
    		}
    		
    	}
    	Version v = vh.getVersion("1.0");
    	Node frozenNode = v.getFrozenNode();
    	System.out.println("Got version 1.0 : " + frozenNode.getProperty("content").getString() );
    	
    	//change add version and list different content..
    	newContent.setProperty("content", "The Quick blue fox jumped over the lazy dog");
    	sess.save();
    	v = vm.checkpoint(newContent.getPath());
    	System.out.println("got new vesion: " + v.getName());
    	frozenNode = v.getFrozenNode();
    	
    	System.out.println("new version has text: " + frozenNode.getProperty("content").getString());
    	
    	//label
    	vh.addVersionLabel("1.1","QA",false);
    	vh.addVersionLabel("1.1", "BETA", false);
    	String[] labels = vh.getVersionLabels(v);
    	List<String> labelList = Arrays.asList(labels);
    	labelList.stream().forEach((lb) -> System.out.println("Got label: " +  lb));
    	
    	
    	
    	
    	
    	
    	

    	
    }

}

package org.shaolin.bmdp.designtime.page;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.utils.CloseUtil;
import org.shaolin.uimaster.page.UserRequestContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.PageDispatcher;
import org.shaolin.uimaster.page.cache.UIPageObject;
import org.shaolin.uimaster.page.flow.WebflowConstants;

/**
 * @goal gen-html
 * @author Shaolin
 *
 */
public class HtmlGeneratorMojo extends AbstractMojo {
	
	// read-only parameters ---------------------------------------------------
    /**
     * The maven project.
     * 
     * @parameter property="project"
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="${basedir}/src/main/resources/entities/"
     * @readonly
     */
    private File entitiesDirectory;
    
    /**
     * @parameter default-value="${basedir}/src/other/web/html"
     * @readonly
     */
    private File htmlDirectory;
    
    /**
     * @parameter property="pageName"
     * @readonly
     */
    private String pageName;

	
    /**
     * Gets the Maven project.
     * 
     * @return the project
     */
    protected MavenProject getProject() {
        return project;
    }
    
    public void execute() throws MojoExecutionException, MojoFailureException  {
    	if (!entitiesDirectory.exists()) {
    		this.getLog().warn("the entity directory does not exist!");
    		return;
    	}
    	if (!htmlDirectory.exists()) {
    		htmlDirectory.mkdirs();
    	}
    	
    	if (pageName == null || pageName.trim().isEmpty()) {
    		this.getLog().warn("please specify the 'pageName' parameter!");
    		return;
    	}
    	
    	this.getLog().info("entitiesDirectory: " + entitiesDirectory.getAbsolutePath());
    	this.getLog().info("pageName: " + pageName);
    	
    	String entityName = pageName.substring(0, pageName.lastIndexOf('.'));
    	String suffix = pageName.substring(pageName.lastIndexOf('.') + 1);
    	
    	File pagePath = new File(entitiesDirectory, entityName.replace('.', '/') + "." + suffix);
    	if (!pagePath.exists()) {
    		this.getLog().warn("the page entity does not exist in this directory! " + pagePath.getAbsolutePath());
    		return;
    	}
    	
    	MockHttpRequest request = new MockHttpRequest();
		request.setAttribute(WebflowConstants.FRAME_NAME, "frame1");
		request.setAttribute("_framePrefix", "frame1");
		request.setAttribute("_frameTarget", "frame1");
		MockHttpResponse response = new MockHttpResponse();
		
		
        UserRequestContext htmlContext = new UserRequestContext(request, response);
        htmlContext.setCurrentFormInfo(entityName, "", "");
        htmlContext.setIsDataToUI(true);
        
        try {
        	IEntityManager entityManager = IServerServiceManager.INSTANCE.getEntityManager();
        	entityManager.reloadDir(pagePath);
        	
			UIPageObject pageObject = HTMLUtil.parseUIPage(entityName);
			PageDispatcher dispatcher = new PageDispatcher(pageObject);
			dispatcher.forwardPage(htmlContext);
			
			File result = new File(htmlDirectory, entityName + ".html");
			if (!result.exists()) {
				result.createNewFile();
			}
			FileWriter fw = null;
			try {
				String code = response.getHtmlCode();
				fw = new FileWriter(result);
				fw.write(code, 0, code.length());
				fw.flush();
				this.getLog().info("Generated Html file: " + result);
			} catch (IOException e) {
				this.getLog().error(e);
			} finally {
				CloseUtil.close(fw);
			}
		} catch (Exception e) {
			this.getLog().error(e);
		}
	}
	
}

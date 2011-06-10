package org.spiffyui.maven.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.json.JSONObject;

/**
 * Goal which determines various revision details about the build
 * 
 * @goal build-info
 * @phase generate-resources
 */
public class BuildInfoMojo extends AbstractMojo
{
    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject m_project;
    
    /**
     * The character encoding scheme to be applied exporting the JSON document
     *
     * @parameter expression="${encoding}" default-value="UTF-8"
     */
    private String m_encoding;
    
    /**
     * The character encoding scheme to be applied exporting the JSON document
     *
     * @parameter expression="${dateFormat}" default-value="yyyy.MMMMM.dd hh:mm aaa"
     */
    private String m_dateFormat;
    
    /**
     * Location of the file.
     * 
     * @parameter expression="${project.build.outputDirectory}/build-info.json"
     * @required
     */
    private File m_outputFile;
    
    /**
     * The base directory of this project
     * 
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    private File m_basedir;

    @Override
    public void execute()
        throws MojoExecutionException,
            MojoFailureException
    {
        Properties p = m_project.getProperties();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(m_dateFormat);
        String date = sdf.format(cal.getTime());
        
        List<Dependency> depsList = m_project.getDependencies();
        Map<String, Dependency> deps = new HashMap<String, Dependency>();
        for (Dependency dep : depsList) {
            deps.put(dep.getArtifactId(), dep);
        }
        
        try {
            File parent = m_outputFile.getParentFile();
            if (!parent.exists()) {
                FileUtils.forceMkdir(parent);
            }
            
            // Create file
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(m_outputFile), m_encoding));
            
            JSONObject rev = new JSONObject();
            rev.put("number", p.getProperty("revision.number"));
            rev.put("date", p.getProperty("revision.date"));
            
            JSONObject components = new JSONObject();
            components.put("spiffyui", deps.get("spiffyui").getVersion());
            
            JSONObject info = new JSONObject();
            info.put("schema", 1);
            info.put("date", date);
            info.put("user", System.getProperties().get("user.name"));
            info.put("components", components);
            info.put("revision", rev);
            info.put("dir", m_basedir.getAbsolutePath());
 
            out.write(info.toString());
            // Close the output stream
            out.close();
        } catch (Exception e) { // Catch exception if any
            throw new MojoExecutionException(e.getMessage());
        }

    }

}

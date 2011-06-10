package org.spiffyui.maven.plugins;

import static org.apache.maven.artifact.Artifact.SCOPE_COMPILE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.gwt.GwtModule;
import org.codehaus.mojo.gwt.shell.AbstractGwtShellMojo;
import org.codehaus.mojo.gwt.utils.GwtModuleReaderException;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SingleTargetSourceMapping;
import org.codehaus.plexus.util.StringUtils;

/**
 * @extendsPlugin gwt-maven-plugin
 * @goal compile
 * @phase compile
 */
public class SpiffyCompileMojo extends AbstractGwtShellMojo
{
    /**
     * @parameter expression="${gwt.compiler.skip}" default-value="false"
     */
    private boolean m_skip;

    /**
     * Don't try to detect if GWT compilation is up-to-date and can be skipped.
     * <p>
     * Can be set from command line using '-Dgwt.compiler.force=true'.
     * </p>
     * 
     * @parameter expression="${gwt.compiler.force}" default-value="false"
     */
    private boolean m_force;

    /**
     * On GWT 1.6+, number of parallel processes used to compile GWT
     * premutations. Defaults to platform available processors number.
     * <p>
     * Can be unset from command line using '-Dgwt.compiler.localWorkers=n'.
     * </p>
     * 
     * @parameter expression="${gwt.compiler.localWorkers}"
     */
    private int m_localWorkers;

    /**
     * Whether or not to enable assertions in generated scripts (-ea).
     * 
     * @parameter default-value="false"
     */
    private boolean m_enableAssertions;

    /**
     * Ask GWT to create the Story of Your Compile (SOYC)
     * <p>
     * Can be set from command line using '-Dgwt.compiler.soyc=true'.
     * </p>
     * 
     * @parameter expression="${gwt.compiler.soyc}" default-value="false"
     * @deprecated you must use {@link #compileReport} option
     */
    private boolean m_soyc;

    /**
     * Artifacts to be included as source-jars in GWTCompiler Classpath. Removes
     * the restriction that source code must be bundled inside of the final JAR
     * when dealing with external utility libraries not designed exclusivelly
     * for GWT. The plugin will download the source.jar if necessary. This
     * option is a workaround to avoid packaging sources inside the same JAR
     * when splitting and application into modules. A smaller JAR can then be
     * used on server classpath and distributed without sources (that may not be
     * desirable).
     * 
     * @parameter
     */
    private String[] m_compileSourcesArtifacts;

    /**
     * Logs output in a graphical tree view.
     * <p>
     * Can be set from command line using '-Dgwt.treeLogger=true'.
     * </p>
     * 
     * @parameter default-value="false" expression="${gwt.treeLogger}"
     */
    private boolean m_treeLogger;

    /**
     * EXPERIMENTAL: Disables some java.lang.Class methods (e.g. getName()).
     * <p>
     * Can be set from command line using '-Dgwt.disableClassMetadata=true'.
     * </p>
     * 
     * @parameter default-value="false" expression="${gwt.disableClassMetadata}"
     */
    private boolean m_disableClassMetadata;

    /**
     * EXPERIMENTAL: Disables run-time checking of cast operations.
     * <p>
     * Can be set from command line using '-Dgwt.disableCastChecking=true'.
     * </p>
     * 
     * @parameter default-value="false" expression="${gwt.disableCastChecking}"
     */
    private boolean m_disableCastChecking;

    /**
     * Validate all source code, but do not compile.
     * <p>
     * Can be set from command line using '-Dgwt.validateOnly=true'.
     * </p>
     * 
     * @parameter default-value="false" expression="${gwt.validateOnly}"
     */
    private boolean m_validateOnly;

    /**
     * Enable faster, but less-optimized, compilations.
     * <p>
     * Can be set from command line using '-Dgwt.draftCompile=true'.
     * </p>
     * 
     * @parameter default-value="false" expression="${gwt.draftCompile}"
     */
    private boolean m_draftCompile;

    /**
     * The directory into which extra, non-deployed files will be written.
     * 
     * @parameter default-value="${project.build.directory}/extra"
     */
    private File m_extra;

    /**
     * The temp directory is used for temporary compiled files (defaults is
     * system temp directory).
     * 
     * @parameter
     */
    private File m_workDir;

    /**
     * add -extra parameter to the compiler command line
     * <p>
     * Can be set from command line using '-Dgwt.extraParam=true'.
     * </p>
     * 
     * @parameter default-value="false" expression="${gwt.extraParam}"
     * @since 2.1.0-1
     */
    private boolean m_extraParam;

    /**
     * add -compileReport parameter to the compiler command line
     * <p>
     * Can be set from command line using '-Dgwt.compiler.compileReport=true'.
     * </p>
     * 
     * @parameter default-value="false"
     *            expression="${gwt.compiler.compileReport}"
     * @since 2.1.0-1
     */
    private boolean m_compileReport;

    /**
     * add -optimize parameter to the compiler command line the value must be
     * between 0 and 9 by default -1 so no arg to the compiler
     * <p>
     * Can be set from command line using '-Dgwt.compiler.optimizationLevel=n'.
     * </p>
     * 
     * @parameter default-value="-1"
     *            expression="${gwt.compiler.optimizationLevel}"
     * @since 2.1.0-1
     */
    private int m_optimizationLevel;

    /**
     * add -XsoycDetailed parameter to the compiler command line
     * <p>
     * Can be set from command line using '-Dgwt.compiler.soycDetailed=true'.
     * </p>
     * 
     * @parameter default-value="false"
     *            expression="${gwt.compiler.soycDetailed}"
     * @since 2.1.0-1
     */
    private boolean m_soycDetailed;

    /**
     * add -strict parameter to the compiler command line
     * <p>
     * Can be set from command line using '-Dgwt.compiler.strict=true'.
     * </p>
     * 
     * @parameter default-value="false" expression="${gwt.compiler.strict}"
     * @since 2.1.0-1
     */
    private boolean m_strict;

    @Override
    public void doExecute()
        throws MojoExecutionException,
            MojoFailureException
    {
        if (m_skip || "pom".equals(getProject().getPackaging())) {
            getLog().info("GWT compilation is skipped");
            return;
        }

        if (!this.getOutputDirectory().exists()) {
            this.getOutputDirectory().mkdirs();
        }

        String[] modules = this.getModules();

        /* ensure there is only one module, and record it for posterity */
        switch (modules.length) {
            case 0:
                throw new MojoExecutionException("No GWT modules detected");
            case 1:
                try {
                    GwtModule module = readModule(modules[0]);
                    File path = new File(getOutputDirectory(), module.getPath());
                    Properties p = getProject().getProperties();
                    p.setProperty("spiffyui.gwt.module.name", module.getName());
                    p.setProperty("spiffyui.gwt.module.path", path.getAbsolutePath());

                    compile(modules);
                } catch (Exception e) {
                    throw new MojoExecutionException(e.getMessage());
                }
                break;
            default:
                throw new MojoExecutionException("Only one GWT module allowed, but " + modules.length + " detected: " + modules);
        }
    }

    private void compile(String[] modules)
        throws MojoExecutionException
    {
        boolean upToDate = true;

        try {
            JavaCommand cmd = new JavaCommand("com.google.gwt.dev.Compiler");
            if (gwtSdkFirstInClasspath) {
                cmd.withinClasspath(getGwtUserJar()).withinClasspath(getGwtDevJar());
            }
            cmd.withinScope(Artifact.SCOPE_COMPILE);

            if (!gwtSdkFirstInClasspath) {
                cmd.withinClasspath(getGwtUserJar()).withinClasspath(getGwtDevJar());
            }

            Properties p = getProject().getProperties();
            File htmlprops = new File(p.getProperty("spiffyui.htmlprops.path"));
            cmd.withinClasspath(htmlprops);

            cmd.arg("-gen", getGen().getAbsolutePath()).arg("-logLevel", getLogLevel()).arg("-style", getStyle())
                .arg("-war", getOutputDirectory().getAbsolutePath())
                .arg("-localWorkers", String.valueOf(getLocalWorkers()))
                // optional advanced arguments
                .arg(m_enableAssertions, "-ea").arg(m_draftCompile, "-draftCompile").arg(m_validateOnly, "-validateOnly").arg(m_treeLogger, "-treeLogger")
                .arg(m_disableClassMetadata, "-XdisableClassMetadata").arg(m_disableCastChecking, "-XdisableCastChecking").arg(m_strict, "-strict")
                .arg(m_soycDetailed, "-XsoycDetailed");

            if (m_optimizationLevel >= 0) {
                cmd.arg("-optimize").arg(Integer.toString(m_optimizationLevel));
            }

            if (m_extraParam || m_compileReport || m_soyc) {
                getLog().debug("create extra directory ");
                if (!m_extra.exists()) {
                    m_extra.mkdirs();
                }
                cmd.arg("-extra").arg(m_extra.getAbsolutePath());
            } else {
                getLog().debug("NOT create extra directory ");
            }

            if (m_compileReport) {
                cmd.arg("-compileReport");
            }

            addCompileSourceArtifacts(cmd);

            if (m_workDir != null) {
                cmd.arg("-workDir").arg(String.valueOf(m_workDir));
            }

            addSOYC(cmd);

            for (String target : modules) {
                if (!compilationRequired(target, getOutputDirectory())) {
                    continue;
                }
                cmd.arg(target);
                upToDate = false;
            }
            if (!upToDate) {
                cmd.execute();
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Add sources.jar artifacts for project dependencies listed as
     * compileSourcesArtifacts. This is a GWT hack to avoid packaging java
     * source files into JAR when sharing code between server and client.
     * Typically, some domain model classes or business rules may be packaged as
     * a separate Maven module. With GWT packaging this requires to distribute
     * such classes with code, that may not be desirable.
     * <p>
     * The hack can also be used to include utility code from external
     * librariries that may not have been designed for GWT.
     */
    private void addCompileSourceArtifacts(JavaCommand cmd)
        throws MojoExecutionException
    {
        if (m_compileSourcesArtifacts == null) {
            return;
        }
        for (String include : m_compileSourcesArtifacts) {
            List<String> parts = new ArrayList<String>();
            parts.addAll(Arrays.asList(include.split(":")));
            if (parts.size() == 2) {
                // type is optional as it will mostly be "jar"
                parts.add("jar");
            }
            String dependencyId = StringUtils.join(parts.iterator(), ":");
            boolean found = false;

            for (Artifact artifact : getProjectArtifacts()) {
                getLog().debug("compare " + dependencyId + " with " + artifact.getDependencyConflictId());
                if (artifact.getDependencyConflictId().equals(dependencyId)) {
                    getLog().debug("Add " + dependencyId + " sources.jar artifact to compile classpath");
                    Artifact sources = resolve(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), "jar", "sources");
                    cmd.withinClasspath(sources.getFile());
                    found = true;
                    break;
                }
            }
            if (!found) {
                getLog().warn("Declared compileSourcesArtifact was not found in project dependencies " + dependencyId);
            }
        }
    }

    private void addSOYC(JavaCommand cmd)
    {
        if (m_soyc) {
            getLog().debug("SOYC has been enabled by user, SOYC is deprecated : you must now use compileReport");
            cmd.arg("-soyc");
        } else {
            getLog().debug("SOYC disabled");
        }
    }

    private int getLocalWorkers()
    {
        if (m_localWorkers > 0) {
            return m_localWorkers;
        }
        // workaround to GWT issue 4031 whith IBM JDK
        // @see
        // http://code.google.com/p/google-web-toolkit/issues/detail?id=4031
        if (System.getProperty("java.vendor").startsWith("IBM")) {
            StringBuilder sb = new StringBuilder("Build is using IBM JDK, localWorkers set to 1 as a workaround");
            sb.append(SystemUtils.LINE_SEPARATOR);
            sb.append("see http://code.google.com/p/google-web-toolkit/issues/detail?id=4031");
            getLog().info(sb.toString());
            return 1;
        }
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Try to find out, if there are stale sources. If aren't some, we don't
     * have to compile... ...this heuristic doesn't take into account, that
     * there could be updated dependencies. But for this case, as 'clean
     * compile' could be executed which would force a compilation.
     * 
     * @param module
     *        Name of the GWT module to compile
     * @param output
     *        Output path
     * @return true if compilation is required (i.e. stale sources are found)
     * @throws MojoExecutionException
     *         When sources scanning fails
     * @author Alexander Gordt
     */
    private boolean compilationRequired(String module, File output)
        throws MojoExecutionException
    {
        try {
            GwtModule gwtModule = readModule(module);
            if (gwtModule.getEntryPoints().size() == 0) {
                getLog().debug(gwtModule.getName() + " has no EntryPoint - compilation skipped");
                // No entry-point, this is an utility module : compiling this
                // one will fail
                // with '[ERROR] Module has no entry points defined'
                return false;
            }

            if (m_force) {
                return true;
            }

            String modulePath = gwtModule.getPath();
            String outputTarget = modulePath + "/" + modulePath + ".nocache.js";

            // Require compilation if no js file present in target.
            if (!new File(output, outputTarget).exists()) {
                return true;
            }

            // js file allreay exists, but may not be up-to-date with project
            // source files
            SingleTargetSourceMapping singleTargetMapping = new SingleTargetSourceMapping(".java", outputTarget);
            StaleSourceScanner scanner = new StaleSourceScanner();
            scanner.addSourceMapping(singleTargetMapping);

            SingleTargetSourceMapping gwtModuleMapping = new SingleTargetSourceMapping(".gwt.xml", outputTarget);
            scanner.addSourceMapping(gwtModuleMapping);

            Collection<File> compileSourceRoots = new HashSet<File>();
            classpathBuilder.addSourcesWithActiveProjects(getProject(), compileSourceRoots, SCOPE_COMPILE);
            classpathBuilder.addResourcesWithActiveProjects(getProject(), compileSourceRoots, SCOPE_COMPILE);
            for (File sourceRoot : compileSourceRoots) {
                if (!sourceRoot.isDirectory()) {
                    continue;
                }
                try {
                    if (!scanner.getIncludedSources(sourceRoot, output).isEmpty()) {
                        getLog().debug("found stale source in " + sourceRoot + " compared with " + output);
                        return true;
                    }
                } catch (InclusionScanException e) {
                    throw new MojoExecutionException("Error scanning source root: \'" + sourceRoot + "\' " + "for stale files to recompile.", e);
                }
            }
            getLog().info(module + " is up to date. GWT compilation skipped");
            return false;
        } catch (GwtModuleReaderException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}

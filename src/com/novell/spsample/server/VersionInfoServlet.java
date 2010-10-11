/*
 * Copyright (c) 2010 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */
package com.novell.spsample.server;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet shows version information.
 */
public class VersionInfoServlet extends HttpServlet
{

    private static final long serialVersionUID = -1l;
    
    private static final ResourceBundle BUILD_BUNDLE = 
        ResourceBundle.getBundle("com/novell/spsample/server/buildnum", Locale.getDefault());
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        
        response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        

        StringBuffer buildinfo = new StringBuffer();
        
        buildinfo.append("{");

        /*
         These values are defined in the buildnum.properties file which is generated by the build
         */
        buildinfo.append("\"version\":\"" + BUILD_BUNDLE.getString("build.version") + "\",");
        buildinfo.append("\"user\":\"" + BUILD_BUNDLE.getString("build.user") + "\",");
        buildinfo.append("\"date\":\"" + BUILD_BUNDLE.getString("build.date") + "\",");
        buildinfo.append("\"rev\":\"" + BUILD_BUNDLE.getString("build.revision") + "\"");
        
        buildinfo.append("}");
        
        out.println(buildinfo.toString());
    }
}

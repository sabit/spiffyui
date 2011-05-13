/*******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.spiffyui.spsample.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;

import org.spiffyui.client.JSUtil;
import org.spiffyui.client.MainHeader;
import org.spiffyui.client.MessageUtil;
import org.spiffyui.client.rest.RESTException;
import org.spiffyui.client.rest.RESTObjectCallBack;
import org.spiffyui.client.rest.RESTility;

/**
 * This is the header for SPSample.
 *
 */
public class SPSampleHeader extends MainHeader
{
    /**
     * Creates a new SPSampleHeader panel
     */
    public SPSampleHeader()
    {
        Anchor logout = new Anchor("Logout", "#");
        logout.getElement().setId("header_logout");
        setLogout(logout);
        if (!Index.userLoggedIn()) {
            JSUtil.hide("#header_logout", "fast");
            setWelcomeString("");            
        } else {
            String token = RESTility.getUserToken();
            setWelcomeString("Welcome " + token.substring(0, token.indexOf('-')));            
        }
        logout.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event)
                {
                    event.preventDefault();
                    doLogout();
                }
            });
    }

    /**
     * Logout of the application
     */
    public static void doLogout()
    {
        RESTility.getAuthProvider().logout(new RESTObjectCallBack<String>()
                        {
            public void success(String message)
            {
                Window.Location.reload();
            }

            public void error(String message)
            {
                Window.Location.reload();
            }

            public void error(RESTException e)
            {
                MessageUtil.showFatalError(e.getReason());
            }
        });
    }
}


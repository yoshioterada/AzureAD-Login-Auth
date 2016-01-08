/*
* Copyright 2015 Yoshio Terada
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.yoshio3.azureRealm;

import com.sun.appserv.security.AppservPasswordLoginModule;
import java.util.Set;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import org.glassfish.security.common.PrincipalImpl;

/**
 *
 * @author Yoshio Terada
 */
public class AzureADLoginModule extends AppservPasswordLoginModule {

    private final static Logger logger = Logger.getLogger(AzureADLoginModule.class.getName());

    @Override
    protected void authenticateUser() throws LoginException {
        AzureADRealm realm = (AzureADRealm)getCurrentRealm();
        String[] groups = realm.authenticate(_username, _passwd);
        
        if (groups == null) {
            throw new LoginException("User does not join any groups");
        }

        Set principals = _subject.getPrincipals();
        principals.add(new PrincipalImpl(_username));
        commitUserAuthentication(groups);
    }

}

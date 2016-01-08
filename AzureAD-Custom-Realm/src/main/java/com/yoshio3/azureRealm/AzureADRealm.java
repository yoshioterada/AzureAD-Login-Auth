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

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.UserInfo;
import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.ServiceUnavailableException;

/**
 *
 * @author Yoshio Terada
 */
public class AzureADRealm extends AppservRealm {

    private final static String AUTHORITY = "https://login.windows.net/common";
    private final static String CLIENT_ID = "********-****-****-****-************";
    
    private final static String AUTH_TPYE = "Authentication done by using Azure Active Directory";
    private final static Logger logger = Logger.getLogger(AzureADRealm.class.getName());

    @Override
    protected void init(Properties props) throws BadRealmException, NoSuchRealmException {
        super.init(props);
        String jaasContext = props.getProperty(JAAS_CONTEXT_PARAM);
        logger.log(Level.INFO, "jaasContext: {0} ", jaasContext);

        String realmName = this.getClass().getSimpleName();
        logger.log(Level.INFO, "{0} started. ", realmName);
        logger.log(Level.INFO, "{0}: {1}", new Object[]{realmName, getAuthType()});
        logger.log(Level.INFO, "{0} authentication uses jar file  located at $domain/lib folder ", realmName);

        setProperty(JAAS_CONTEXT_PARAM, jaasContext);
    }

    @Override
    public String getAuthType() {
        return AUTH_TPYE;
    }

    @Override
    public Enumeration getGroupNames(String username) throws InvalidOperationException, NoSuchUserException {
        return Collections.enumeration(Arrays.asList(findGroups(username)));
    }

    private String[] findGroups(String username) {
        //実際には ADAL 経由では グループは取れなかった。
        //Graph API に切り替える予定
        return new String[]{"foo", "bar", "baz"};
    }

    private boolean validate(String username, char[] password) {
        String pass = String.valueOf(password);

        try {
            AuthenticationResult result = getAccessTokenFromUserCredentials(
                    username, pass);
            logger.log(Level.INFO, "Access Token - {0}", result.getAccessToken());
            logger.log(Level.INFO, "Refresh Token - {0}", result.getRefreshToken());
            logger.log(Level.INFO, "ID Token - {0}", result.getIdToken());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            return false;
        }
        logger.log(Level.INFO, "Username : {0}\tPASSWORD : {1}", new Object[]{username, pass});
        return true;

    }

    String[] authenticate(String username, char[] password) {
        if (validate(username, password) == false) {
            return null;
        }
        return findGroups(username);
    }

    private static AuthenticationResult getAccessTokenFromUserCredentials(
            String username, String password) throws Exception {
        AuthenticationContext context = null;
        AuthenticationResult result = null;
        //開発環境が Web Profile で行っていたため、便宜的に 
        //Java SE の ExecutorService を使用（非推奨）
        //本来は、Java EE 7 の ManagedExecutorService で実装すべき

        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(AUTHORITY, false, service);
            Future<AuthenticationResult> future = context.acquireToken(
                    "https://graph.windows.net", CLIENT_ID, username, password,
                    null);
            result = future.get();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new ServiceUnavailableException(
                    "authentication result was null");
        }
        UserInfo userInfo = result.getUserInfo();
        
        return result;
    }
}

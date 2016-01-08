This is first commit of GlassFish/Payara custom realm for Microsoft Azure
Active Directory.

At first, I had tried to use the ADAL4J in this first commit.
https://github.com/AzureAD/azure-activedirectory-library-for-java

However I noticed that there was few functionality on it. And I
couldn’t get the group information from libraries.
Thus, I could implement the authentication but I couldn’t implement the
authorization.

Thus, I decided to use the Graph API in next version.

<HR>
1. Build and copy the custom realm to GlassFish/Payara domain lib directory like follows.<br>
cp AzureAD-Custom-Realm.jar $GLASSFISH_INSTALL/glassfish-4.1.1/glassfish/domains/domain1/lib

2. GlassFish/Payara Configuration for Login<br>
/Applications/NetBeans/glassfish-4.1.1/glassfish/domains/domain1/config/login.conf<br>
AzureADRealm {<br>
        com.yoshio3.azureRealm.AzureADLoginModule required;<br>
};<br>

3. GlassFish/Payara Configuration for new Realm<br>
In order to use this "AzureAD-Custom-Realm", you need to configure the Security Realm on GlassFish/Payara as follows.
 asadmin create-auth-realm --classname com.yoshio3.azureRealm.AzureADRealm --property="jaas-context=AzureADRealm" AzureADRealm

4. Restart GlassFish/Payara

5. Build and Deploy "AzureAD-Login-WebApp" to GlassFish/Payara

6. Configure the Active Directory on Microsoft Azure<br>
   Note: Please register the application as "Native Client Application"?
   After register it, please get the client ID from Azure Portal and 
   copy & paste to the following code of AzureAD-Custom-Realm project.
   
public class AzureADRealm extends AppservRealm {<br>
    private final static String CLIENT_ID = "********-****-****-****-************";<br>
}<br>

If you create the user on Active Directory on Microsoft Azure, you can login to the GlassFish/Payara
and screen will transfer to the welcomePrimefaces.xhtml.

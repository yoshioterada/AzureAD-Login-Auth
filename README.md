This is first commit of GlassFish custom realm for Microsoft Azure
Active Directory.

At first, I had tried to use the ADAL4J in this first commit.
https://github.com/AzureAD/azure-activedirectory-library-for-java

However I noticed that there was few functionality on it. And I
couldn’t get the group information from libraries.
Thus, I could implement the authentication but I couldn’t implement the
authorization.

Thus, I decided to use the Graph API in next version.

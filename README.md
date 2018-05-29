# ApiFest OAuth 2.0 Server
The ApiFest OAuth 2.0 Server implements OAuth 2.0 server side as per http://tools.ietf.org/html/rfc6749.

## Features
- register new client app
- generate access token using auth code
- generate access token using username and password (grant_type=password)
- generate access token using client credentials (grant_type=client_credentials)
- generate access token using refresh token (grant_type=refresh_token)
- revoke access token
- validate access token
- pluggable storage (currently supports MongoDB, Redis & Hazelcast)
- unlimited horizontal scalability


#Start ApiFest OAuth 2.0 Server

Start the ApiFest OAuth 2.0 Server with the following command:

```
java -Dproperties.file=[apifest_properties_file_path] -Dlog4j.configuration=file:///[log4j_xml_file_path] -jar apifest-oauth20-0.1.2-SNAPSHOT-jar-with-dependencies.jar
```

When the server is started, you'll see:
```
ApiFest OAuth 2.0 Server started at [host]:[port]
```

# ApiFest OAuth 2.0 Server configuration

Here is a template of the apifest-oauth.properties file:
```
oauth20.host=  
oauth20.port=  
oauth20.https=  
oauth20.production.mode=  
oauth20.subnets.whitelist=  
oauth20.keystore.path=  
oauth20.keystore.password=  
oauth20.database=  
mongodb.uri=
redis.master=
redis.sentinels=
redis.password=
hazelcast.cluster.name=  
hazelcast.password=  
hazelcast.cluster.members=  
custom.classes.jar=  
custom.authenticate.class=
custom.grant_type.class=
```

The path to the apifest.properties file should be set using system variable ***-Dproperties.file***  

* **Setup the ApiFest OAuth 2.0 Server host and port**

The ApiFest OAuth 2.0 Server can run on different hosts and ports.
You can define the host and the port in the apifest-oauth.properties file using ***oauth20.host*** and ***oauth20.port***

* **Setup security properties**

Set the server to run in SSL only mode if setting ***oauth20.https*** to true. ***oauth20.keystore.path*** and ***oauth20.keystore.password*** allow you to set the ssl certificate for the server

Set the server to run in production mode with ***oauth20.production.mode***. This will restrict the access to authenticated users (see /oauth20/login endpoint) to the endpoints used for sensitive administration (see endpoint descriptions at the end)

Filter access to restricted endpoints by setting a whitelist ***oauth20.subnets.whitelist*** of authorized subnets (CIDR notation separated by commas) with the following property :

e.g. ```oauth20.subnets.whitelist = 10.0.0.1/24,...,192.168.0.1/16```

* **Setup the type of the DB (Hazelcast, MongoDB or Redis)**
>
>Define the type of the DB to be used (by default MongoDB is used) - valid values are "hazelcast", "mongodb" and "redis" (without quotes) with property ***oauth20.database***
>
> ***MongoDB***
>
>If MongoDB is used, define the mongo URI string with property ***mongodb.uri***
>
>e.g. ```mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]```
>
>Username and password can optionally be set in the connection URI, find more documentation at (https://docs.mongodb.com/manual/reference/connection-string/) .
>
>Unless overridden, the following default values are set for the connection: ```connectTimeoutMS=2000```  
>
> ***Redis***
>
>If Redis is used, define Redis sentinels list(as comma-separated list) in property ***redis.sentinels***
>N.B: Redis code uses the SCAN command which requires 2.8.0+ versions
>
>Define the name of Redis master in property ***redis.master*** and it's password in property ***redis.password***
>
> ***Hazelcast***
>
> If Hazelcast is used, you can use an embedded instance or connect to an external cluster
>
> If defined the property ***hazelcast.cluster.name*** will connect to an external cluster with the given group name
> Set a password using the property ***hazelcast.password*** (otherwise the default Hazelcast password - dev-pass will be used)
> Setup the distributed storage nodes of the cluster using the property ***hazelcast.cluster.members*** (as comma-separated list of IPs)

* **Setup user authentication**

As the ApiFest OAuth 2.0 Server should be able to authenticate the user, you can implement your own user authentication implementing ```com.apifest.oauth20.IUserAuthentication``` interface (```com.apifest.oauth20.security.GuestUserAuthentication``` is the default implementation which always returns a default user).

In addition, ApiFest supports a custom grant_type and you can implement your own handler for it (implement ```com.apifest.oauth20.ICustomGrantTypeHandler``` interface and add the ```com.apifest.oauth20.GrantType``` annotation to provide the grant type name).

Add your classes to the classpath or provide a jar that contains the implementation of these custom classes and set the property ***custom.classes.jar***

The custom user authentication class will be loaded when it's name is provided by the property ***custom.authenticate.class***

If for some reason, you need to support additional custom grant_type, you can set it's classname using the property ***custom.grant_type.class***

##ApiFest OAuth 2.0 Server Endpoints
| Name | Description | Admin restricted access |
:------------- | :------------- | :-------------:
| */oauth20/login* | logs an user using provided access_token and checking credentials against configured authenticator class to access restricted endpoints when running in production mode | :white_check_mark: |
| */oauth20/applications* | registers client applications (POST method), returns all client applications info (GET method) | :white_check_mark: |
| */oauth20/applications/[client_id]* | returns client application info (GET method), updates a client application (PUT method), deletes a client application (DELETE method) | :white_check_mark: |
| */oauth20/auth-codes* | issues auth codes |  |
| */oauth20/tokens* | issues access tokens |  |
| */oauth20/tokens/validate* | validates access tokens |  |
| */oauth20/tokens/revoke* | revokes access tokens | :white_check_mark: |
| */oauth20/scopes* | creates a new scope (POST method) | :white_check_mark: |
| */oauth20/scopes/[scope_name]* | returns info about a scope name, description and expires_in (GET method), updates a scope (PUT method), deletes a scope (DELETE method) | :white_check_mark: |
| */oauth20/scopes?client_id=[client_id]* | returns scopes by client_id | :white_check_mark: |
| */oauth20/tokens?client_id=[client_id]&user_id=[user_id]* | returns all active tokens for a given user and client application | :white_check_mark: |

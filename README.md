websocket-testbed
=============

Testbed for experimenting with WebSockets.

Compatibility:
=============
Firefox 6.0.2: Native using MozWebSocket (hybi-07)
Firefox 7-10: Native using MozWebSocket (hybi-08)
Safari 5.1.5 (desktop): Native using WebSocket (hixie DRAFT-76)
Chrome 14: Native using WebSocket (hybi-08)
Internet Explorer 6-9: Flash Bridge (hixie DRAFT-76)*
Opera: Flash Bridge (hixie DRAFT-76)**

 * Support for Internet Explorer 6-9 and Opera is provided with a third-party Flash client
 implementation and will not be maintained.  Internet Explorer 10, which will only be available
 for Windows 8, will allegedly support WebSockets natively.  Windows users without an 
 unlimited budget are advised to use Firefox or Chrome, which are regularly updated with 
 new features and security fixes without the added expense of an OS upgrade.

 ** Does anyone other than compatibility testers actually use Opera?

Build Instructions
=============
The current build uses [Maven](http://maven.apache.org/).  To build and install the modules
into a local maven repository, use mvn install from the top-level directory.

The project can be imported into Eclipse with the m2eclipse plugin by cloning the 
repository to the workspace and importing with "Import existing maven projects."  Since the 
example web application uses websocket-server as a dependency, Eclipse will report build
path errors until the websocket-server module has been built and installed.

The web application can be deployed and undeployed through the Maven build.  The relevant 
tomcat manager URL, username and password must be set as system properties when launching 
the script:
* Undeploy `mvn tomcat:undeploy -Dtomcat.manager.url=http://localhost:8080/manager	-Dtomcat.manager.username=admin -Dtomcat.manager.username=password`
* Deploy `mvn tomcat:deploy -Dtomcat.manager.url=http://localhost:8080/manager	-Dtomcat.manager.username=admin -Dtomcat.manager.username=password`

License
=============
This is purely a study project.  See the LICENSE file for mandatory legalese.  
Unless otherwise specified, all application sources shall be considered to be 
released under the BSD 2-clause license.  Friends don't force friends to use GPL.

Third-Party Code
=============
* Hixie76 support based upon the [java-websocket](https://github.com/TooTallNate/Java-WebSocket) project by Nathan Rajlich 
* [Base64Coder.java](http://www.source-code.biz/base64coder/java/)  Copyright 2003-2010 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland; used under the BSD license
* [FABridge.js](http://opensource.adobe.com/svn/opensource/flex/sdk/trunk/frameworks/javascript/FABridge/) Copyright 2006 Adobe Systems Incorporated
* [Flash websocket implementation](http://gimite.net/en/) Copyright: Hiroshi Ichikawa  (New BSD license)
* [SWFObject v2.2](http://code.google.com/p/swfobject) - used under the MIT license
* [json2.js](http://www.JSON.org/json2.js) Public Domain
* [PIE.htc](http://css3pie.com) Used under the Apache License Version 2.0


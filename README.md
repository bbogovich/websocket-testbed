websocket-testbed
=================

Testbed for experimenting with WebSockets.  Support for legacy browsers is provided
using a third-party Flash compatibility wrapper and should be considered deprecated.

Compatibility:
=================
Firefox 6.0.2: Native using MozWebSocket (hybi-07)
Firefox 7: Native using MozWebSocket (hybi-08)
Safari-Desktop: Native WebSocket (DRAFT-76)
Chrome 14: Native using WebSocket (hybi-08)
Internet Explorer 9: Flash Bridge (DRAFT-76)
Opera: Flash Bridge (DRAFT-76)

Build Instructions
=================
The current build uses Maven.  No modifications to a default configuration are needed.
The build output is a war file intended for deployment in Tomcat or another servlet
container.

License
=================
This is purely a study project.  See the LICENSE file for mandatory legalese.  
Unless otherwise specified, all application sources shall be considered to be 
releaed under the BSD 2-clause license.  Friends don't force friends to use GPL.

Third-Party Code
=================
Hixie76 support based upon the java-websocket project by Nathan Rajlich - https://github.com/TooTallNate/Java-WebSocket (New BSD License)
Base64Coder.java  Copyright 2003-2010 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland; www.source-code.biz, www.inventec.ch/chdh; used under the BSD license
FABridge.js Copyright 2006 Adobe Systems Incorporated
Flash websocket implementation Copyright: Hiroshi Ichikawa <http://gimite.net/en/> (New BSD license)
SWFObject v2.2 <http://code.google.com/p/swfobject/> - used under the MIT license
json2.js - http://www.JSON.org/json2.js - Public Domain
PIE.htc - http://css3pie.com - Used under the Apache License Version 2.0


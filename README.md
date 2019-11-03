## Unique Email Address Counting Service

This is a Java-based web service that takes in a list of email addresses and returns an integer indicating the number of unique email addresses.
Where "unique" email addresses means they will be delivered to the same account using Gmail account matching. Specifically: Gmail will ignore the placement of "." in the username. And it will ignore any portion of the username after a "+".

#### General Usage:
1. Run `com.mattring.fetchrewards.emailaddr.EmailAddressService <port>`
1. The port will default to 8080, if left off the command line.
1. The index page (http://localhost:8080/) will display a form for easy testing.
1. For direct use, **POST** to /countUniqueAddrs (http://localhost:8080/countUniqueAddrs) with a form parameter of '_addrList_'.

#### Usage Details:
1. The '_addrList_' param may contain a delimited list of email addresses.
1. Supported delimiters are ',', ';' and combinations of newline characters.
1. Multiple '_addrList_' params are supported and will be concatenated together.
1. If the **POST** submission does not include any '_addrList_' params, then the service will return an error string, matching 'ERROR: No 'addrList' fields were provided.'

#### Developer Comments:
1. Developed and tested with openjdk\jdk_08_192_b12_openj9 on Windows 7, using IntelliJ Idea.
1. Due to time constraints, I purposely kept the server very simple. It's Javalin with no SOAP or REST.
1. There are pretty good unit tests for the core functionality, but edge cases exist, like "what should happen when an email address starts with a '+'"?

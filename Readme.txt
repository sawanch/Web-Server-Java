NAME : Sawan Chakraborty
ASSIGNMENT : PA1
SCU-ID : W1653472

PROJECT DESCRIPTION : The goal of this programming assignment is to build a functional web server. This assignment will teach you the basics of distributed programming, client/server structures, and issues in building high performance servers. While the course lectures will focus on the concepts that enable network communication, it is also important to understand the structure of systems that make use of the global Internet.


EXPLANATION: In the context of this assignment, I have developed a web server application responsible for monitoring a specific port and handling incoming HTTP requests. To enhance its flexibility, this web server allows users to specify both the port number and the document root directory as command-line arguments when launching the executable JAR file. A notable feature of this web server is its support for multithreading, achieved by accepting incoming requests and initiating separate threads for each request's processing. This multithreading capability is realized through the utilization of the `Runnable` interface to handle request processing and the concurrent execution of unique threads for parallel request management. This approach ensures that each client request is processed independently and concurrently, optimizing the server's responsiveness and efficiency.

Within my server implementation, the process involves the extraction of an inputStream from the socket, a fundamental component of the communication process. Subsequently, this inputStream is used to read the HTTP request, specifically focusing on retrieving essential details like the request method and the requested file. It's important to emphasize that my server exclusively supports GET requests, and any utilization of other request methods triggers a deliberate response with a 405 status code, signifying that the method is not allowed.

My server's versatility shines through in its dynamic responses to various types of requests. Depending on the nature of the incoming request, it diligently crafts the appropriate HTTP status code from a selection including 200, 400, 403, 404, and 405. These status codes convey crucial information regarding the success or failure of the request to the client.

Furthermore, my server enhances the responses it sends back to clients by including additional metadata. With each response, it attaches essential headers, such as Content-Type, Content-Length, and Date. These headers provide valuable context and information about the content being transmitted.

One of the noteworthy strengths of my server is its capacity to manage multiple requests simultaneously without blocking any single request. This non-blocking behavior is achieved through the utilization of individual threads for each request, ensuring that they run independently and concurrently. Consequently, the server remains highly responsive and efficient even when dealing with multiple requests in parallel.

In cases where a request does not explicitly specify the desired resource (typically represented as "index.html"), my server defaults to serving the standard "index.html" file. This simplifies the client's interaction with the server, offering an expected and user-friendly experience when specific resource details are omitted.

Once a response is successfully generated and delivered to the client via the OutputStream, the server ensures proper resource management by closing the associated socket. This step is essential for maintaining system resources and ensuring the smooth and efficient operation of the server.



List of Files Attached : 

-> Readme.txt
-> screenshots
-> webserver-project.zip
-> PA1_COEN_317_Sawan_Chakraborty_Report.pdf


INSTRUCTIONS TO RUN THE PROGRAM:

1.Begin by using the Maven build tool to prepare the environment. Open the command-line interface and execute the command "mvn clean install". This step ensures that all project dependencies are in order, and it compiles  the source code.

2.Once the Maven build process is complete, proceed to the next step. Navigate to the specific directory where the executable jar, named "WebServer.jar," is located. This jar file isfound within the "target" directory of your project structure.

3.To run the jar, use the following in the command line 
 java -jar WebServer.jar -document_root /providelocation/to/document/root -port 8484

e.g. java -jar F:\PA-1\webserver-project\target\WebServer\WebServer.jar -document_root F:\PA-1\webserver-project\src\main\resources\document-root -port 8484



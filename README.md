# BXToolDemonstrator : Demon-BX

Prerequisite:

1. Eclipse Configuration:
Install Eclipse Modelling Tool- EMT(Preferably NEON) with below plug-ins:

a) eMoflon (https://emoflon.github.io/eclipse-plugin/emoflon_2.22.0/update-site2/)

b) Web Tools Platform (WTP Version 3.8.1. SDK not required.)(wtp for Neon - http://download.eclipse.org/webtools/repository/neon/)

2. Tomcat:
Install Tomcat Version 7.0 on your system. You can follow the below link for the installation process.
(http://crunchify.com/step-by-step-guide-to-setup-and-install-apache-tomcat-server-in-eclipse-development-environment-ide/)


Import projects from Git, Build and Run:
(http://stackoverflow.com/questions/6760115/importing-a-github-project-into-eclipse)
File -> Import -> Git -> Projects from Git > Clone URI

1. Import Demon-BX 
   (URI: https://github.com/eMoflon/demon-bx.git)
   
   Note: import all 5 projects: Adapter, Client, GridLanguage, KitchenLanguage, KitchenToGridLanguage

2. After importing all 5 projects, check for the problems(if any). You might have to add the dependencies manually if missing.

   Check for all 5 projects, Build path -> Configure build Path -> Deployment Assembly, there must be a row present for Plug-in Dependencies. If not, please add it.

3. In Java EE perspective, Click on “Server” tab -> click on the link to create a new Sever -> Enter and Select the Tomcat server configured earlier -> Next -> Enter the Tomcat server path and JRE version -> Next -> Select the project name and add to the server and Finish

   You will see a folder name “Servers” on Project Explorer

4. Run the Project

   Right click on Project -> Run As -> Run on Server -> select the Tomcat server and Finish

   Now you will be able to run the project on browser by entering http://localhost:8080/Client/GUI.html
   
5. For small code changes, Tomcat automatically build and push the changes while in running mode. Only you have to refresh the browser to see the effect of changes.
   For big changes, changes in javascript file(logic.js) or if the automatic build is not working(sometimes), stop the server, clean and then start once again.   
   

Deploy project on Web-Server:
1. First set-up a Web-Server with java 8 and Tomcat 7.0 installed in it. You will need a login id with admin rights. (Current ready Web-Server: bxtransform.cs.upb.de)

2. Create a war file (Client.war) from the Client (Right click on Client Project -> Export -> WAR file) and save it at a desired location.

3. Login to the Web-Server with your login id (admin rights). Locate "webapps" folder inside tomcat7 default directory on the Web-Server. 
   Move the newly generated WAR file as in step 2 into the Web-Server's tomcat7/webapps folder.

4. Start the tomcat on Web-Server -- Open a command prompt on the Web-Server. Go to tomcat7 default directory and run the "startup.sh" file. 

   For example,
   $: cd /opt/tomcat7
   $: ./bin/startup.sh
   
   Now you will be able to run the project on browser by entering http://server-name:8080/Client/GUI.html
   
5. For re-deployment of the WAR file, follow the below steps:
   
   a) create a new WAR file (Client.war) from Client as described in Step 2.
   
   b) Stop the tomcat running on the Web-Server -- Open a command prompt on the Web-Server. Go to tomcat7 default directory and run the "shutdown.sh" file.  
   
      For example,
      $: cd /opt/tomcat7
      $: ./bin/shutdown.sh 
	  
   c) Delete the already existing Client.war file and Client folder from Web-Server's tomcat7/webapps folder.
   
      For example,
	  $: cd /opt/tomcat7/webapps
      $: rm Client.war
      $: rm -rf Client
   
   d) Move the newly generated WAR file as in step 5(a) into the Web-Server's tomcat7/webapps folder. 
   
   e) Start the tomcat on the Web-Server as described in Step 4.
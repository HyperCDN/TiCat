# Docker 4 Dev - Setup Guide  


1) Install Docker  
   https://docs.docker.com/engine/install/  
2) Open a terminal inside the docker4dev directory and create the containers by executing   
   ``docker compose up --no-start``  
   You can start them via ``docker compose start``
   or stop them through ``docker compose stop`` 
3) Open a webbrowser to configure Keycloak  
   http://localhost:8080/admin/master/console/  
4) Login using the `admin` account with the password `adminsupersecret`  
5) Create a new realm through the realm selection dropdown naming it `TiCat`  
6) Create the role `TiCatApp_user` 
7) Create the role `TiCatApp_admin` and associate `TiCatApp_user`   
7) Add the following users to the realm. Make sure to disable the temporary credentials toggle.  
   `ticat_admin` with password `ticat_admin` and role `TiCatApp_admin`  
   `ticat_user` with password `ticat_user` and role `TiCatApp_user`
8) Create a client with the client id `TiCatApp`  
   Set `http://localhost:3000/auth/returning` as redirect uri  
   Set `http://localhost:3000/` as post logout redirect uri  
   Set `+` as web origins  
   Save the changes made  


# J.A.R.V.I.C.
Just A Rather Very Intelligent Chatbot

## Dependencies - 

### Database : 
Install mysql in Python3 : https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-16-04  \
Create a table for JARVIC using the following commands-
~~~~
$mysql -u root -p
mysql>CREATE DATABASE JARVIC;
mysql>USE JARVIC;
mysql>CREATE TABLE USERS (name VARCHAR(100),username VARCHAR(100),password VARCHAR(100),contact VARCHAR(100),history VARCHAR(65000));
~~~~
### Machine Learning :
Install tensorflow, numpy, pickle, nltk and sklearn 

## To run JARVIC:

~~~~ 
$git clone https://github.com/rohitjack/J.A.R.V.I.C..git 
$cd J.A.R.V.I.C.
$python3 chatbot.py --mysqlpass="password of your mysql"
~~~~
To run Front End :
- set the host names in server.py and Client.java files.
- compile the Client.java 
- run server.py 
- run Client.java
- GUI will appear for the user. The server end can chat from the terminal only.

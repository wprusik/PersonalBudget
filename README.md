## PersonalBudget

PersonalBudget is a tool for comprehensive management of your money. It allows you to manage bank accounts, add current, search and plan transactions, create your own budgets, manage your debts, generate charts to trackyour spending structure, convert currencies, and so on.

#### Technologies used:
- Spring Boot with Spring Security, Hibernate, REST
- Thymeleaf, Bootstrap
- JavaScript, jQuery
* HighCharts library - *used under the Creative Commons Attribution-NonCommercial 3.0 license*

#### Startup instructions:
1.	Import the project into your IDE (it&#39;s a Spring Boot project)
2.	Install XAMPP or its equivalent, run the MySQL server
3.	Go to the phpMyAdmin and run the SQL script from the **personal_budget.sql** file
4.	On the MySQL server, create a user and grant him all privileges to the **personal_budget** database, e.g.
```sql
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON `personal_budget`.* TO 'admin'@'localhost';
```
5.	*(optionally)* Edit the **application.properties** file located in the **src\main\resources** directory
4a. If your MySQL server is running on a port other than 3306, set it in line 4
4b. If you named the user differently than in the example from point 4, set the correct username and password in lines 5 and 6
6.	Run the application by selecting **DemoApplication.java** as the main class

&#42;*There is a test account prepared for a better presentation of the application capabilities (login: **test**   password: **test123**)*

&nbsp;
-------------
###### Additional information:
&#42;*The site is not yet full responsive, so far it has been optimized for fullHD resolution (1920x1080).*

&#42;&#42;*Proper working of this application depends on the availability of [www.exchangeratesapi.io](http://www.exchangeratesapi.io "www.exchangeratesapi.io") website, from which current exchange rates are retrieved. If it isn&#39;t available, the application will inform you about itby displaying the error page with the appropriate information.*

&#42;&#42;&#42;*The registration process has been maximally simplified and the login page has no password recovery optiondue to the fact that the application was not created with a view to its subsequent implementation.*

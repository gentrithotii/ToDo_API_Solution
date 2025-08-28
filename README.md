## ⚙️ Backend Setup

### Requirements

- Java JDK 21+

- Maven

- MySQL Database

- Git

- IDE (e.g., IntelliJ IDEA)

### Steps

1. Fork and clone the backend repository: [ToDo API](https://github.com/mehrdad-javan/ToDo_API_Solution)

2. Run the backend server.

3. Test all API endpoints via Swagger.

4. Verify JWT authentication is working.

### User Credentials

Admin User

• Username: admin  
• Password: password  
• Role: ADMIN  
• Email: admin@test.se

Regular User

• Username: user1  
• Password: password  
• Role: USER  
• Email: user1@test.se  

### Email Service (Optional)

If you want to enable email notifications:

- Clone this repo: [notify-util-spring](https://github.com/mehrdad-javan/notify-util-spring)

- Add its dependencies to your pom.xml.
- Set environment variables:  
  - APP_USER_EMAIL=your_email  
  - APP_USER_PASSWORD=your_password 
- Enable NotifyUtilConfig in App Config
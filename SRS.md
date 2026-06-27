**Software Requirement Specification(SRS)**

**For**

**Nightingale,** 

**A Hospital Management App**

**Version 1.0**

**Prepared By \- Group-8**

**Table Of Contents**

**[1.Introduction	2](#1.introduction)**

[1.1 Project Title	2](#1.1-project-title)

[1.2 Purpose	2](#1.2-purpose)

[1.3 Intended Audience	3](#1.3-intended-audience)

[1.4 Scope	3](#1.4-scope)

[**2\. Overall Description	4**](#2.-overall-description)

[2.1 User Roles	4](#2.1-user-roles)

      [2.2 Operating Environment	4](#2.2-operating-environment)

[2.3 Assumptions	5](#2.3-assumptions)

[**3\. Functional Requirements(FR)	6**](#3.-functional-requirements\(fr\))

[**4\. Non-Functional Requirements(NFR)	7**](#4.non-functional-requirements\(nfr\))

[**5\. External Interfaces	8**](#5.-external-interfaces)

[**6\. Constraints & Assumptions	8**](#6.-constraints-&-assumptions)

[**7\. Appendices	9**](#7.-appendices)

### **1.Introduction** {#1.introduction}

### **1.1 Project Title** {#1.1-project-title}

***Nightingale, A Hospital Management App***

### **1.2 Purpose** {#1.2-purpose}

The purpose of the ***Nightingale ,A Hospital Management App*** is to make hospital services easier, faster, and more organized for everyone involved-patients, doctors, and administrators.

In many hospitals, tasks like booking appointments, managing patient records, writing prescriptions, and handling admissions are still done manually or through scattered systems. This often leads to delays, errors, and miscommunication. Our project aims to solve these problems by bringing everything into one simple mobile application.

The main goal is to reduce paperwork, improve communication, and ensure that healthcare services become more accessible and reliable. By digitizing these processes, the system helps save time, minimize errors, and create a smoother experience for both healthcare providers and patients.

This project is about transforming traditional hospital management into a smart, connected, and user-friendly digital system.

### 

### **1.3 Intended Audience** {#1.3-intended-audience}

- [ ] Software Developers  
- [ ] UI/UX Designers  
- [ ] Project Supervisors  
- [ ] Stakeholders  
      

### **1.4 Scope** {#1.4-scope}

Nightingale is an Android-based mobile application developed using Kotlin that aims to digitize and automate hospital management processes. The system will support three primary user roles:

* Admin  
* Doctor  
* Patient

The application will streamline hospital operations, improve communication, and ensure efficient healthcare service delivery.

## 

## **2\. Overall Description** {#2.-overall-description}

### **2.1 User Roles** {#2.1-user-roles}

| User Type | Description |
| ----- | ----- |
| Admin | Manages hospital resources, schedules, and overall system control |
| Doctor | Provides medical services, prescriptions, and monitors patients |
| Patient | Books appointments, views medical data, and interacts with doctors |

## 

## **2.2 Operating Environment** {#2.2-operating-environment}

The *Nightingale Hospital Management App* operates in a mobile and cloud-based environment to ensure smooth performance and accessibility.

### **Client-Side**

- [ ] Platform: Android  
- [ ] Language: Kotlin  
- [ ] OS Requirement: Android 8.0 or above  
- [ ] Runs on smartphones with internet access

### **Server-Side**

- [ ] Backend: Firebase (or similar cloud service)  
- [ ] Provides authentication, database, and notification services  
      

### **Database**

- [ ] Cloud-based (NoSQL) database  
- [ ] Stores patient records, appointments, prescriptions, and other hospital data securely

### **Network**

- [ ] Requires stable internet connection  
- [ ] Supports real-time data synchronization

### **User Environment**

- [ ]  Accessible by Admins, Doctors, and Patients  
- [ ] Secure login with a simple and user-friendly interface

### 

### **2.3 Assumptions** {#2.3-assumptions}

- The system will run on Android devices  
- Internet connectivity is required  
- Secure authentication is mandatory for all users  
- Data privacy regulations must be followed

## **3\. Functional Requirements(FR)** {#3.-functional-requirements(fr)}

| FR ID | Module | Functional Requirements |
| ----- | ----- | ----- |
| FR-1 | User Authentication | \- Secure registration and login  \- Role-based access: Admin, Doctor, Patient  \- Password encryption and secure session handling |
| FR-2 | Admin Module | \- Manage hospital beds, operation theatres, and diagnostic tests \- Add/update/remove doctor profiles and manage schedules  \- View system activities and generate reports |
| FR-3 | Doctor Module | \- Set and manage appointment slots  \- Access patient history and monitor treatments \- Create/share prescriptions digitally  \- Check medicine availability  |
| FR-4 | Patient Module | \- Search, book, and cancel appointments  \- View prescriptions, diagnostic results, and maintain medical history \- Receive notifications and updates from doctors/admin |
| FR-5 | Database Management | \- Centralized storage of patients, doctors, appointments, and     prescriptions  \- Ensure data consistency and integrity |

## **4.Non-Functional Requirements(NFR)** {#4.non-functional-requirements(nfr)}

| NFR ID | Category | Description |
| ----- | ----- | ----- |
| NFR-1 | System Performance | Fast response time (\< 3 seconds) and efficient handling of concurrent users. |
| NFR-2 | Information Security | Data encryption (at rest and in transit), robust authentication and authorization, protection against unauthorized access. |
| NFR-3 | User Experience (UX) | Intuitive and user-friendly interface, seamless navigation, minimal learning curve. |
| NFR-4 | System Reliability | High availability (24/7) with minimal downtime and fault tolerance. |
| NFR-5 | Scalability & Extensibility | Capability to handle increasing numbers of users and expanding data volume. |
| NFR-6 | Maintainability & Modularity | Modular architecture to facilitate easy updates, enhancements, and bug fixes. |

## **5\. External Interfaces** {#5.-external-interfaces}

### **User Interface(UI)**

- Web and mobile interfaces for patients to search doctors, select departments, book appointments, and view statuses.  
- Doctor dashboard to accept, reject, or manage appointments.

 **APIs**

- RESTful APIs for appointment creation, status updates, and user authentication.  
- Integration with notification services (email/SMS) for reminders.

 **Hardware**

- Standard client devices (PC, smartphones, tablets).  
- Servers to host backend services and databases.

**Database**

- Relational database(eg. SQL) to store user, doctor, department, and appointment data.  
- Support for secure data retrieval and updates.

**Other Interfaces**

- Payment gateways (if online payments are implemented).

### 

### **6\. Constraints & Assumptions** {#6.-constraints-&-assumptions}

* Real-time appointment updates require internet connectivity; offline support is not included.  
* User authentication (e.g., login, OTP) assumes valid credentials are provided.  
* All patient and doctor data must be stored on university-approved or secure servers.  
* Notification delivery (email/SMS) depends on third-party services availability.  
* Appointment conflicts are prevented by system logic, but manual overrides by staff may cause inconsistencies.  
* User consent is assumed for storing personal and medical-related data (compliant with GDPR/local data laws).

## **7\. Appendices** {#7.-appendices}


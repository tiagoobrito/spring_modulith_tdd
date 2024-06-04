# Exploring Modularity in Monoliths

Modular monolith application development using Spring Modulith and comparasion with non modular monolith.

The prototype is developed using a modular monolith architecture but also with non modular monolith for performance comparasion using Jmeter and code quality comparison using SonarGraph. The modular prototype has two versions, one that uses asynchronous communication and another that uses synchronous communncation.
The modular protoype has the following modules: Owner, Pet, Vet, Visit. The focus of the project was to explore modularity in monoliths using Spring Modulith. To ensure that the prototypes are equal in terms of functionality and most important, that they are functioning, there are 42 intergration tests and 47 unitary tests for each prototype.

## Context of the project

There is an app, with a user interface, that can be used by veterinary clinics to manage their operations. This allows clinic staff to easily add and edit pet owner information, thus ensuring a complete and organized record of each client. In addition, the application allows you to associate animals with their respective owners, thus facilitating their monitoring, and you can also edit their information, if necessary. Another feature of the application is the ability to schedule visits for the animals, that is, professionals can record appointments, exams, vaccinations and other procedures, thus ensuring a detailed monitoring of the health of each animal. In addition, it is also possible to view the list of existing veterinarians in the clinic with all their information, such as their specialization. Pet owners are characterised by a private identifier, a first and last name, their address/address, their city of residence and their telephone number. Animals are characterized by a private identifier, their name, their date of birth, the type of animal. Visits are characterised by a private identifier, the date of the visit and a description of the subject of the visit. Veterinarians are characterized by a private identifier, first and last name, and their specialty. 

The clinic's staff can only register an owner or a visit by correctly providing all the information that characterizes it, already described above. The same applies to the registration of animals, but in these cases, attention must be paid to the date of birth, as obviously it cannot be a date later than the day on which the registration is being carried out. Since the developed application is, in practice, a prototype, it will have owners, animals and visits previously created.

## Analysis

### Domain Model

![dm1](https://github.com/miguel1211593/spring_modulith/assets/106148619/e348b56c-e7cb-43b7-8a63-7b27c9df2928)

### Use Case Diagrams

![ucdOwner](https://github.com/miguel1211593/spring_modulith/assets/106148619/e2d77e1a-98a4-489d-bb45-76fa88fdd599)

![ucdPet](https://github.com/miguel1211593/spring_modulith/assets/106148619/608e17fb-66d0-4940-9f35-e93022fee677)

![ucdVet](https://github.com/miguel1211593/spring_modulith/assets/106148619/84e74682-13cc-4aec-96e3-de6387e3c3a8)

![ucdVisit](https://github.com/miguel1211593/spring_modulith/assets/106148619/0ec6bf32-4af2-48f8-9007-519b03757591)


## Design

Here, the architectural representation of the application will be presented and two models will be used for this, C4 and 4+1. In the diagrams made for this project, not all levels of abstraction are explored since they are not relevant to the understanding of the project.

### Modular Monolith Documentation

### Logic View

Level 1:

![n1vl](https://github.com/miguel1211593/spring_modulith/assets/106148619/385f1d66-01ca-4d14-86e4-aac360f5065b)

Level 2 (Asynchronous Version):

![n2vlmoduasync](https://github.com/miguel1211593/spring_modulith/assets/106148619/9ee24a31-1d5d-4f73-a9f3-62cacdbde8ae)

Level 2 (Synchronous Version):

![n2vlmodusync](https://github.com/miguel1211593/spring_modulith/assets/106148619/a84db78e-f990-4ed4-b54d-0dc0f4d1568a)

Level 3:

![n3vlmodu](https://github.com/miguel1211593/spring_modulith/assets/106148619/6c2e9113-d8f4-4cff-b869-7caa87bcdb86)

### Physical View (Asynchronous Version)

![asyncvf](https://github.com/miguel1211593/spring_modulith/assets/106148619/a8ad272b-aebc-4251-ab13-699da851683e)


### Physical View (Synchronous Version)

![vf](https://github.com/miguel1211593/spring_modulith/assets/106148619/e4133bd0-d2ac-4e60-a94a-295e15b87125)


Database separation pattern:

![Alternative](https://github.com/miguel1211593/spring_modulith/assets/106148619/a9821270-279c-439e-a404-ca86f193a565)

### Process View:

Here there will be only three diagrams to provide example.

Get Owner (Level 3):

![ModulithSearchOwner](https://github.com/miguel1211593/spring_modulith/assets/106148619/c40bc543-3f74-4a0a-904a-1ba14c710f43)

Post Pet (Level 3 - Asynchronous):

![N3AddPet](https://github.com/miguel1211593/spring_modulith/assets/106148619/a0df4003-f4b7-4d3c-a04f-b861557597cc)

Post Pet (Level 3 - Synchronous):

![N3AddPet](https://github.com/miguel1211593/spring_modulith/assets/106148619/44452af5-575c-4957-8467-12774e2221bc)

### Development View

Level 3:

![n3vi](https://github.com/miguel1211593/spring_modulith/assets/106148619/a5afd761-dd19-492c-a78b-f6c29edbc5eb)

Level 4:

![n4vi](https://github.com/miguel1211593/spring_modulith/assets/106148619/201839d7-5b2c-4184-bd6b-144596eeac0b)


### Non Modular Documentation

### Logic View

Level 1: The same as in Modular Monolith

Level 2: 

![n2vlmono](https://github.com/miguel1211593/spring_modulith/assets/106148619/ced2bdf4-4aa5-4a3b-bfad-1cf2c372bb5e)

Level 3: 

![n3vlmono](https://github.com/miguel1211593/spring_modulith/assets/106148619/1934ee05-3aa9-474f-995d-23b115e9ebd6)

### Physical View

![monovf](https://github.com/miguel1211593/spring_modulith/assets/106148619/31ef1ba9-1846-4f4d-8067-8a9c03e7e8e2)


### Process View

Get Owner (Level 3):

![monoSearchOwnerSD](https://github.com/miguel1211593/spring_modulith/assets/106148619/30ee1791-8b12-4634-aa21-bc5b7e885d70)


Post Pet (Level 3):

![N3AddPet](https://github.com/miguel1211593/spring_modulith/assets/106148619/323a4867-7028-47f6-b893-dcde1f6e4570)

### Development View

![monovi](https://github.com/miguel1211593/spring_modulith/assets/106148619/a1e448f6-c0d3-47c4-99a5-1eb406a0c7c1)











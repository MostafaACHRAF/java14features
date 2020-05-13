# java14features
A simple demo for some features of java14 using spring JDBC API and dockerized postgres container.

## Tested features are :
### Smart switch
var index = switch(StateEnum) {\
 case HAPPY -> 1;\
 case SO_SO -> 0;\
 case SAD -> -1;\
}

### Smart cast
if(person.getAge() instanceof Integer age) {\
   return age++;\
}

### Records
record Person(Intger id, String firstName, String lastName) {\
}

### Compact constructor (can be used only with records)
record CoronaVirusStatus(String country, Long numberOfCases) {\
   public CoronaVirusStatus {\
      this.contry = country.toUpperCase();\
   }\
}

### String multilines
var greeting = """\
 Hello world\
 !\
""";\



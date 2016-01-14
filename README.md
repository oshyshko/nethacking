# nethacking

## Prerequisites
- JDK 8 
- Maven 3

## Running examples

    git clone https://github.com/oshyshko/nethacking.git
    
    cd nethacking
     
    mvn install

    mvn exec:java -Dexec.mainClass="examples.E01_Interfaces"
    mvn exec:java -Dexec.mainClass="examples.E02_Listening"
    mvn exec:java -Dexec.mainClass="examples.E03_Sending"
    mvn exec:java -Dexec.mainClass="examples.E04_Sending_and_Listening"

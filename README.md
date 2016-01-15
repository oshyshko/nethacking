# nethacking

## Prerequisites
- JDK 8 
- Maven 3

## Running examples

    git clone https://github.com/oshyshko/nethacking.git
    
    cd nethacking
     
    mvn install

    mvn exec:java -Dexec.mainClass="examples.E01_Interfaces"
    mvn exec:java -Dexec.mainClass="examples.E02_Send"
    mvn exec:java -Dexec.mainClass="examples.E03_Send_ARP"
    mvn exec:java -Dexec.mainClass="examples.E04_Listen"
    mvn exec:java -Dexec.mainClass="examples.E05_Listen_and_Send"

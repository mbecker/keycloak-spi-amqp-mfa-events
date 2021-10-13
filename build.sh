rm -r -f ./ear-module/target &&\
rm -r -f ./jar-module/target &&\
mvn clean package
# mvn -B -e -C -T 1C org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline && mvn -B -e -o -T 1C verify
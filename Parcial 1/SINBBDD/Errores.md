
# Para el JDBC Connection Pools 


Se debe copiar el archivo el driver de mysql-connector-java-8.0.33.jar la version que tengas a la carpeta lib del servidor Payara.

En la ruta C:\payara7\glassfish\domains\domain1\lib

----

Se debe cambiar en Datasource Classname: 

com.mysql.cj.jdbc.MysqlDataSource

----

Para que funcione payara 7 el web service debemos colocar el jar que esta en esa ruta en el repositorio de maven:

https://repo1.maven.org/maven2/jakarta/jws/jakarta.jws-api/3.0.0/

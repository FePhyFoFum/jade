jade
====

A fork of the PEBLS Jade collection of Java objects for phylogenetics and evolutionary biology

install
-----

To install this repository as a maven dependency:

1. git clone the jade repo locally
2. cd into the jade directory and run:

```
sh mvn_install.sh
```

3. copy and paste the following code into the pom.xml file of any local maven project to access the jade packages

```
<dependency>
  <groupId>org.opentree</groupId>
  <artifactId>jade</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

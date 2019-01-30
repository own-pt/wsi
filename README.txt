WeSearch Infrastructure (WSI)
Installation & Ussage Guide

Author: Milen Kouylekov
email: milen@ifi.uio.no
license: LGPL (see https://web.archive.org/web/20180428082142/http://wesearch.delph-in.net/)

1. Requirements

To run the WSI the user must have the following software installed in
their system:

1) Java 7 (OpenJDK tested, SUN Java should also be ok)

2) Maven (Apache Maven http://maven.apache.org/) 

3) Graphviz DOT (Required if the user wants to use SVG as structure
   representation in the graphical interface)

2. Compilation and Installation

Command: mvn install

The compilation and installation procedure should printout the
following lines

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------

as one of the last lines.

3) Indexing Data

Indexing data is done using the create-index script. The script has
the following parameters:

usage: create-index -f <arg> [-h] [-i <arg>] [-n] -o <arg> [-s]
Create index directory for WeSearch infrastructure
  -f,--format <arg>    Format of the input file. Possible values: conll, eds,
                       mrs, dm, pas & psd
  -h,--help            Display help
  -i,--filter <arg>    Filter graphs with id higher than stated
  -n,--no-svg          Do not generate svg
  -o,--output <arg>    The location of the generated index
  -s,--has-sense       SDP input file has sense column

Example 1. Creating an index from Deepbank corpus

./create-index -i 22100001 -f mrs -o <INDEX_PATH> <DEEPBANK_EXPORT_PATH>
./create-index -i 22100001 -f eds -o <INDEX_PATH> <DEEPBANK_EXPORT_PATH>
./create-index -i 22100001 -f dm -o <INDEX_PATH> dm.sdp


If you want to have the same demo for both formats the INDEX_PATH
should be the same for both commands.

Example 2. Create an index from SDP corpus with senses.

./create-index -i 22100001 -s -f dm -o <INDEX_PATH> dm.sdp 
./create-index -i 22100001 -s -f pas -o <INDEX_PATH> pas.sdp 
./create-index -i 22100001 -s -f psd -o <INDEX_PATH> psd.sdp

 
4) Creating a web demo from the input.

To have a web demo the user must posses a java web application server
(tomcat or jetty). To create the application the following procedure
must be followed:

Step 1. Go into  trunk/generic-gui

Step 2. Edit the file src/main/webapp/WEB-INF/web.xml

In this file the user must set the following parameters:

		<init-param>
			<param-name>DATA_PATH</param-name>
			<param-value>INDEX_PATH -edit </param-value>
		</init-param>
		<init-param>
			<param-name>LOG_PATH</param-name>
			<param-value>LOG_DIR_PATH -edit </param-value>
		</init-param>
		<init-param>
			<param-name>DEMO_NAME</param-name>
			<param-value>DEMO_NAME - edit</param-value>
		</init-param>

Step 3. Compile the web application with commands:

mvn compile
mvn war:war

in the  trunk/generic-gui directory.

Step 4. Upload the generated war file
trunk/generic-gui/target/demo.war in your java web application server.

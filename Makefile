build:
	mvn -B package -DskipTests

dependency:
	mvn -B dependency:resolve
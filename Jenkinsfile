pipeline {
    agent any
	
    stages {
        stage('Build') {
            steps {
				if (isUnix()) {
					sh "./gradlew build"
				}
				else {
					bat "gradlew.bat build"
				}
            }
        }
    }
}
pipeline {
    agent any
	
    stages {
        stage('Build') {
            steps {
				withEnv() {
					if (isUnix()) {
						sh "./gradlew build"
					} else {
						bat "gradlew.bat build"
					}
				}
            }
        }
    }
}
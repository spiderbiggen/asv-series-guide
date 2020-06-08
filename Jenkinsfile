pipeline {
  agent { docker { image 'spiderbiggen/android' } }
  options {
    // Stop the build early in case of compile or test failures
    skipStagesAfterUnstable()
  }
  stages {
    stage('Compile') {
      steps {
        // Compile the app and its dependencies
        sh './gradlew compilePureDebugSources'
      }
    }
    stage('test') {
      steps {
        withCredentials([file(credentialsId: 'asv-secrets', variable: 'SECRET_PROPERTIES')]) {
          sh 'cp $SECRET_PROPERTIES ./secret.properties'
          sh './gradlew --stacktrace testPureDebugUnitTest'
          // Analyse the test results and update the build result as appropriate
          junit '**/TEST-*.xml'
        }
      }
    }
  }
}
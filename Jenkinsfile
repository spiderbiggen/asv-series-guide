pipeline {
  agent { docker { image 'spiderbiggen/android' } }
  options {
    // Stop the build early in case of compile or test failures
    skipStagesAfterUnstable()
  }
  stages {
    stage('Clean') {
      steps {
        sh './gradlew clean'
      }
    }
    stage('Compile') {
      steps {
        // Compile the app and its dependencies
        sh './gradlew compilePureDebugSources'
      }
    }
    stage('Test') {
      steps {
        sh './gradlew :app:testPureDebugUnitTest'
        // Analyse the test results and update the build result as appropriate
        junit '**/TEST-*.xml'
        publishCoverage adapters: [jacocoAdapter('app/build/reports/jacoco/testPureDebugUnitTestCoverage/testPureDebugUnitTestCoverage.xml')]
      }
    }
    stage('SonarQube') {
      steps {
        withCredentials([
          string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_LOGIN'),
          string(credentialsId: 'SONAR_HOST_URL', variable: 'SONAR_HOST_URL')
        ]) {
          sh './gradlew sonarqube'
        }
      }
    }
  }
}
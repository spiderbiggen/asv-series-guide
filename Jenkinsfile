pipeline {
  agent { docker { image 'spiderbiggen/android' } }
  stages {
    stage('test') {
      steps {
        withCredentials([file(credentialsId: 'asv-secrets', variable: 'SECRET_PROPERTIES')]) {
          sh 'cp $SECRET_PROPERTIES ./secret.properties'
          sh './gradlew --stacktrace :app:createPureDebugCoverageReport :app:jacocoPureDebug'
        }
      }
    }
  }
}
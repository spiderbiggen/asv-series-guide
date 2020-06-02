pipeline {
    agent { docker { image 'alvrme/alpine-android:android-29' } }
    stages {
        stage('test') {
            steps {
                sh 'gradlew jacocoTestReport'
            }
        }
    }
}
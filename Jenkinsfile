pipeline {
  agent {
    node {
      label 'AWS_STANDARD_SLAVE'
    }
  }

  triggers {
    pollSCM('')
    githubPush()
  }
  
  options {
    buildDiscarder(logRotator(numToKeepStr: '1'))
    disableConcurrentBuilds()
    timeout(time: 1, unit: 'HOURS')
  }

  tools {
    gradle "gradle"
  }

  stages {
    stage("Notify Start") {
      steps {
        emailext body: 'Your app is building!', recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider'], [$class: 'CulpritsRecipientProvider']], subject: '$PROJECT_NAME - Build # $BUILD_NUMBER - Started!'
      }
    }

    stage("Build App") {
      steps {
        script {
          sh "gradle clean assembleRelease --info --stacktrace"
        } 
      }
    }

    stage("Archive APK") {
      steps {
        archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.apk', fingerprint: true, onlyIfSuccessful: true
      }
    }

    stage('Deploy') {
      steps {
        androidApkUpload apkFilesPattern: '**/*-release.apk', deobfuscationFilesPattern: '**/mapping.txt', googleCredentialsId: 'api-6886201687893048698-196111', rolloutPercentage: '100%', trackName: 'beta'
      }
    }
  }
  
  post {
    failure {
      emailext attachmentsPattern: '**/*.apk', attachLog: true, body: '$DEFAULT_CONTENT', recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider'], [$class: 'CulpritsRecipientProvider']], subject: '$PROJECT_NAME - Build # $BUILD_NUMBER - failure!'
    }

    success {
      emailext attachmentsPattern: '**/*.apk', body: '$DEFAULT_CONTENT', recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider'], [$class: 'CulpritsRecipientProvider']], subject: '$PROJECT_NAME - Build # $BUILD_NUMBER - success!'
    }
  }
}

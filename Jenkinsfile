pipeline {
  agent any
  stages {
    stage('Buid') {
      steps {
        build(job: 'run_testApp', wait: true, propagate: true)
      }
    }
  }
}
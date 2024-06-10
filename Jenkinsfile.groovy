pipeline {
    agent any
    
    parameters {
        booleanParam(name: 'autoApprove', defaultValue: false, description: 'Automatically run apply after generating plan?')
        choice(name: 'action', choices: ['apply', 'destroy'], description: 'Select the action to perform')
    }

    environment {
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
        AWS_DEFAULT_REGION    = 'eu-west-1'

    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: '083f268b-1ab6-4871-a097-baea446e7737', url: 'https://github.com/KirillJBee/diplomataskbeta'
            }
        }
    }

}
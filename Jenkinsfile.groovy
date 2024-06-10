pipeline {
    agent { 
                label 'JenkinsNode1'
            }
    
    parameters {
        booleanParam(name: 'autoApprove', defaultValue: false, description: 'Automatically run apply after generating plan?')
        choice(name: 'action', choices: ['apply', 'destroy'], description: 'Select the action to perform')
    }

    environment {
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
        AWS_DEFAULT_REGION    = 'eu-west-1'
        GIT_TOKEN = credentials('GIT_TOKEN')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scmGit(
                                branches: [[name: 'main']],
                                userRemoteConfigs: [[credentialsId:'GIT_TOKEN',
                                url: 'https://github.com/KirillJBee/diplomataskbeta.git']])
            }
        }

        stage('Terraform init') {
            steps {
                sh 'terraform init'

                //  cleanWs()
                //     dir("${env.WORKSPACE}@tmp") {
                //         deleteDir()
                //     }
            }  
        }

        stage('Plan') {
            steps {
                sh 'terraform plan -out tfplan'
                sh 'terraform show -no-color tfplan > tfplan.txt'
            }
        }
    
    }

}
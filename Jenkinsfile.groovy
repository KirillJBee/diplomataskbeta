pipeline {

    agent { 
                label 'JenkinsNode1'
            }
    
    parameters {
        // booleanParam(name: 'autoApprove', defaultValue: false, description: 'Automatically run apply after generating plan?')
        choice(name: 'action', choices: ['apply', 'destroy'], description: 'Select the action to perform')
    }

    environment {
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
        AWS_DEFAULT_REGION    = 'eu-west-1'
        GIT_TOKEN = credentials('GIT_TOKEN')
        DOCKERHUB_CREDENTIALS = credentials('kirilljbee_dockerhub')
        NAME_IMAGE = 'kirilljbee/diplomatask:latest'
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

        stage('Build image webpage') { 
            steps {
                sh 'docker build -t ${NAME_IMAGE} .'    
            }
        }

        stage('Push image webpage') {
            steps {
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                sh 'docker push ${NAME_IMAGE}'
                sh 'docker system prune -af'
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


        stage('Apply / Destroy') {
            steps {
                script {
                    if (params.action == 'apply') {
                        sh 'terraform ${action} -input=false tfplan'
                    } 
                    else if (params.action == 'destroy') {
                        sh 'terraform ${action} --auto-approve'
                    } 
                    else {
                        error "Invalid action selected. Please choose either 'apply' or 'destroy'."
                    }
                }
            }
        }

        // stage('Plan') {
        //     steps {
        //         sh 'terraform plan -out tfplan'
        //         sh 'terraform show -no-color tfplan > tfplan.txt'
        //     }
        // }
        
        // // stage('Apply plan') {
        // //     steps {
        // //         sh 'terraform apply --auto-approve'
        // //     }
        // // }

        // stage('Destroy') {
        //     steps {
        //         sh 'terraform destroy --auto-approve'
        //     }
        // }


    }

}
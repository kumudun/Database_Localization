pipeline {
    agent any

    tools {

        maven 'Maven 3'

    }
    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        DOCKERHUB_CREDENTIALS_ID = 'Docker-Hub'
        DOCKERHUB_REPO = 'kumudun/database_localization'
        DOCKER_IMAGE_TAG = 'latest'


    }
    stages{
        stage('check'){
            steps {
                git branch: 'main', url: 'https://github.com/kumudun/Database_Localization.git'
            }
        }

        stage('build job: '){
            steps {
                bat  'mvn clean install'
            }
        }
        stage('test'){
            steps {
                bat 'mvn test'
            }
        }
        stage('Report'){
            steps {
                bat 'mvn jacoco:report'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                        docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                    }
                }
            }
        }



    }
}
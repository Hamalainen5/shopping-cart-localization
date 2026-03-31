pipeline {
    agent any

    tools {
        maven "MAVEN_HOME"
        jdk "JAVA_25"
    }

    environment {
        DOCKERHUB_REPO = "eemelham/shopping-cart-localization"
        DOCKER_IMAGE_TAG = "latest"
    }

    stages {

        stage("Checkout git") {
            steps {
                git branch: 'main',
                url: 'https://github.com/Hamalainen5/shopping-cart-localization'
            }
        }

        stage("Maven clean and install") {
            steps {
                bat "mvn clean install"
            }
        }

        stage("Generate JaCoCo report") {
            steps {
                bat "mvn jacoco:report"
            }
        }

        stage("Publish Test Results") {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage("Publish Coverage report") {
            steps {
                publishHTML(target: [
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage'
                ])
            }
        }

        stage("Build Docker Image") {
            steps {
                script {
                    dockerImage = docker.build(
                        "${env.DOCKERHUB_REPO}:${env.DOCKER_IMAGE_TAG}",
                        "."
                    )
                }
            }
        }

        stage("Push Docker Image to Docker Hub") {
            steps {
                bat "docker context use default"
                script {
                    docker.withRegistry(
                        'https://registry.hub.docker.com',
                        'Docker_Hub'
                    ) {
                        dockerImage.push()
                    }
                }
            }
        }
    }
}
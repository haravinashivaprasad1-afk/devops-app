pipeline {
    agent any
    environment {
        DOCKER_HUB_USER = 'haravinashivaprasad1'
        IMAGE_NAME      = 'devops-app'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Artifact') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest ."
            }
        }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USER')]) {
                    sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USER --password-stdin"
                    sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                }
            }
        }
        stage('Deploy to Minikube') {
            steps {
                sh """
                    cat k8s-deployment.yaml | docker run -i --rm --network host \
                        -v /root/.kube:/root/.kube:ro \
                        bitnami/kubectl:latest apply --validate=false -f -
                    docker run --rm --network host \
                        -v /root/.kube:/root/.kube:ro \
                        bitnami/kubectl:latest rollout restart deployment/devops-app
                """
            }
        }
    }
}

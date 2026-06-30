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
                    if [ ! -x ./kubectl ]; then
                        curl -sLO https://dl.k8s.io/release/v1.30.0/bin/linux/amd64/kubectl
                        chmod +x ./kubectl
                    fi
                    ./kubectl config set-cluster in-cluster \
                        --server=https://\$KUBERNETES_SERVICE_HOST:\$KUBERNETES_SERVICE_PORT \
                        --certificate-authority=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt \
                        --embed-certs > /dev/null
                    ./kubectl config set-credentials jenkins \
                        --token=\$(cat /var/run/secrets/kubernetes.io/serviceaccount/token) > /dev/null
                    ./kubectl config set-context in-cluster \
                        --cluster=in-cluster --user=jenkins --namespace=default > /dev/null
                    ./kubectl config use-context in-cluster > /dev/null
                    ./kubectl apply -f k8s-deployment.yaml
                    ./kubectl rollout restart deployment/devops-app
                """
            }
        }
    }
}

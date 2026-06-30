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
                    KCFG=\$(docker run --rm -v /:/host:ro alpine sh -c '
                        for f in /host/root/.kube/config /host/etc/kubernetes/admin.conf /host/home/*/.kube/config; do
                            [ -f "\$f" ] && echo "\$f" && exit 0
                        done
                    ' 2>/dev/null)
                    if [ -z "\$KCFG" ]; then
                        echo "ERROR: No kubeconfig found on host"
                        exit 1
                    fi
                    KCFG_HOST=\$(echo \$KCFG | sed 's|^/host||')
                    HOME_DIR=\$(dirname "\$(dirname "\$KCFG_HOST")")
                    cat k8s-deployment.yaml | docker run -i --rm --network host \\
                        -v "\$KCFG_HOST":"\$KCFG_HOST":ro \\
                        -v "\$HOME_DIR/.minikube":"\$HOME_DIR/.minikube":ro \\
                        -e HOME="\$HOME_DIR" \\
                        bitnami/kubectl:latest apply --validate=false -f -
                    docker run --rm --network host \\
                        -v "\$KCFG_HOST":"\$KCFG_HOST":ro \\
                        -v "\$HOME_DIR/.minikube":"\$HOME_DIR/.minikube":ro \\
                        -e HOME="\$HOME_DIR" \\
                        bitnami/kubectl:latest rollout restart deployment/devops-app
                """
            }
        }
    }
}

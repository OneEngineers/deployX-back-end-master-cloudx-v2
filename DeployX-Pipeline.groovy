pipeline {
    agent any
    options {
        timeout(time: 30, unit: 'MINUTES') // Safety net
        timestamps()
    }

    parameters {
        string(name: 'DEPLOY_ID', defaultValue: '', description: 'ID of the database record')
        string(name: 'PROJECT_NAME', defaultValue: '', description: 'Project name for docker tag')
        string(name: 'GIT_URL', defaultValue: '', description: 'URL of the git repo')
        string(name: 'IMAGE_TAG', defaultValue: 'latest', description: 'Docker image tag')
    }

    environment {
        // Fetch from Jenkins Global Credentials for better security
        DOCKER_HUB_USER = "sayson"
        Docker_Access_Token = "dckr_pat_PMDSMJpQjuROKouMKexvAp_t5sY"
        BACKEND_URL = "http://your-server-ip:8080/api/v1/deploy"
        DOCKER_CREDS = 'dockerhub-credentials-id'
    }

    stages {
        stage('Clone') {
            steps {
                // Ensure we don't have leftover files from previous builds
                cleanWs()
                git url: "${params.GIT_URL}", branch: 'main'
            }
        }

        stage('Build Java') {
            steps {
                sh "chmod +x mvnw"
                sh "./mvnw clean package -DskipTests"
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    def imgName = "${DOCKER_HUB_USER}/${params.PROJECT_NAME}:${params.IMAGE_TAG}"
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDS}") {
                        def img = docker.build(imgName)
                        img.push()
                    }
                }
            }
        }
    }

    post {
        // Use 'always' to send the final signal, or keep success/failure for specific payloads
        success {
            script {
                def subdomain = "${params.PROJECT_NAME}.chanthea.com"
                sh """
                curl -X PATCH ${BACKEND_URL}/${params.DEPLOY_ID}/callback \
                -H "Content-Type: application/json" \
                -d '{"status": "READY", "imageTag": "${params.IMAGE_TAG}", "subdomain": "${subdomain}"}'
                """
            }
        }
        failure {
            sh "curl -X PATCH ${BACKEND_URL}/${params.DEPLOY_ID}/callback -H 'Content-Type: application/json' -d '{\"status\": \"FAILED\"}'"
        }
    }
}
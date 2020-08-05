@Field def parameters
def runJenkinsFile(pipelineConfigPath = 'pipeline.properties') {
    try {
        parameters=preparationStage(pipelineConfigPath)
        buildStage(parameters)
        testStage(parameters)
        deployStage(parameters)
    } catch (Error|Exception e) {
        e.printStackTrace()
    } finally {
        stage('Post stage') {
            finalStage()
        }
    }
}

def preparationStage(pipelineConfigPath){
    stage('Preparation') {
        node(nodeLabel) {
            parameters = initParameters { configFilePath=parameters }
            return parameters
        }
    }
}

def buildStage( parameters ) {
    stage('Build') {
        node(nodeLabel) {
            mavenBuild {
                mavenLabel = parameters['MAVEN_TOOL_LABEL']
                jdkLabel = parameters['JDK_TOOL_LABEL']
            }
            dockerBuild { 
                explicitDockerImageName = parameters['DOCKER_IMAGE_NAME'] 
                dockerImageVersion = parameters['REVISION']
                }
            }
        }
    }
}

def testStage( parameters ) {
    stage('Test') {
        node(nodeLabel) {
            runTests {
                mavenLabel = parameters['MAVEN_TOOL_LABEL']
                jdkLabel = parameters['JDK_TOOL_LABEL']
            }
        }
    }
}

def deployStage(parameters) {
    stage('Deploy') {
        node(nodeLabel) {
            doDeploy { 
                explicitDockerImageName = parameters['DOCKER_IMAGE_NAME'] 
                dockerImageVersion = parameters['REVISION']
            }
        }
    }
}

def finalStage () {
     println "Pipeline execution is completed"
}
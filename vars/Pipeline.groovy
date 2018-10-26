import groovy.json.JsonOutput
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import jenkins.security.*
import hudson.model.* 

def proceed(){
 def deploymentConfig
 def projectConfigJenkinsId = "project-config"
 configFileProvider([configFile(fileId: projectConfigJenkinsId, variable: 'JENKINS_CONFIG')]) { 
    def value = readJSON file: env.JENKINS_CONFIG
    echo '\u27A1 Jenkins Config file content:'
    echo JsonOutput.prettyPrint(value.toString())
    deploymentConfig.projectConfig = value
    deploymentConfig.checkoutDir = env.BUILD_DIR
   }
 stage('[build]'){
      node{ 
	    mavenBuild(deploymentConfig)
	    }
    }
}

def mavenBuild(deploymentConfig)
{
   def result = build(job: deploymentConfig.projectConfig.buildPipeline, parameters: [
          [$class: 'StringParameterValue', name: 'deploymentConfig', value: deploymentConfig.toString()]
  ], propagate: true) 
}
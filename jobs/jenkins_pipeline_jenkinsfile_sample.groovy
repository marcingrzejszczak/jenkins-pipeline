import javaposse.jobdsl.dsl.DslFactory

//  ======= PER REPO VARIABLES =======
String organization = "dsyer"
String gitRepoName = "github-analytics"
String fullGitRepo = "https://github.com/${organization}/${gitRepoName}"
String cronValue = "H H * * 7" //every Sunday - I guess you should run it more often ;)
String gitCredentialsId = 'git'

// TODO: Change to sth like this
// Example of a version with date and time in the name
//String pipelineVersion = '${new Date().format("yyyyMMddHHss")}'
String pipelineVersion = '0.0.1.M1'

//  ======= PER REPO VARIABLES =======

DslFactory dsl = this

dsl.pipelineJob('jenkins-pipeline-jenkinsfile-sample') {
	definition {
		cps {
			script("""
			node {
				stage 'Build and Upload'
				properties [[\$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
							parameters([booleanParam(defaultValue: false, description: 'If Eureka & StubRunner & CF binaries should be redownloaded if already present', name: 'REDOWNLOAD_INFRA'),
										booleanParam(defaultValue: false, description: 'If Eureka & StubRunner binaries should be redeployed if already present', name: 'REDEPLOY_INFRA'),
										string(defaultValue: 'com.example.eureka', description: 'Group Id for Eureka used by tests', name: 'EUREKA_GROUP_ID'),
										string(defaultValue: 'github-eureka', description: 'Artifact Id for Eureka used by tests', name: 'EUREKA_ARTIFACT_ID'),
										string(defaultValue: '0.0.1.M1', description: 'Artifact Version for Eureka used by tests', name: 'EUREKA_VERSION'),
										string(defaultValue: 'com.example.eureka', description: 'Group Id for Stub Runner used by tests', name: 'STUBRUNNER_GROUP_ID'),
										string(defaultValue: 'github-analytics-stub-runner-boot', description: 'Artifact Id for Stub Runner used by tests', name: 'STUBRUNNER_ARTIFACT_ID'),
										string(defaultValue: '0.0.1.M1', description: 'Artifact Version for Stub Runner used by tests', name: 'STUBRUNNER_VERSION')]),
										string(defaultValue: '${pipelineVersion}', description: 'Pipeline version', name: 'PIPELINE_VERSION')]),
										pipelineTriggers([])]

				checkout([\$class: 'GitSCM', branches: [[name: '*/master']],
					userRemoteConfigs: [[credentialsId: '${gitCredentialsId}',
					name: 'origin', url: 'https://github.com/${organization}/${gitRepoName}']]])
				sh '''#!/bin/bash
						set -e

						${readFileFromWorkspace('src/main/bash/pipeline.sh')}
						${readFileFromWorkspace('src/main/bash/test_deploy.sh')}
				'''
				junit '**/surefire-reports/*.xml'
				withCredentials([[\$class: 'UsernamePasswordMultiBinding', credentialsId: '${gitCredentialsId}',
					usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
					sh '''
					  set +x
					  git tag dev/\${PIPELINE_VERSION} -a -m "Development deployment"
					  git push origin --tags
					'''
                }
			}
			node {
				stage 'Deploy to test'
				echo 'Deploying to test'
				stage 'Tests on test'
				echo 'Running tests on test'
				stage 'Deploy to test latest prod version'
				echo 'Deploying to test latest prod version'
				stage 'Tests on test latest prod version'
				echo 'Running tests on test with latest prod version'
			}
			node {
				stage 'Deploy to stage'
				echo 'Deploying to stage'
				stage 'Tests on stage'
				echo 'Running tests on stage'
			}
			node {
				stage 'Deploy to prod'
				echo 'Deploying to prod green instance'
				stage 'Complete switch over'
				echo 'Disabling blue instance'
			}
			""")
		}
	}
}

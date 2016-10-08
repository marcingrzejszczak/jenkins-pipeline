import javaposse.jobdsl.dsl.DslFactory

DslFactory factory = this

factory.job('jenkins-pipeline-seed') {
    scm {
        git {
            remote {
                github('marcingrzejszczak/jenkins-pipeline')
            }
            branch('master')
        }
    }
    wrappers {
        parameters {
            stringParam('REPOS', '', "Provide a comma separated list of repos. " +
                    "If nothing is passed then the default repos will be used")
        }
    }
    steps {
        gradle("clean build")
        dsl {
            external('jobs/jenkins_pipeline_sample*.groovy')
            removeAction('DISABLE')
            removeViewAction('DELETE')
            ignoreExisting(false)
            lookupStrategy('SEED_JOB')
            additionalClasspath([
                'src/main/groovy', 'src/main/resources', 'src/main/bash'
            ].join("\n"))
        }
    }
}

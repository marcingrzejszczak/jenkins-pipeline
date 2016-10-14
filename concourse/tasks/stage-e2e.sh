#!/bin/bash

export ROOT_FOLDER=$( pwd )
export REPO_RESOURCE=repo
export TOOLS_RESOURCE=tools
export VERSION_RESOURCE=version
export OUTPUT_RESOURCE=out

echo "Root folder is [${ROOT_FOLDER}]"
echo "Repo resource folder is [${REPO_RESOURCE}]"
echo "Tools resource folder is [${TOOLS_RESOURCE}]"
echo "Version resource folder is [${VERSION_RESOURCE}]"

source ${ROOT_FOLDER}/${TOOLS_RESOURCE}/concourse/tasks/pipeline.sh

echo "Testing the built application on stage environment"
cd ${ROOT_FOLDER}/${REPO_RESOURCE}

echo "Retrieving group and artifact id - it can take a while..."
retrieveGroupId
retrieveArtifactId
projectGroupId=$( retrieveGroupId )
projectArtifactId=$( retrieveArtifactId )
mkdir target
logInToCf ${REDOWNLOAD_INFRA} ${CF_STAGE_USERNAME} ${CF_STAGE_PASSWORD} ${CF_STAGE_ORG} ${CF_STAGE_SPACE} ${CF_API_URL}
propagatePropertiesForTests ${projectArtifactId}
readTestPropertiesFromFile

echo "Retrieved application and stub runner urls"
. ${SCRIPTS_OUTPUT_FOLDER}/stage_e2e.sh

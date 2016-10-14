#!/bin/bash

export ROOT_FOLDER=$( pwd )
export REPO_RESOURCE=repo

M2_LOCAL=${ROOT_FOLDER}/m2/rootfs/opt/m2
echo "Changing the maven local to [${M2_LOCAL}]"
export MAVEN_ARGS="-Dmaven.repo.local=${M2_LOCAL}"

if [ "$1" == "init" ]; then
	mkdir -p ${M2_LOCAL}
fi

cd ${REPO_RESOURCE}
./mvnw clean verify install ${MAVEN_ARGS}
cd ${ROOT_FOLDER}/m2
tar -C rootfs -cf rootfs.tar .
mv rootfs.tar ${ROOT_FOLDER}/to-push/

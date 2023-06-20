#!/bin/bash
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker volume rm con_org_volume
docker pull conorgdocker/con-org-app
docker volume create --name con_org_volume
#!/bin/bash
docker volume create --name con_org_volume
docker pull conorgdocker/con-org-app
docker container run --mount source=con_org_volume,target=/root -it conorgdocker/con-org-app

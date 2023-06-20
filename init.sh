#!/bin/bash
docker container run --mount source=con_org_volume,target=/root -it conorgdocker/con-org-app

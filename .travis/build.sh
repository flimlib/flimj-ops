#!/bin/sh
curl -fsLO https://raw.githubusercontent.com/scijava/scijava-scripts/master/travis-build.sh
sh travis-build.sh $encrypted_e99a9fb5640d_key $encrypted_e99a9fb5640d_iv

#!/bin/zsh
source ~/.zshrc

cd /Users/mrhaki/Projects/blogger-extras
sdk env

./gradlew run
./deploy.sh

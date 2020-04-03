#!/bin/zsh
source ~/.zshrc
sdk use java 8.0.242.hs-adpt

cd /Users/mrhaki/Projects/blogger-extras

./gradlew run
./deploy.sh

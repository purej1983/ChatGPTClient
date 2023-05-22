# ChatGPTClient

## Introduction
The Project is try to demo an android app connect the Chat GPT api

## Pre-requisites
1. Api Key to openapi([howto?](APIKeyHowTo.md))
2. Android studio

## Getting Started
1. Install Android Studio if dont have it
2. clone the project 
3. in the project, there is a file called apikey.properties.samples in root directory, rename it as apikey.properties, then put the api key value get in pre-requisites and save
4. Build and run the project

## awareness
The http client default time out is 10s. However, some response will take longer than 10s. So I have change the default timeout to 20s.
The config is located  in [AppModule](app/src/main/java/com/thomaslam/chatgptclient/di/AppModule.kt#L42)

## Debug Tools
1. Postman collections ([howto?](PostManHowTo.md))
2. Room DB data validate ([howto?](DatabaseInspection.md))


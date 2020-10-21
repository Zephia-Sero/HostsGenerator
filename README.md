# HostsGenerator
Uses DuckDuckGo and Wolfram|Alpha APIs to find many subdomains under a user-supplied domain name, and creates a Windows hosts file for mass-blocking of said domains.

## Required libraries to build:
  - commons-logging-1.2.jar
  - httpcore-4.0.1.jar
  - httpclient-4.0.1.jar
  - commons-codec-1.3.jar
  - jsoup-1.13.1.jar

Contains lots of code from https://github.com/belsinb/Wolfram so thank you <3

## Using HostsGenerator
Note that, in order to create your own hosts file using HostsGenerator.jar (or by building it yourself), you will need a Wolfram|Alpha API AppID which requires creating an account here: https://products.wolframalpha.com/api/

Once you have that, create a file titled "appid" (no extension) next to the jar file, and put your App ID on the first line. Anything else in that file will be ignored.

Alternatively, if you run the jar file, it will attempt to create the appid file on its own (but it will be empty).

### Can I use any of the source for my projects?
Any usage of the source is permitted, within the terms of the included libraries, DuckDuckGo, and Wolfram|Alpha itself.

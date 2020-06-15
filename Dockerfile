FROM adoptopenjdk:11-jdk-hotspot
LABEL maintainer="Stefan B <spiderbiggen@gmail.com>"

ENV SDK_TOOLS "6200805"
ENV ANDROID_HOME "/opt/sdk"
ENV PATH $PATH:${ANDROID_HOME}/cmdline-tools/tools/bin:${ANDROID_HOME}/platform-tools

# Install required dependencies
RUN apt-get update && \
    apt-get install -y wget git unzip && \
    rm -rf /var/lib/apt/lists/*

# Download and extract Android Tools
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-${SDK_TOOLS}_latest.zip -O /tmp/tools.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    unzip -qq /tmp/tools.zip -d ${ANDROID_HOME}/cmdline-tools && \
    rm -v /tmp/tools.zip

# Install SDK Packages
RUN mkdir -p ~/.android/ && touch ~/.android/repositories.cfg && \
    yes | sdkmanager --sdk_root=${ANDROID_HOME} --licenses && \
    sdkmanager --sdk_root=${ANDROID_HOME} "platform-tools" "extras;android;m2repository" "extras;google;m2repository" "extras;google;instantapps" && \
    sdkmanager --update

RUN useradd -ms /bin/bash android

ENV PATH $PATH:${ANDROID_HOME}/build-tools/29.0.3
ENV HOME /home/android

# Install SDK Packages
RUN sdkmanager "build-tools;28.0.3" "build-tools;29.0.3" "platforms;android-29" "platforms;android-28"

USER android
WORKDIR /home/android
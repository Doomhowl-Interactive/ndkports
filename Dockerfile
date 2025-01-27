FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    sdkmanager \
    cmake \
    curl \
    ninja-build \
    openjdk-17-jdk \
    python3-pip \
    zip && \
    apt-get clean

RUN pip3 install meson

RUN sdkmanager --install "ndk;r27c"
RUN sdkmanager --install "platforms;android-35"

WORKDIR /worker
COPY . .

ENTRYPOINT ["./gradlew", "--no-daemon", "--gradle-user-home=.gradle_home", "--stacktrace", "-PndkPath=/opt/android-sdk/ndk/r27c"]
CMD ["-Prelease", "clean", "build"]

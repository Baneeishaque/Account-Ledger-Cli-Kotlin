FROM gitpod/workspace-full

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java 15.0.2-open && sdk default java 15.0.2-open"

RUN sudo apt-get update \
 && sudo apt-get install -y \
    libncurses5 \
 && sudo rm -rf /var/lib/apt/lists/*
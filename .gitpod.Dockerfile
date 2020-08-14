FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java 15.ea.35-open && sdk default java 15.ea.35-open"

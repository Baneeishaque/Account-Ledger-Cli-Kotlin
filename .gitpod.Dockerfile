FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh"
RUN bash -c "sdk install java 15.ea.35-open"
RUN bash -c "sdk default java 15.ea.35-open"
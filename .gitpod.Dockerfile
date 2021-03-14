FROM gitpod/workspace-full

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java 15.0.2-open && sdk default java 15.0.2-open && sdk install java 21.0.0.r11-grl && /home/gitpod/.sdkman/candidates/java/21.0.0.r11-grl/bin/gu install native-image"

RUN sudo apt-get update \
 && sudo apt-get install -y \
     libncurses5 \
 && sudo rm -rf /var/lib/apt/lists/*

RUN curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | sudo bash

RUN sudo apt-get update \
 && sudo apt-get install -y \
     zsh python-pygments fd-find fzf ripgrep silversearcher-ag git-extras git-flow git-lfs httpie autojump nmap imagemagick \
 && sudo rm -rf /var/lib/apt/lists/*

RUN cd $HOME \
 && wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | zsh

RUN sed -i 's/_THEME=\"robbyrussell\"/_THEME=\"xiong-chiamiov-plus\"/g' ~/.zshrc

RUN git clone "https://github.com/datasift/gitflow.git" \
 && cd gitflow \
 && sudo ./install.sh \
 && cd .. \
 && sudo rm -rf gitflow

RUN sudo pip3 install thefuck

RUN cd $HOME \
 && git clone "https://github.com/davidde/git.git" ".oh-my-zsh/custom/plugins/git"

RUN cd $HOME \
 && wget "https://gist.githubusercontent.com/oshybystyi/475ee7768efc03727f21/raw/4bfd57ef277f5166f3070f11800548b95a501a19/git-auto-status.plugin.zsh" -P ".oh-my-zsh/custom/plugins/git-auto-status/"

RUN wget "http://kassiopeia.juls.savba.sk/~garabik/software/grc/grc_1.12-1_all.deb" \
 && sudo dpkg -i grc_1.12-1_all.deb \
 && rm grc_1.12-1_all.deb

RUN cd $HOME \
 && git clone "https://github.com/gradle/gradle-completion" ".oh-my-zsh/custom/plugins/gradle-completion/"

RUN cd $HOME \
 && git clone "https://github.com/bobthecow/git-flow-completion" ".oh-my-zsh/custom/plugins/git-flow-completion/"

RUN cd $HOME \
 && wget -P ".oh-my-zsh/custom/plugins/git-completion/" "https://raw.githubusercontent.com/git/git/master/contrib/completion/git-completion.bash" \
 && wget -P ".oh-my-zsh/custom/plugins/git-completion/" "https://raw.githubusercontent.com/git/git/master/contrib/completion/git-completion.tcsh" \
 && wget -O ".oh-my-zsh/custom/plugins/git-completion/git-completion.plugin.zsh" "https://raw.githubusercontent.com/git/git/master/contrib/completion/git-completion.zsh" \
 && wget -P ".oh-my-zsh/custom/plugins/git-completion/" "https://raw.githubusercontent.com/git/git/master/contrib/completion/git-prompt.sh"

RUN sed -i 's/plugins=(git)/plugins=(git gradle gradle-completion adb sdk common-aliases dircycle dirhistory dirpersist history copydir copyfile autojump fd git-completion git-auto-status git-prompt gitfast gitignore git-flow git-flow-completion git-flow-avh git-hubflow git-lfs git-extras last-working-dir per-directory-history perms wd safe-paste thefuck systemadmin scd pj magic-enter man command-not-found jump timer colored-man-pages jsontools grc colorize ripgrep httpie sprunge nmap transfer universalarchive catimg extract)/g' ~/.zshrc

CMD ["/usr/bin/zsh"]

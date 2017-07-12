FROM java/8-jdk-alpine
MAINTAINER tiago.pires@mindera.com

ENV VERTICLE_FILE inspiring-career-fat.jar

ENV VERTICLE_HOME /service

ENV CONFIG_DIR /config

COPY config/supervisord.conf /etc/supervisord.conf
COPY bootstrap/target/$VERTICLE_FILE $VERTICLE_HOME/
COPY script/docker_start $VERTICLE_HOME/docker_start

RUN ln -sf $CONFIG_DIR $VERTICLE_HOME/config && \
    chmod +x $VERTICLE_HOME/docker_start

WORKDIR $VERTICLE_HOME

EXPOSE 8080

ENTRYPOINT ["supervisord", "--nodaemon", "--configuration", "/etc/supervisord.conf"]

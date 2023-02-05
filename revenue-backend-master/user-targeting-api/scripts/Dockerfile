FROM 536123028970.dkr.ecr.ap-south-1.amazonaws.com/java:openjdk-8u151-alpine
ARG profile
ENV profile $profile
ENV JAR_NAME=user-targeting-api-0.0.1.jar
ENV KEY_STORE=wynkkeystore.jks
ENV JAVA_OPTS="-Duser.timezone=Asia/Calcutta -Dfile.encoding=UTF-8 -Dspring.profiles.active=$profile"
ENV JMX_OPTS="-Xmx2g -XX:+HeapDumpOnOutOfMemoryError -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:MaxTenuringThreshold=1 -XX:+AggressiveOpts -XX:+UseFastAccessorMethods"
ADD $JAR_NAME /opt/
ADD $KEY_STORE /opt/
WORKDIR /opt/
RUN mkdir -p /data
RUN echo $JAVA_OPTS
EXPOSE 8081
CMD exec java $JMX_OPTS $JAVA_OPTS -jar /opt/$JAR_NAME
FROM eclipse-temurin:8-jre

WORKDIR /app
RUN mkdir -p /app/logs /home/ruoyi/uploadPath
COPY ruoyi-admin/target/ruoyi-admin.jar /app/ruoyi-admin.jar

ENV TZ=Asia/Shanghai \
    SERVER_PORT=18080 \
    RUOYI_PROFILE=/home/ruoyi/uploadPath \
    SPRING_PROFILES_ACTIVE=druid

EXPOSE 18080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
  CMD wget -qO- "http://127.0.0.1:${SERVER_PORT}/health" >/dev/null || exit 1

ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "/app/ruoyi-admin.jar"]

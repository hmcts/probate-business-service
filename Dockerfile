
ARG APP_INSIGHTS_AGENT_VERSION=2.3.1
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-debug-1.0
LABEL maintainer="https://github.com/hmcts/probate-business-service"
COPY lib/applicationinsights-agent-2.3.1.jar lib/AI-Agent.xml /opt/app/

COPY build/libs/business-service.jar /opt/app/


EXPOSE 8080
CMD [ "business-service.jar" ]



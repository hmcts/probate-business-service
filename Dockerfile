ARG APP_INSIGHTS_AGENT_VERSION=2.5.1-BETA
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.2

COPY lib/AI-Agent.xml /opt/app/
COPY lib/applicationinsights-agent-2.5.1-BETA.jar /opt/app/

COPY build/libs/business-service.jar /opt/app/

EXPOSE 8080
CMD [ "business-service.jar" ]

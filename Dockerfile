ARG APP_INSIGHTS_AGENT_VERSION=2.3.1
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.0

COPY src/lib/applicationinsights-agent-2.3.1.jar src/lib/AI-Agent.xml /opt/app/

COPY build/libs/business-service.jar /opt/app/

EXPOSE 8080
CMD [ "business-service.jar" ]

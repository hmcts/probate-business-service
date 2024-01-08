 # renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
ARG APP_INSIGHTS_AGENT_VERSION=3.4.19
FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY build/libs/business-service.jar /opt/app/
COPY lib/applicationinsights.json /opt/app/

EXPOSE 8080
CMD [ "business-service.jar" ]

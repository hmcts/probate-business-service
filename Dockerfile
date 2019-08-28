FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.0


COPY lib/applicationinsights-agent-2.3.1.jar lib/AI-Agent.xml /opt/app/

COPY build/libs/business-service.jar /opt/app/

EXPOSE 8080
CMD [ "business-service.jar" ]



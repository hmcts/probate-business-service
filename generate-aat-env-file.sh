#!/usr/bin/env bash

set -eu

# Clear the file:
> .aat-env

# Safe urls
{
  echo "AUTH_PROVIDER_SERVICE_CLIENT_BASEURL=http://rpe-service-auth-provider-aat.service.core-compute-aat.internal";
  echo "SERVICE_AUTH_PROVIDER_BASE_URL=http://rpe-service-auth-provider-aat.service.core-compute-aat.internal";
  echo "AUTH_S2S_CLIENT_ID=probate_backend";
  echo "IDAM_SERVICE_HOST=https://idam-api.aat.platform.hmcts.net";
  echo "IDAM_SECRET=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name idam-secret-probate)";
  echo "S2S_AUTH_TOTP_SECRET=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name s2sAuthTotpSecret)";
  echo "IDAM_CLIENT_NAME=probate";
  echo "IDAM_REDIRECT_URL=https://probate-frontend-aat.service.core-compute-aat.internal/oauth2/callback";
  echo "AUTH_TOKEN_EMAIL=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name authTokenEmail)";
  echo "AUTH_TOKEN_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name authTokenPassword)";
  echo "AUTH_IDAM_CLIENT_BASEURL=https://idam-api.aat.platform.hmcts.net";
  echo "IDAM_CLIENT_ID=probate";
  echo "AUTH2_CLIENT_SECRET=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name idam-secret-probate)";
  echo "PAYMENT_CASEWORKER_USERNAME=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name payCaseWorkerUser)";
  echo "PAYMENT_CASEWORKER_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name payCaseWorkerPass)";
  echo "SCHEDULER_CASEWORKER_USERNAME=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name schedulerCaseWorkerUser)";
  echo "SCHEDULER_CASEWORKER_PASSWORD=$(az keyvault secret show --vault-name probate-aat -o tsv --query value --name schedulerCaseWorkerPass)";
  echo "SUBMIT_SERVICE_API_URL=http://probate-submit-service-aat.service.core-compute-aat.internal";
  echo "BACK_OFFICE_API_URL=http://probate-back-office-aat.service.core-compute-aat.internal";
  echo "PDF_SERVICE_URL=http://cmc-pdf-service-aat.service.core-compute-aat.internal";
} >> .aat-env

#provider "vault" {
  //  # It is strongly recommended to configure this provider through the
  //  # environment variables described above, so that each user can have
  //  # separate credentials set in the environment.
  //  #
  //  # This will default to using $VAULT_ADDR
  //  # But can be set explicitly
#  address = "https://vault.reform.hmcts.net:6200"
#}


provider "azurerm" {
  version = "1.22.1"
}

#s2s vault
data "azurerm_key_vault" "s2s_vault" {
  name = "s2s-${local.local_env}"
  resource_group_name = "rpe-service-auth-provider-${local.local_env}"
}

locals {
  aseName = "core-compute-${var.env}"
  app_full_name = "${var.product}-${var.microservice}"

  local_env = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"
  local_ase = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "core-compute-aat" : "core-compute-saat" : local.aseName}"

  previewVaultName = "${var.raw_product}-aat"
  nonPreviewVaultName = "${var.raw_product}-${var.env}"
  vaultName = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName}"

}

data "azurerm_key_vault" "probate_key_vault" {
  name = "${local.vaultName}"
  resource_group_name = "${local.vaultName}"
}

data "azurerm_key_vault_secret" "probate_notify_invite_apikey" {
  name = "probate-notify-invite-apikey"
  key_vault_id = "${data.azurerm_key_vault.probate_key_vault.id}"
}

data "azurerm_key_vault_secret" "business_services_notify_invitedata_templateId" {
  name = "business-services-notify-invitedata-templateId"
  key_vault_id = "${data.azurerm_key_vault.probate_key_vault.id}"
}

data "azurerm_key_vault_secret" "business_services_notify_pin_templateId" {
  name = "business-services-notify-pin-templateId"
  key_vault_id = "${data.azurerm_key_vault.probate_key_vault.id}"
}

data "azurerm_key_vault_secret" "business_services_notify_invitedata_bilingualTemplateId" {
  name      = "business-services-notify-invitedata-bilingualTemplateId"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "business_services_notify_pin_bilingualTemplateId" {
  name      = "business-services-notify-pin-bilingualTemplateId"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "s2s_key" {
  name      = "microservicekey-probate-backend"
  key_vault_id = "${data.azurerm_key_vault.s2s_vault.id}"
}

resource "azurerm_key_vault_secret" "s2s-secret-for-probate-backoffice" {
  name         = "s2s-probate-backend"
  value        = "${data.azurerm_key_vault_secret.s2s_key.value}"
  key_vault_id = "${data.azurerm_key_vault.probate_key_vault.id}"
}

module "probate-business-service" {
  source = "git@github.com:hmcts/cnp-module-webapp?ref=master"
  product = "${var.product}-${var.microservice}"
  location = "${var.location}"
  env = "${var.env}"
  ilbIp = "${var.ilbIp}"
  is_frontend  = false
  subscription = "${var.subscription}"
  asp_name     = "${var.asp_name}"
  capacity     = "${var.capacity}"
  common_tags  = "${var.common_tags}"
  asp_rg       = "${var.asp_rg}"
  appinsights_instrumentation_key = "${var.appinsights_instrumentation_key}"

  app_settings = {

	  // Logging vars
    REFORM_TEAM = "${var.product}"
    REFORM_SERVICE_NAME = "${var.microservice}"
    REFORM_ENVIRONMENT = "${var.env}"


    DEPLOYMENT_ENV= "${var.deployment_env}"
    //JAVA_OPTS = "${local.java_proxy_variables}"


    SERVICES_PERSISTENCE_BASEURL = "${var.business_services_persistence_baseUrl}"
    SERVICES_NOTIFY_APIKEY = "${data.azurerm_key_vault_secret.probate_notify_invite_apikey.value}"
    SERVICES_NOTIFY_INVITEDATA_TEMPLATEID = "${data.azurerm_key_vault_secret.business_services_notify_invitedata_templateId.value}"
    SERVICES_NOTIFY_INVITEDATA_INVITELINK = "${var.business_services_notify_invitedata_inviteLink}"
    SERVICES_NOTIFY_PIN_TEMPLATEID = "${data.azurerm_key_vault_secret.business_services_notify_pin_templateId.value}"
    SERVICES_PDF_SERVICE_URL = "${var.pdf_service_url}"
    DOCUMENT_MANAGEMENT_URL =  "${var.evidence_management_host}"
    SERVICES_AUTH_PROVIDER_BASEURL = "${var.s2s_service_api}"
    SERVICES_AUTH_PROVIDER_TOTP_SECRET = "${data.azurerm_key_vault_secret.s2s_key.value}"

    java_app_name = "${var.microservice}"
    LOG_LEVEL = "${var.log_level}"
    //Testing = "TESTING"  //remove json output

  }
}


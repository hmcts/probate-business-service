provider "vault" {
  //  # It is strongly recommended to configure this provider through the
  //  # environment variables described above, so that each user can have
  //  # separate credentials set in the environment.
  //  #
  //  # This will default to using $VAULT_ADDR
  //  # But can be set explicitly
  address = "https://vault.reform.hmcts.net:6200"
}


locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
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
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "business_services_notify_invitedata_templateId" {
  name = "business-services-notify-invitedata-templateId"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "business_services_notify_pin_templateId" {
  name = "business-services-notify-pin-templateId"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}


module "probate-business-service" {
  source = "git@github.com:hmcts/moj-module-webapp.git?ref=master"
  product = "${var.product}-${var.microservice}"
  location = "${var.location}"
  env = "${var.env}"
  ilbIp = "${var.ilbIp}"
  is_frontend  = false
  subscription = "${var.subscription}"
  asp_name     = "${var.product}-${var.env}"
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
    java_app_name = "${var.microservice}"
    LOG_LEVEL = "${var.log_level}"
    //ROOT_APPENDER = "JSON_CONSOLE"  //remove json output

  }
}


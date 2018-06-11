resource "azurerm_resource_group" "resource_group" {
  name     = "${var.product}-${var.microservice}-${var.env}"
  location = "UK South"
}

provider "vault" {
  //  # It is strongly recommended to configure this provider through the
  //  # environment variables described above, so that each user can have
  //  # separate credentials set in the environment.
  //  #
  //  # This will default to using $VAULT_ADDR
  //  # But can be set explicitly
  address = "https://vault.reform.hmcts.net:6200"
}

data "vault_generic_secret" "probate_notify_invite_apikey" {
  path = "secret/${var.vault_section}/probate/probate_notify_invite_apikey"
}

data "vault_generic_secret" "business_services_notify_invitedata_templateId" {
  path = "secret/${var.vault_section}/probate/business_services_notify_invitedata_templateId"
}

data "vault_generic_secret" "business_services_notify_pin_templateId" {
  path = "secret/${var.vault_section}/probate/business_services_notify_pin_templateId"
}

locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"

  //java_proxy_variables: "-Dhttp.proxyHost=${var.proxy_host} -Dhttp.proxyPort=${var.proxy_port} -Dhttps.proxyHost=${var.proxy_host} -Dhttps.proxyPort=${var.proxy_port}"

  //probate_frontend_hostname = "probate-frontend-aat.service.core-compute-aat.internal"
  previewVaultName    = "pro-business-ser"
  nonPreviewVaultName = "pro-business-ser-${var.env}"
  vaultName           = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName}"
  nonPreviewVaultUri  = "${module.probate-business-service-vault.key_vault_uri}"
  previewVaultUri     = "https://pro-business-ser-aat.vault.azure.net/"
  vaultUri            = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultUri : local.nonPreviewVaultUri}"
}

module "probate-business-service" {
  source              = "git@github.com:hmcts/moj-module-webapp.git?ref=infra_versions"
  product             = "${var.product}-${var.microservice}-${var.env}"
  resource_group_name = "${azurerm_resource_group.resource_group.name}"
  location            = "${var.location}"
  env                 = "${var.env}"
  ilbIp               = "${var.ilbIp}"
  subscription        = "${var.subscription}"
  asp_id              = "${data.terraform_remote_state.probate_infrastructure.aspA}"
  deploymentTag       = "${var.product}"

  app_settings = {
    // Logging vars
    REFORM_TEAM         = "${var.product}"
    REFORM_SERVICE_NAME = "${var.microservice}"
    REFORM_ENVIRONMENT  = "${var.env}"

    DEPLOYMENT_ENV = "${var.deployment_env}"

    //JAVA_OPTS = "${local.java_proxy_variables}"

    SERVICES_PERSISTENCE_BASEURL          = "${var.business_services_persistence_baseUrl}"
    SERVICES_NOTIFY_APIKEY                = "${data.vault_generic_secret.probate_notify_invite_apikey.data["value"]}"
    SERVICES_NOTIFY_INVITEDATA_TEMPLATEID = "${data.vault_generic_secret.business_services_notify_invitedata_templateId.data["value"]}"
    SERVICES_NOTIFY_INVITEDATA_INVITELINK = "${var.business_services_notify_invitedata_inviteLink}"
    SERVICES_NOTIFY_PIN_TEMPLATEID        = "${data.vault_generic_secret.business_services_notify_pin_templateId.data["value"]}"
    java_app_name                         = "${var.microservice}"
    LOG_LEVEL                             = "${var.log_level}"

    //ROOT_APPENDER = "JSON_CONSOLE"  //remove json output
  }
}

module "probate-business-service-vault" {
  source                  = "git@github.com:hmcts/moj-module-key-vault?ref=master"
  name                    = "${local.vaultName}"
  product                 = "${var.product}"
  env                     = "${var.env}"
  tenant_id               = "${var.tenant_id}"
  object_id               = "${var.jenkins_AAD_objectId}"
  resource_group_name     = "${azurerm_resource_group.resource_group.name}"
  product_group_object_id = "33ed3c5a-bd38-4083-84e3-2ba17841e31e"
}

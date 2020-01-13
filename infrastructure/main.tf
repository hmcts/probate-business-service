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

locals {
  aseName       = "core-compute-${var.env}"
  app_full_name = "${var.product}-${var.microservice}"

  local_env = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"
  local_ase = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "core-compute-aat" : "core-compute-saat" : local.aseName}"

  previewVaultName    = "${var.raw_product}-aat"
  nonPreviewVaultName = "${var.raw_product}-${var.env}"
  vaultName           = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName}"
}

data "azurerm_key_vault" "probate_key_vault" {
  name                = "${local.vaultName}"
  resource_group_name = "${local.vaultName}"
}

data "azurerm_key_vault_secret" "probate_notify_invite_apikey" {
  name      = "probate-notify-invite-apikey"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "business_services_notify_invitedata_templateId" {
  name      = "business-services-notify-invitedata-templateId"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "business_services_notify_pin_templateId" {
  name      = "business-services-notify-pin-templateId"
  vault_uri = "${data.azurerm_key_vault.probate_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "s2s_key" {
  name      = "microservicekey-probate-backend"
  vault_uri = "https://s2s-${local.local_env}.vault.azure.net/"
}

resource "azurerm_key_vault_secret" "s2s-secret-for-probate-backoffice" {
  name         = "s2s-probate-backend"
  value        = "${data.azurerm_key_vault_secret.s2s_key.value}"
  key_vault_id = "${data.azurerm_key_vault.probate_key_vault.id}"
}

resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.microservice}-${var.env}"
  location = "${var.location}"

  tags = "${var.common_tags}"
}

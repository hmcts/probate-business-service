provider "azurerm" {
  features {}
}

#s2s vault
data "azurerm_key_vault" "s2s_vault" {
  name = "s2s-${var.env}"
  resource_group_name = "rpe-service-auth-provider-${var.env}"
}

locals {
  vaultName = "${var.product}-${var.env}"
}

data "azurerm_key_vault" "probate_key_vault" {
  name = local.vaultName
  resource_group_name = local.vaultName
}

data "azurerm_key_vault_secret" "s2s_key" {
  name      = "microservicekey-probate-backend"
  key_vault_id = data.azurerm_key_vault.s2s_vault.id
}

resource "azurerm_key_vault_secret" "s2s-secret-for-probate-backoffice" {
  name         = "s2s-probate-backend"
  value        = data.azurerm_key_vault_secret.s2s_key.value
  key_vault_id = data.azurerm_key_vault.probate_key_vault.id
}


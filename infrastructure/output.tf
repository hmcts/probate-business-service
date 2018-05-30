output "vaultUri" {
  value = "${module.probate-business-service-vault.key_vault_uri}"
}

output "vaultName" {
  value = "${module.probate-business-service-vault.key_vault_name}"
}

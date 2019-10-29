// Infrastructural variables
variable "product" {} //get from jenkins file

variable "raw_product" {
  default = "probate" // jenkins-library overrides product for PRs and adds e.g. pr-118-cmc
}

variable "microservice" {
  default = "business-service"
}

variable "location" {
  default = "UK South"
}

variable "env" {
  type = "string"
}

variable "ilbIp" {}

variable "deployment_env" {
  type = "string"
}

variable "tenant_id" {
  description = "(Required) The Azure Active Directory tenant ID that should be used for authenticating requests to the key vault. This is usually sourced from environemnt variables and not normally required to be specified."
}

variable "appinsights_instrumentation_key" {
  description = "Instrumentation key of the App Insights instance this webapp should use. Module will create own App Insights resource if this is not provided"
  default     = ""
}

variable "component" {
  default = "backend"
}

variable "subscription" {}

variable "vault_section" {
  type = "string"
}

// CNP settings
variable "jenkins_AAD_objectId" {
  type        = "string"
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "business_services_persistence_baseUrl" {
  default = "/"
}

variable "business_services_notify_invitedata_inviteLink" {
  default = "./executors/invitation/"
}

variable "pdf_service_url" {
  default = "/"
}

variable "log_level" {
  type = "string"
}

variable "capacity" {
  default = "1"
}

variable "common_tags" {
  type = "map"
}

variable "asp_rg" {

}

variable "asp_name" {
  
}

variable "evidence_management_host" {
  type = "string"
}

variable "s2s_service_api" {
  type = "string"
}

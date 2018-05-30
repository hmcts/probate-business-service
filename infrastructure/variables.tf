// Infrastructural variables

variable "product" {
  default = "probate"
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

variable "ilbIp" { }

variable "deployment_env" {
  type = "string"
}

variable "tenant_id" {
  description = "(Required) The Azure Active Directory tenant ID that should be used for authenticating requests to the key vault. This is usually sourced from environemnt variables and not normally required to be specified."
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
  type                        = "string"
  description                 = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}


variable "outbound_proxy" {
  default = "http://proxyout.reform.hmcts.net:8080/"
}

variable "no_proxy" {
  default = "localhost,127.0.0.0/8,127.0.0.1,127.0.0.1*,local.home,reform.hmcts.net,*.reform.hmcts.net,betaDevBprobateApp01.reform.hmcts.net,betaDevBprobateApp02.reform.hmcts.net,betaDevBccidamAppLB.reform.hmcts.net,*.internal,*.platform.hmcts.net"
}

variable "business_services_persistence_invitedata_url" {
  default = "./invitedata"
}

variable "business_services_persistence_formdata_url" {
  default = "./formdata"
}

variable "business_services_notify_invitedata_inviteLink" {
  default = "./executors/invitation/"
}

variable "proxy_host" {
  type = "string"
}

variable "proxy_port" {
  type = "string"
}

variable "log_level" {
  type = "string"
}




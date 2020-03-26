// Infrastructural variables
variable "product" {}  //get from jenkins file

variable "raw_product" {
  default = "probate" // jenkins-library overrides product for PRs and adds e.g. pr-118-cmc
}

variable "env" {
  type = "string"
}

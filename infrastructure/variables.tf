// Infrastructural variables
variable "product" {}  //get from jenkins file

variable "env" {
  type = "string"
}

variable "common_tags" {
  type = map(string)
}

// Infrastructural variables
variable "product" {}  //get from jenkins file

variable "env" {}

variable "common_tags" {
  type = map(string)
}

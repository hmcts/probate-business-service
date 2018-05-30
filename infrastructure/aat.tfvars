env = "aat"
deployment_env = "preprod"

vault_section = "preprod"

packages_environment = "preprod"
packages_version = "3.0.0"

probate_frontend_hostname = "probate-frontend-aat.service.core-compute-aat.internal"

outbound_proxy = ""

proxy_host = "proxyout.reform"
proxy_port = "8080"


log_level = "INFO"

business_services_persistence_invitedata_url  = "http://betaPreProdprobateApp01.reform.hmcts.net:4103/invitedata"
business_services_persistence_formdata_url  = "http://betaPreProdprobateApp01.reform.hmcts.net:4103/formdata"
business_services_notify_invitedata_inviteLink = "https://probate-frontend-aat.service.core-compute-aat.internal/executors/invitation/"
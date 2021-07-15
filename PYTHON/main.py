from xiaoe_client import XiaoeClient

BIZ_URL = "https://api.xiaoe-tech.com/xe.user.batch.get/1.0.0"

data = {
    "page": 1,
    "page_size": 2
}

client = XiaoeClient()
client.request('post', BIZ_URL, data)
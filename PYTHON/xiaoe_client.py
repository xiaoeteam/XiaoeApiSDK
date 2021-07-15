# %%
from token_manager import TokenManager
import json
import requests

APP_ID = "appXXXXXXXXXXXXX"
CLIENT_ID = "xopXXXXXXXXXXXXX"
SECRET_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
GRANT_TYPE = "client_credential" # 固定值 硬编码即可

# %%

MANAGER = TokenManager(APP_ID, CLIENT_ID, SECRET_KEY, GRANT_TYPE)

# 调用小鹅client实现接口操作
class XiaoeClient:
    # user_params 需要传dict
    def request(self, method, url, user_params={}):
        access_token = MANAGER.token()
        user_params["access_token"] = access_token
        payload = json.dumps(user_params)
        print(payload)
        headers = {
            'Content-Type': 'application/json'
        }
        response = requests.request(method, url, headers=headers, data=payload)

        print(response.text)
        return response.text

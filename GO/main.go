package main

import (
	"encoding/json"
	"fmt"
	"github.com/faabiosr/cachego/file"
	"net/http"
	"os"
	"strings"
	"xiaoe.demo/demo"
)

const (
	XiaoEAppId     = "appxxxx"           // 店铺app_id
	XiaoEClientId  = "xxxxxx"            // 店铺client_id
	XiaoEAppSecret = "xxxxxx"            // 店铺client_sercet
	XiaoEGrantType = "client_credential" // 固定不变

	GetUserListOpenApi = "https://api.xiaoe-tech.com/xe.user.batch.get/1.0.0"
)

func main() {
	xiaoE := &demo.DefaultAccessTokenManager{
		Id:   XiaoEAppId,
		Name: "access_token",
		GetRefreshRequestFunc: func() *http.Request {
			params := make(map[string]string)
			params["app_id"] = XiaoEAppId
			params["client_id"] = XiaoEClientId
			params["secret_key"] = XiaoEAppSecret
			params["grant_type"] = XiaoEGrantType

			str, err := json.Marshal(params)
			if err != nil {
				fmt.Println(err)
			}
			payload := strings.NewReader(string(str))
			req, err := http.NewRequest(http.MethodGet, demo.ServerUrl, payload)
			if err != nil {
				return nil
			}

			return req
		},
		// os.TempDir() - 设置缓存路径，默认为系统默认缓存路径,为了方便查找建议修改路径
		Cache: file.New(os.TempDir()),
	}

	// 小鹅云 客户端
	xiaoEClient := demo.NewClient(xiaoE)

	// 调用示例 不需要传入access_token
	UserListParams := make(map[string]interface{})
	UserListParams["page"] = 1
	UserListParams["page_size"] = 2

	resp, err := xiaoEClient.CurlDo(http.MethodPost, GetUserListOpenApi, UserListParams)
	if err != nil {
		fmt.Println("err: ", err)
	}
	fmt.Println(string(resp))
}

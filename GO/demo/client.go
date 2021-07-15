package demo

import (
	"encoding/json"
	"io/ioutil"
	"net/http"
	"net/url"
	"strings"
)

const (
	contentTypeApplicationJson = "application/json"
)

var (
	ServerUrl       = "https://api.xiaoe-tech.com/token"
)

// NewClient
func NewClient(AccessTokenManager AccessTokenManager) (client *Client) {
	return &Client{
		AccessTokenManager: AccessTokenManager,
		HttpClient:         http.DefaultClient,
	}
}

type Client struct {
	AccessTokenManager AccessTokenManager
	HttpClient         *http.Client
}

// CurlDo 执行 请求
func (client *Client) CurlDo(method string, methodUrl string, paramsMap map[string]interface{}) (resp []byte, err error) {
	// 添加 access_token
	accessToken, err := client.AccessTokenManager.GetAccessToken()
	if err != nil {
		return
	}
	params := make(map[string]interface{})
	params["access_token"] = accessToken

	for k, v := range paramsMap {
		params[k] = v
	}

	str, _ := json.Marshal(params)
	payload := strings.NewReader(string(str))

	req, _ := http.NewRequest(method, methodUrl, payload)

	// 检测URL
	if !strings.HasPrefix(req.URL.String(), "http") {
		parse, _ := url.Parse(ServerUrl)
		req.URL.Host = parse.Host
		req.URL.Scheme = parse.Scheme
	}

	// 默认 Header Content-Type
	if req.Method == http.MethodPost && req.Header.Get("Content-Type") == "" {
		req.Header.Set("Content-Type", contentTypeApplicationJson)
	}

	response, err := client.HttpClient.Do(req)
	if err != nil {
		return
	}
	defer response.Body.Close()

	resp, err = ioutil.ReadAll(response.Body)
	if err != nil {
		return
	}

	return
}

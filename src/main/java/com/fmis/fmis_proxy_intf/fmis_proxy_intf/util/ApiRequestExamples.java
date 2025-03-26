package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

/**
 * This class contains examples of API requests for different use cases.
 * These examples demonstrate how to send requests to the FMIS Proxy Interface API in various programming languages and tools.
 */
public class ApiRequestExamples {

    /**
     * Example for creating a new partner in JSON format.
     * This is a sample payload for making a POST request to create a new partner.
     *
     * @cURL Example
     * @JavaScript (Fetch API) Example
     * @Python (requests) Example
     * @Java (HttpURLConnection) Example
     * @C# (HttpClient) Example
     * @PHP (cURL) Example
     * @Node.js (Axios) Example
     * @Ruby (Net::HTTP) Example
     * @Go (net/http) Example
     */
    public static final String CREATE_PARTNER_EXAMPLE = """
            {
                "code": "PART123",
                "name": "Test Partner"
            }
            """;

    /**
     * cURL Example for creating a new partner.
     * This shows how to use cURL to send a POST request with the partner data in JSON format.
     */
    public static final String CREATE_PARTNER_CURL = """
            curl -X POST "https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner" \\
            -H "Content-Type: application/json" \\
            -d '{
                "code": "PART123",
                "name": "Test Partner"
            }'
            """;

    /**
     * JavaScript (Fetch) Example for creating a new partner.
     * This demonstrates how to use the Fetch API in JavaScript to send a POST request with JSON data.
     */
    public static final String CREATE_PARTNER_JS_FETCH = """
            fetch('https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    code: 'PART123',
                    name: 'Test Partner'
                })
            })
            .then(response => response.json())
            .then(data => console.log(data));
            """;

    /**
     * Python (requests) Example for creating a new partner
     * This example demonstrates how to send a POST request to the API using the Python `requests` library.
     * It includes the URL, headers, and body data, and prints the JSON response from the server.
     */
    public static final String CREATE_PARTNER_PYTHON = """
            import requests
            
            url = 'https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner'
            headers = {'Content-Type': 'application/json'}
            data = {
                'code': 'PART123',
                'name': 'Test Partner'
            }
            
            response = requests.post(url, json=data, headers=headers)
            print(response.json())
            """;

    /**
     * Java (OkHttp) Example for creating a new partner
     * This example demonstrates how to send a POST request using the OkHttp library in Java.
     * It sets the request body, adds the necessary headers, and executes the request to the server.
     */
    public static final String CREATE_PARTNER_JAVA_OKHTTP = """
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\\"code\\":\\"PART123\\",\\"name\\":\\"Test Partner\\"}");
            
            Request request = new Request.Builder()
                .url("https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
            
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
            """;

    /**
     * C# (HttpClient) Example for creating a new partner
     * This example shows how to use C#'s `HttpClient` to send a POST request with JSON data.
     * It asynchronously sends the request, waits for the response, and then prints the result.
     */
    public static final String CREATE_PARTNER_CSHARP = """
            using System;
            using System.Net.Http;
            using System.Text;
            using System.Threading.Tasks;
            
            class Program
            {
                static async Task Main()
                {
                    using HttpClient client = new HttpClient();
                    string url = "https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner";
            
                    var json = "{\\\"code\\\":\\\"PART123\\\",\\\"name\\\":\\\"Test Partner\\\"}";
                    var content = new StringContent(json, Encoding.UTF8, "application/json");
            
                    HttpResponseMessage response = await client.PostAsync(url, content);
                    string result = await response.Content.ReadAsStringAsync();
                    Console.WriteLine(result);
                }
            }
            """;

    /**
     * PHP (cURL) Example for creating a new partner
     * This example shows how to send a POST request in PHP using cURL.
     * The data is encoded as JSON, the necessary headers are set, and the response is echoed.
     */
    public static final String CREATE_PARTNER_PHP_CURL = """
            <?php
            $url = 'https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner';
            $data = json_encode(['code' => 'PART123', 'name' => 'Test Partner']);
            
            $ch = curl_init($url);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
            
            $response = curl_exec($ch);
            curl_close($ch);
            
            echo $response;
            ?>
            """;

    /**
     * Node.js (Axios) Example for creating a new partner
     * This example shows how to send a POST request using the `Axios` library in Node.js.
     * It sets the request body with the partner's data, sends the request, and logs the response.
     */
    public static final String CREATE_PARTNER_NODEJS = """
            const axios = require('axios');
            const url = 'https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner';
            const data = { code: 'PART123', name: 'Test Partner' };
            
            axios.post(url, data, { headers: { 'Content-Type': 'application/json' } })
                .then(response => console.log(response.data))
                .catch(error => console.error(error));
            """;

    /**
     * Ruby (Net::HTTP) Example for creating a new partner
     * This example demonstrates how to send a POST request in Ruby using the `Net::HTTP` library.
     * It prepares the request, sets the necessary headers, sends the request, and prints the response body.
     */
    public static final String CREATE_PARTNER_RUBY = """
            require 'net/http'
            require 'json'

            url = URI('https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner')
            http = Net::HTTP.new(url.host, url.port)
            http.use_ssl = true
            
            request = Net::HTTP::Post.new(url, { 'Content-Type' => 'application/json' })
            request.body = { code: 'PART123', name: 'Test Partner' }.to_json
            
            response = http.request(request)
            puts response.body
            """;

    /**
     * Go (net/http) Example for creating a new partner
     * This example demonstrates how to send a POST request in Go using the `net/http` package.
     * The request body is encoded as JSON, and the response status is printed after the request is executed.
     */
    public static final String CREATE_PARTNER_GO = """
            package main
            
            import (
                "bytes"
                "encoding/json"
                "fmt"
                "net/http"
            )
            
            func main() {
                url := "https://dev-fmis-intf.fmis.gov.kh/api/v1/create-partner"
                data := map[string]string{"code": "PART123", "name": "Test Partner"}
                jsonData, _ := json.Marshal(data)
            
                req, _ := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
                req.Header.Set("Content-Type", "application/json")
            
                client := &http.Client{}
                resp, _ := client.Do(req)
                defer resp.Body.Close()
            
                fmt.Println(resp.Status)
            }
            """;

    /**
     * Example for getting all partners with pagination
     *
     * @cURL
     * @JavaScript (Fetch API)
     * @Python (requests)
     * @Java (HttpURLConnection)
     * @C# (HttpClient)
     * @PHP (cURL)
     * @Node.js (Axios)
     * @Ruby (Net::HTTP)
     * @Go (net/http)
     */
    public static final String GET_ALL_PARTNERS_CURL = """
            curl -X GET "https://your-api-url/list-partner?page=1&size=10"
            """;

    /**
     * JavaScript (Fetch) Example for getting all partners with pagination
     * This example shows how to use the Fetch API in JavaScript to send a GET request to the API.
     * The query parameters for pagination (`page=1&size=10`) are passed in the URL.
     */
    public static final String GET_ALL_PARTNERS_JS_FETCH = """
            fetch('https://your-api-url/list-partner?page=1&size=10')
                .then(response => response.json())
                .then(data => console.log(data));
            """;

    /**
     * Python (requests) Example for getting all partners with pagination
     * This example demonstrates how to send a GET request using the Python `requests` library with query parameters.
     * It includes pagination parameters (`page=1&size=10`) and prints the JSON response from the API.
     */
    public static final String GET_ALL_PARTNERS_PYTHON = """
            import requests
            
            url = 'https://your-api-url/list-partner'
            params = {'page': 1, 'size': 10}
            
            response = requests.get(url, params=params)
            print(response.json())
            """;

    /**
     * Java (HttpURLConnection) Example for getting all partners with pagination
     * This example demonstrates how to send a GET request in Java using `HttpURLConnection`.
     * It passes pagination parameters (`page=1&size=10`) in the URL and prints the response.
     */
    public static final String GET_ALL_PARTNERS_JAVA_HTTPURLConnection = """
            import java.net.HttpURLConnection;
            import java.net.URL;
            import java.io.BufferedReader;
            import java.io.InputStreamReader;
            
            public class Main {
                public static void main(String[] args) throws Exception {
                    URL url = new URL("https://your-api-url/list-partner?page=1&size=10");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
            
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
            
                    System.out.println(response.toString());
                }
            }
            """;

    /**
     * C# (HttpClient) Example for getting all partners with pagination
     * This example shows how to send a GET request in C# using `HttpClient` to fetch paginated data.
     * The pagination parameters are passed as query parameters in the URL (`page=1&size=10`).
     */
    public static final String GET_ALL_PARTNERS_CSHARP = """
            using System;
            using System.Net.Http;
            using System.Threading.Tasks;
            
            class Program
            {
                static async Task Main()
                {
                    using HttpClient client = new HttpClient();
                    string url = "https://your-api-url/list-partner?page=1&size=10";
            
                    HttpResponseMessage response = await client.GetAsync(url);
                    string result = await response.Content.ReadAsStringAsync();
                    Console.WriteLine(result);
                }
            }
            """;

    /**
     * PHP (cURL) Example for getting all partners with pagination
     * This example demonstrates how to use PHP's `cURL` to send a GET request to fetch partners with pagination.
     * It adds query parameters (`page=1&size=10`) to the URL and prints the response.
     */
    public static final String GET_ALL_PARTNERS_PHP_CURL = """
            <?php
            $url = 'https://your-api-url/list-partner?page=1&size=10';
            $ch = curl_init($url);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            
            $response = curl_exec($ch);
            curl_close($ch);
            
            echo $response;
            ?>
            """;

    /**
     * Node.js (Axios) Example for getting all partners with pagination
     * This example shows how to use `Axios` in Node.js to send a GET request with pagination parameters.
     * The `params` object contains `page=1` and `size=10`, which are passed in the URL for the request.
     */
    public static final String GET_ALL_PARTNERS_NODEJS = """
            const axios = require('axios');
            const url = 'https://your-api-url/list-partner';
            const params = { page: 1, size: 10 };
            
            axios.get(url, { params })
                .then(response => console.log(response.data))
                .catch(error => console.error(error));
            """;

    /**
     * Ruby (Net::HTTP) Example for getting all partners with pagination
     * This example demonstrates how to send a GET request in Ruby using `Net::HTTP`.
     * It includes query parameters (`page=1&size=10`) for pagination and prints the response body.
     */
    public static final String GET_ALL_PARTNERS_RUBY = """
            require 'net/http'
            require 'uri'
            require 'json'
            
            url = URI.parse('https://your-api-url/list-partner?page=1&size=10')
            response = Net::HTTP.get_response(url)
            
            puts response.body
            """;

    /**
     * Go (net/http) Example for getting all partners with pagination
     * This example shows how to use the `net/http` package in Go to send a GET request with pagination.
     * The query parameters (`page=1&size=10`) are added to the URL, and the response status is printed.
     */
    public static final String GET_ALL_PARTNERS_GO = """
            package main
            
            import (
                "fmt"
                "net/http"
                "net/url"
            )
            
            func main() {
                baseURL := "https://your-api-url/list-partner"
                params := url.Values{}
                params.Add("page", "1")
                params.Add("size", "10")
            
                resp, err := http.Get(baseURL + "?" + params.Encode())
                if err != nil {
                    fmt.Println("Error:", err)
                    return
                }
                defer resp.Body.Close()
            
                fmt.Println("Response Status:", resp.Status)
            }
            """;

    /**
     * Example for Import Bank Statement endpoint
     *
     * @cURL
     * @JavaScript (Fetch API)
     * @Python (requests)
     * @Java (HttpURLConnection)
     * @C# (HttpClient)
     * @PHP (cURL)
     * @Node.js (Axios)
     * @Ruby (Net::HTTP)
     * @Go (net/http)
     */
    public static final String IMPORT_BANK_STATEMENT_EXAMPLE = """
            {
                "data": {
                    "CMB_BANKSTM_STG": [
                        {
                            "CMB_BSP_STMT_DT": "2022-12-31",
                            "CMB_BANK_ACCOUNT_N": "String",
                            "CMB_CURRENCY_CD": "String",
                            "CMB_VALUE_DT": "2022-12-31",
                            "CMB_BANK_STMT_TYPE": "String",
                            "CMB_BSP_TRAN_AMT": 2000.0,
                            "CMB_OPEN_BALANCE": 0.0,
                            "CMB_END_BALANCE": 0.0,
                            "CMB_IMMEDIATE_BAL": 2000.0,
                            "CMB_RECON_REF_ID": "String",
                            "CMB_CHECK_NUMBER": "String",
                            "CMB_DESCRLONG": "String",
                            "CMB_LETTER_NUMBER": "String"
                        },
                        {
                            "CMB_BSP_STMT_DT": "2022-12-31",
                            "CMB_BANK_ACCOUNT_N": "String",
                            "CMB_CURRENCY_CD": "String",
                            "CMB_VALUE_DT": "2022-12-31",
                            "CMB_BANK_STMT_TYPE": "String",
                            "CMB_BSP_TRAN_AMT": 2000.0,
                            "CMB_OPEN_BALANCE": 0.0,
                            "CMB_END_BALANCE": 0.0,
                            "CMB_IMMEDIATE_BAL": 0.0,
                            "CMB_RECON_REF_ID": "String",
                            "CMB_CHECK_NUMBER": "String",
                            "CMB_DESCRLONG": "String",
                            "CMB_LETTER_NUMBER": "String"
                        }
                    ]
                }
            }
            """;

    // cURL Example for Import Bank Statement
    public static final String IMPORT_BANK_STATEMENT_CURL = """
            curl -X POST "https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement" \\
            -H "Content-Type: application/json" \\
            -H "X-Partner-Token: String" \\
            -d '{
                "data": {
                    "CMB_BANKSTM_STG": [
                        {
                            "CMB_BSP_STMT_DT": "2022-12-31",
                            "CMB_BANK_ACCOUNT_N": "String",
                            "CMB_CURRENCY_CD": "String",
                            "CMB_VALUE_DT": "2022-12-31",
                            "CMB_BANK_STMT_TYPE": "String",
                            "CMB_BSP_TRAN_AMT": 2000.0,
                            "CMB_OPEN_BALANCE": 0.0,
                            "CMB_END_BALANCE": 0.0,
                            "CMB_IMMEDIATE_BAL": 2000.0,
                            "CMB_RECON_REF_ID": "String",
                            "CMB_CHECK_NUMBER": "String",
                            "CMB_DESCRLONG": "String",
                            "CMB_LETTER_NUMBER": "String"
                        },
                        {
                            "CMB_BSP_STMT_DT": "2022-12-31",
                            "CMB_BANK_ACCOUNT_N": "String",
                            "CMB_CURRENCY_CD": "String",
                            "CMB_VALUE_DT": "2022-12-31",
                            "CMB_BANK_STMT_TYPE": "String"
                            "CMB_BSP_TRAN_AMT": 2000.0,
                            "CMB_OPEN_BALANCE": 0.0,
                            "CMB_END_BALANCE": 0.0,
                            "CMB_IMMEDIATE_BAL": 0.0,
                            "CMB_RECON_REF_ID": "String",
                            "CMB_CHECK_NUMBER": "String",
                            "CMB_DESCRLONG": "String",
                            "CMB_LETTER_NUMBER": "String"
                        }
                    ]
                }
            }'
            """;

    /**
     * JavaScript (Fetch) Example for Import Bank Statement
     * This example shows how to use the Fetch API in JavaScript to send a POST request to import a bank statement.
     * The request includes the necessary headers and body, which is formatted as JSON, containing bank statement information.
     * The response is logged to the console in JSON format for easy inspection.
     */
    public static final String IMPORT_BANK_STATEMENT_JS_FETCH = """
            fetch('https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Partner-Token': 'String'  // Replace with actual partner token
                },
                body: JSON.stringify({
                    data: {
                        CMB_BANKSTM_STG: [
                            {
                                CMB_BSP_STMT_DT: '2022-12-31',
                                CMB_BANK_ACCOUNT_N: 'String',
                                CMB_CURRENCY_CD: 'String',
                                CMB_VALUE_DT: '2022-12-31',
                                CMB_BANK_STMT_TYPE: 'String',
                                CMB_BSP_TRAN_AMT: 2000.0,
                                CMB_OPEN_BALANCE: 0.0,
                                CMB_END_BALANCE: 0.0,
                                CMB_IMMEDIATE_BAL: 2000.0,
                                CMB_RECON_REF_ID: 'String',
                                CMB_CHECK_NUMBER: 'String',
                                CMB_DESCRLONG: 'String',
                                CMB_LETTER_NUMBER: 'String'
                            },
                            {
                                CMB_BSP_STMT_DT: '2022-12-31',
                                CMB_BANK_ACCOUNT_N: 'String',
                                CMB_CURRENCY_CD: 'String',
                                CMB_VALUE_DT: '2022-12-31',
                                CMB_BANK_STMT_TYPE: 'String',
                                CMB_BSP_TRAN_AMT: 2000.0,
                                CMB_OPEN_BALANCE: 0.0,
                                CMB_END_BALANCE: 0.0,
                                CMB_IMMEDIATE_BAL: 0.0,
                                CMB_RECON_REF_ID: 'String',
                                CMB_CHECK_NUMBER: 'String',
                                CMB_DESCRLONG: 'String',
                                CMB_LETTER_NUMBER: 'String'
                            }
                        ]
                    }
                })
            })
            .then(response => response.json())
            .then(data => console.log(data));
            """;

    /**
     * Python (requests) Example for Import Bank Statement
     * This Python example uses the `requests` library to send a POST request for importing a bank statement.
     * It sends the required headers, such as `Content-Type` and `X-Partner-Token`, along with the JSON body containing the bank statement data.
     * The response is then printed out as a JSON object.
     */
    public static final String IMPORT_BANK_STATEMENT_PYTHON = """
            import requests
            
            url = 'https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement'
            headers = {
                'Content-Type': 'application/json',
                'X-Partner-Token': 'String'  // Replace with actual partner token
            }
            data = {
                'data': {
                    'CMB_BANKSTM_STG': [
                        {
                            'CMB_BSP_STMT_DT': '2022-12-31',
                            'CMB_BANK_ACCOUNT_N': 'String',
                            'CMB_CURRENCY_CD': 'String',
                            'CMB_VALUE_DT': '2022-12-31',
                            'CMB_BANK_STMT_TYPE': 'String',
                            'CMB_BSP_TRAN_AMT': 2000.0,
                            'CMB_OPEN_BALANCE': 0.0,
                            'CMB_END_BALANCE': 0.0,
                            'CMB_IMMEDIATE_BAL': 2000.0,
                            'CMB_RECON_REF_ID': 'String',
                            'CMB_CHECK_NUMBER': 'String',
                            'CMB_DESCRLONG': 'String',
                            'CMB_LETTER_NUMBER': 'String'
                        },
                        {
                            'CMB_BSP_STMT_DT': '2022-12-31',
                            'CMB_BANK_ACCOUNT_N': 'String',
                            'CMB_CURRENCY_CD': 'String',
                            'CMB_VALUE_DT': '2022-12-31',
                            'CMB_BANK_STMT_TYPE': 'String',
                            'CMB_BSP_TRAN_AMT': 2000.0,
                            'CMB_OPEN_BALANCE': 0.0,
                            'CMB_END_BALANCE': 0.0,
                            'CMB_IMMEDIATE_BAL': 0.0,
                            'CMB_RECON_REF_ID': 'String',
                            'CMB_CHECK_NUMBER': 'String',
                            'CMB_DESCRLONG': 'String',
                            'CMB_LETTER_NUMBER': 'String'
                        }
                    ]
                }
            }
            
            response = requests.post(url, json=data, headers=headers)
            print(response.json())
            """;

    /**
     * Java (HttpURLConnection) Example for Import Bank Statement
     * This Java example demonstrates how to send a POST request using `HttpURLConnection` to import a bank statement.
     * The request includes headers such as `Content-Type` and `X-Partner-Token`, and the body contains JSON data for the bank statement.
     * The response is read and printed to the console.
     */
    public static final String IMPORT_BANK_STATEMENT_JAVA_HTTPURLCONNECTION = """
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.io.BufferedReader;
        import java.io.InputStreamReader;

        public class Main {
            public static void main(String[] args) throws Exception {
                String url = "https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement";
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("X-Partner-Token", "String"); // Replace with actual partner token
                con.setDoOutput(true);
        
                String jsonData = "{\n" +
                    "\"data\": {\n" +
                    "\"CMB_BANKSTM_STG\": [\n" +
                    "  {\n" +
                    "    \"CMB_BSP_STMT_DT\": \"2022-12-31\",\n" +
                    "    \"CMB_BANK_ACCOUNT_N\": \"String\",\n" +
                    "    \"CMB_CURRENCY_CD\": \"String\",\n" +
                    "    \"CMB_VALUE_DT\": \"2022-12-31\",\n" +
                    "    \"CMB_BANK_STMT_TYPE\": \"String\",\n" +
                    "    \"CMB_BSP_TRAN_AMT\": 2000.0,\n" +
                    "    \"CMB_OPEN_BALANCE\": 0.0,\n" +
                    "    \"CMB_END_BALANCE\": 0.0,\n" +
                    "    \"CMB_IMMEDIATE_BAL\": 2000.0,\n" +
                    "    \"CMB_RECON_REF_ID\": \"String\",\n" +
                    "    \"CMB_CHECK_NUMBER\": \"String\",\n" +
                    "    \"CMB_DESCRLONG\": \"String\",\n" +
                    "    \"CMB_LETTER_NUMBER\": \"String\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"CMB_BSP_STMT_DT\": \"2022-12-31\",\n" +
                    "    \"CMB_BANK_ACCOUNT_N\": \"String\",\n" +
                    "    \"CMB_CURRENCY_CD\": \"String\",\n" +
                    "    \"CMB_VALUE_DT\": \"2022-12-31\",\n" +
                    "    \"CMB_BANK_STMT_TYPE\": \"String\",\n" +
                    "    \"CMB_BSP_TRAN_AMT\": 2000.0,\n" +
                    "    \"CMB_OPEN_BALANCE\": 0.0,\n" +
                    "    \"CMB_END_BALANCE\": 0.0,\n" +
                    "    \"CMB_IMMEDIATE_BAL\": 0.0,\n" +
                    "    \"CMB_RECON_REF_ID\": \"String\",\n" +
                    "    \"CMB_CHECK_NUMBER\": \"String\",\n" +
                    "    \"CMB_DESCRLONG\": \"String\",\n" +
                    "    \"CMB_LETTER_NUMBER\": \"String\"\n" +
                    "  }\n" +
                    "]\n" +
                    "}\n" +
                    "}";
       
                con.getOutputStream().write(jsonData.getBytes("UTF-8"));

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println(response.toString());
            }
        }
        """;

    /**
     * C# (HttpClient) Example for Import Bank Statement
     * This C# example demonstrates how to use `HttpClient` to send a POST request to import a bank statement.
     * It includes the necessary headers (`Content-Type`, `X-Partner-Token`) and sends the data in JSON format.
     * The response is captured and printed as a string.
     */
    public static final String IMPORT_BANK_STATEMENT_CSHARP_HTTPCLIENT = """
        using System;
                using System.Net.Http;
                using System.Text;
                using System.Threading.Tasks;
    
                class Program
                {
                    static async Task Main()
                    {
                        using HttpClient client = new HttpClient();
                        string url = "https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement";
                        client.DefaultRequestHeaders.Add("X-Partner-Token", "String");  // Replace with actual partner token
                        var json = ""\"
                            {
                                "data": {
                                    "CMB_BANKSTM_STG": [
                                        {
                                            "CMB_BSP_STMT_DT": "2022-12-31",
                                            "CMB_BANK_ACCOUNT_N": "String",
                                            "CMB_CURRENCY_CD": "String",
                                            "CMB_VALUE_DT": "2022-12-31",
                                            "CMB_BANK_STMT_TYPE": "String",
                                            "CMB_BSP_TRAN_AMT": 2000.0,
                                            "CMB_OPEN_BALANCE": 0.0,
                                            "CMB_END_BALANCE": 0.0,
                                            "CMB_IMMEDIATE_BAL": 2000.0,
                                            "CMB_RECON_REF_ID": "String",
                                            "CMB_CHECK_NUMBER": "String",
                                            "CMB_DESCRLONG": "String",
                                            "CMB_LETTER_NUMBER": "String"
                                        },
                                        {
                                            "CMB_BSP_STMT_DT": "2022-12-31",
                                            "CMB_BANK_ACCOUNT_N": "String",
                                            "CMB_CURRENCY_CD": "String",
                                            "CMB_VALUE_DT": "2022-12-31",
                                            "CMB_BANK_STMT_TYPE": "String",
                                            "CMB_BSP_TRAN_AMT": 2000.0,
                                            "CMB_OPEN_BALANCE": 0.0,
                                            "CMB_END_BALANCE": 0.0,
                                            "CMB_IMMEDIATE_BAL": 0.0,
                                            "CMB_RECON_REF_ID": "String",
                                            "CMB_CHECK_NUMBER": "String",
                                            "CMB_DESCRLONG": "String",
                                            "CMB_LETTER_NUMBER": "String"
                                        }
                                    ]
                                }
                            }
                        ""\";
                       \s
                        var content = new StringContent(json, Encoding.UTF8, "application/json");
                        var response = await client.PostAsync(url, content);
                        var result = await response.Content.ReadAsStringAsync();
                        Console.WriteLine(result);
                    }
                }
    """;

    /**
     * This PHP example uses cURL to send a POST request to import a bank statement.
     * It sets the necessary headers (`Content-Type` and `X-Partner-Token`) and sends the JSON data containing bank statement information.
     * The response from the server is printed out.
     */
    public static final String IMPORT_BANK_STATEMENT_PHP_CURL = """
            <?php
                $url = 'https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement';
                $headers = [
                    'Content-Type: application/json',
                    'X-Partner-Token: String'  // Replace with actual partner token
                ];
                $data = json_encode([
                    'data' => [
                        'CMB_BANKSTM_STG' => [
                            [
                                'CMB_BSP_STMT_DT' => '2022-12-31',
                                'CMB_BANK_ACCOUNT_N' => 'String',
                                'CMB_CURRENCY_CD' => 'String',
                                'CMB_VALUE_DT' => '2022-12-31',
                                'CMB_BANK_STMT_TYPE' => 'String',
                                'CMB_BSP_TRAN_AMT' => 2000.0,
                                'CMB_OPEN_BALANCE' => 0.0,
                                'CMB_END_BALANCE' => 0.0,
                                'CMB_IMMEDIATE_BAL' => 2000.0,
                                'CMB_RECON_REF_ID' => 'String',
                                'CMB_CHECK_NUMBER' => 'String',
                                'CMB_DESCRLONG' => 'String',
                                'CMB_LETTER_NUMBER' => 'String'
                            ],
                            [
                                'CMB_BSP_STMT_DT' => '2022-12-31',
                                'CMB_BANK_ACCOUNT_N' => 'String',
                                'CMB_CURRENCY_CD' => 'String',
                                'CMB_VALUE_DT' => '2022-12-31',
                                'CMB_BANK_STMT_TYPE' => 'String',
                                'CMB_BSP_TRAN_AMT' => 2000.0,
                                'CMB_OPEN_BALANCE' => 0.0,
                                'CMB_END_BALANCE' => 0.0,
                                'CMB_IMMEDIATE_BAL' => 0.0,
                                'CMB_RECON_REF_ID' => 'String',
                                'CMB_CHECK_NUMBER' => 'String',
                                'CMB_DESCRLONG' => 'String',
                                'CMB_LETTER_NUMBER' => 'String'
                            ]
                        ]
                    ]
                ]);
                $ch = curl_init($url);
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
                curl_setopt($ch, CURLOPT_POST, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
    
                $response = curl_exec($ch);
                curl_close($ch);
    
                echo $response;
            ?>
    """;

    /**
     * This Node.js example uses Axios to send a POST request to import a bank statement.
     * It includes necessary headers (`Content-Type` and `X-Partner-Token`), and sends the data as JSON format.
     * The response data is logged to the console.
     */
    public static final String IMPORT_BANK_STATEMENT_NODEJS = """
        const axios = require('axios');
        const url = 'https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement';
        const headers = {
            'Content-Type': 'application/json',
            'X-Partner-Token': 'String'  // Replace with actual partner token
        };
        const data = {
            data: {
                CMB_BANKSTM_STG: [
                    {
                        CMB_BSP_STMT_DT: '2022-12-31',
                        CMB_BANK_ACCOUNT_N: 'String',
                        CMB_CURRENCY_CD: 'String',
                        CMB_VALUE_DT: '2022-12-31',
                        CMB_BANK_STMT_TYPE: 'String',
                        CMB_BSP_TRAN_AMT: 2000.0,
                        CMB_OPEN_BALANCE: 0.0,
                        CMB_END_BALANCE: 0.0,
                        CMB_IMMEDIATE_BAL: 2000.0,
                        CMB_RECON_REF_ID: 'String',
                        CMB_CHECK_NUMBER: 'String',
                        CMB_DESCRLONG: 'String',
                        CMB_LETTER_NUMBER: 'String'
                    },
                    {
                        CMB_BSP_STMT_DT: '2022-12-31',
                        CMB_BANK_ACCOUNT_N: 'String',
                        CMB_CURRENCY_CD: 'String',
                        CMB_VALUE_DT: '2022-12-31',
                        CMB_BANK_STMT_TYPE: 'String',
                        CMB_BSP_TRAN_AMT: 2000.0,
                        CMB_OPEN_BALANCE: 0.0,
                        CMB_END_BALANCE: 0.0,
                        CMB_IMMEDIATE_BAL: 0.0,
                        CMB_RECON_REF_ID: 'String',
                        CMB_CHECK_NUMBER: 'String',
                        CMB_DESCRLONG: 'String',
                        CMB_LETTER_NUMBER: 'String'
                    }
                ]
            }
        };

        axios.post(url, data, { headers })
            .then(response => console.log(response.data))
            .catch(error => console.error(error));
    """;

    /**
     * This Ruby example uses Net::HTTP to send a POST request to import a bank statement.
     * It includes headers (`Content-Type` and `X-Partner-Token`) and sends the data as JSON format.
     * The response body is printed out after the request completes.
     */
    public static final String IMPORT_BANK_STATEMENT_RUBY = """
        require 'net/http'
        require 'uri'
        require 'json'

        url = URI.parse('https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement')
        header = {'Content-Type' => 'application/json', 'X-Partner-Token' => 'String'}  # Replace with actual partner token
        data = {
            'data' => {
                'CMB_BANKSTM_STG' => [
                    {
                        'CMB_BSP_STMT_DT' => '2022-12-31',
                        'CMB_BANK_ACCOUNT_N' => 'String',
                        'CMB_CURRENCY_CD' => 'String',
                        'CMB_VALUE_DT' => '2022-12-31',
                        'CMB_BANK_STMT_TYPE' => 'String',
                        'CMB_BSP_TRAN_AMT' => 2000.0,
                        'CMB_OPEN_BALANCE' => 0.0,
                        'CMB_END_BALANCE' => 0.0,
                        'CMB_IMMEDIATE_BAL' => 2000.0,
                        'CMB_RECON_REF_ID' => 'String',
                        'CMB_CHECK_NUMBER' => 'String',
                        'CMB_DESCRLONG' => 'String',
                        'CMB_LETTER_NUMBER' => 'String'
                    },
                    {
                        'CMB_BSP_STMT_DT' => '2022-12-31',
                        'CMB_BANK_ACCOUNT_N' => 'String',
                        'CMB_CURRENCY_CD' => 'String',
                        'CMB_VALUE_DT' => '2022-12-31',
                        'CMB_BANK_STMT_TYPE' => 'String',
                        'CMB_BSP_TRAN_AMT' => 2000.0,
                        'CMB_OPEN_BALANCE' => 0.0,
                        'CMB_END_BALANCE' => 0.0,
                        'CMB_IMMEDIATE_BAL' => 0.0,
                        'CMB_RECON_REF_ID' => 'String',
                        'CMB_CHECK_NUMBER' => 'String',
                        'CMB_DESCRLONG' => 'String',
                        'CMB_LETTER_NUMBER' => 'String'
                    }
                ]
            }
        }.to_json

        uri = URI.parse(url)
        request = Net::HTTP::Post.new(uri, header)
        request.body = data

        response = Net::HTTP.start(uri.hostname, uri.port) { |http| http.request(request) }
        puts response.body
    """;

    /**
     * This Go example uses the net/http package to send a POST request to import a bank statement.
     * The headers and data are set in the request, and the response status is printed out.
     */
    public static final String IMPORT_BANK_STATEMENT_GO = """
        package main
    
        import (
            "bytes"
            "fmt"
            "net/http"
            "log"
        )
    
        func main() {
            url := "https://dev-fmis-intf.fmis.gov.kh/api/v1/import-bank-statement"
            jsonData := []byte(`
            {
                "data": {
                    "CMB_BANKSTM_STG": [
                        {
                            "CMB_BSP_STMT_DT": "2022-12-31",
                            "CMB_BANK_ACCOUNT_N": "String",
                            "CMB_CURRENCY_CD": "String",
                            "CMB_VALUE_DT": "2022-12-31",
                            "CMB_BANK_STMT_TYPE": "String",
                            "CMB_BSP_TRAN_AMT": 2000.0,
                            "CMB_OPEN_BALANCE": 0.0,
                            "CMB_END_BALANCE": 0.0,
                            "CMB_IMMEDIATE_BAL": 2000.0,
                            "CMB_RECON_REF_ID": "String",
                            "CMB_CHECK_NUMBER": "String",
                            "CMB_DESCRLONG": "String",
                            "CMB_LETTER_NUMBER": "String"
                        },
                        {
                            "CMB_BSP_STMT_DT": "2022-12-31",
                            "CMB_BANK_ACCOUNT_N": "String",
                            "CMB_CURRENCY_CD": "String",
                            "CMB_VALUE_DT": "2022-12-31",
                            "CMB_BANK_STMT_TYPE": "String",
                            "CMB_BSP_TRAN_AMT": 2000.0,
                            "CMB_OPEN_BALANCE": 0.0,
                            "CMB_END_BALANCE": 0.0,
                            "CMB_IMMEDIATE_BAL": 0.0,
                            "CMB_RECON_REF_ID": "String",
                            "CMB_CHECK_NUMBER": "String",
                            "CMB_DESCRLONG": "String",
                            "CMB_LETTER_NUMBER": "String"
                        }
                    ]
                }
            }
            `)
    
            req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
            if err != nil {
                log.Fatal(err)
            }
    
            req.Header.Set("Content-Type", "application/json")
            req.Header.Set("X-Partner-Token", "String")  // Replace with actual partner token
    
            client := &http.Client{}
            resp, err := client.Do(req)
            if err != nil {
                log.Fatal(err)
            }
            defer resp.Body.Close()
    
            fmt.Println("Response status:", resp.Status)
        }
    """;

    /**
     * Fetches the example for a given endpoint.
     *
     * @param endpoint the API endpoint
     * @return the example as a JSON string, or "{}" if no example is found
     */
    public static String getExample(String endpoint) {
        switch (endpoint) {
            case "/api/v1/create-partner":
                return CREATE_PARTNER_EXAMPLE;

            case "/api/v1/list-partner":
                return GET_ALL_PARTNERS_CURL;
            default:
                return "{}";
        }
    }
}